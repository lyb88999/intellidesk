package com.intellidesk.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellidesk.common.core.constant.SecurityConstants;
import com.intellidesk.common.core.domain.Result;
import com.intellidesk.common.core.domain.ResultCode;
import com.intellidesk.common.redis.service.TokenBlacklistService;
import com.intellidesk.common.security.utils.JwtUtils;
import com.intellidesk.gateway.config.AuthProperties;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * JWT 认证过滤器
 *
 * @author IntelliDesk
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;
    private final TokenBlacklistService tokenBlacklistService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 如果认证未启用，直接放行
        if (!authProperties.getEnabled()) {
            return chain.filter(exchange);
        }

        // 检查是否在白名单中
        if (isWhitelist(path)) {
            log.debug("白名单路径，直接放行: {}", path);
            return chain.filter(exchange);
        }

        // 提取 Token
        String token = extractToken(request);

        // Token 为空
        if (!StringUtils.hasText(token)) {
            log.warn("Token 缺失: {}", path);
            return unauthorized(exchange.getResponse(), ResultCode.UNAUTHORIZED);
        }

        // 验证 Token
        try {
            if (!JwtUtils.validateToken(token)) {
                log.warn("Token 无效: {}", token.substring(0, Math.min(20, token.length())));
                return unauthorized(exchange.getResponse(), ResultCode.TOKEN_INVALID);
            }

            // 检查 Token 是否在黑名单中
            if (tokenBlacklistService.isBlacklisted(token)) {
                log.warn("Token 已失效（在黑名单中）: {}", token.substring(0, Math.min(20, token.length())));
                return unauthorized(exchange.getResponse(), ResultCode.TOKEN_INVALID);
            }

            // 解析 Token
            Claims claims = JwtUtils.parseToken(token);

            // 构建新的请求，添加用户信息到请求头
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(SecurityConstants.USER_ID, claims.get(SecurityConstants.USER_ID, String.class))
                    .header(SecurityConstants.USERNAME, claims.get(SecurityConstants.USERNAME, String.class))
                    .header(SecurityConstants.USER_TYPE, claims.get(SecurityConstants.USER_TYPE, String.class))
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

            log.debug("Token 验证成功: userId={}, path={}",
                    claims.get(SecurityConstants.USER_ID), path);

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            log.error("Token 验证失败: {}", e.getMessage());
            return unauthorized(exchange.getResponse(), ResultCode.TOKEN_INVALID);
        }
    }

    /**
     * 检查路径是否在白名单中
     *
     * @param path 请求路径
     * @return 是否在白名单
     */
    private boolean isWhitelist(String path) {
        for (String pattern : authProperties.getWhitelist()) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从请求中提取 Token
     *
     * @param request 请求
     * @return Token
     */
    private String extractToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(SecurityConstants.TOKEN_HEADER);

        if (StringUtils.hasText(authorization) && authorization.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return authorization.substring(SecurityConstants.TOKEN_PREFIX.length());
        }

        return null;
    }

    /**
     * 返回未授权响应
     *
     * @param response   响应
     * @param resultCode 响应码
     * @return Mono
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, ResultCode resultCode) {
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Result<?> result = Result.error(resultCode);

        try {
            byte[] bytes = objectMapper.writeValueAsString(result).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化失败: {}", e.getMessage());
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        // 在日志过滤器之后执行
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
