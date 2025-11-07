package com.intellidesk.common.redis.service;

import com.intellidesk.common.security.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token 黑名单服务
 * 用于管理已失效的 JWT Token
 *
 * @author IntelliDesk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 将 Token 加入黑名单
     *
     * @param token JWT Token
     */
    public void addToBlacklist(String token) {
        try {
            // 获取 Token 剩余有效期
            long expireTime = JwtUtils.getExpireTime(token);
            long currentTime = System.currentTimeMillis();
            long ttl = expireTime - currentTime;

            if (ttl > 0) {
                String key = BLACKLIST_PREFIX + token;
                // 将 Token 加入黑名单，过期时间与 Token 一致
                stringRedisTemplate.opsForValue().set(key, "1", ttl, TimeUnit.MILLISECONDS);
                log.info("Token 已加入黑名单，剩余有效期: {}ms", ttl);
            } else {
                log.warn("Token 已过期，无需加入黑名单");
            }
        } catch (Exception e) {
            log.error("加入黑名单失败: {}", e.getMessage(), e);
            throw new RuntimeException("加入黑名单失败", e);
        }
    }

    /**
     * 检查 Token 是否在黑名单中
     *
     * @param token JWT Token
     * @return true-在黑名单中，false-不在黑名单中
     */
    public boolean isBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            Boolean exists = stringRedisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("检查黑名单失败: {}", e.getMessage(), e);
            // 发生异常时保守处理，认为不在黑名单中（避免影响正常用户）
            return false;
        }
    }

    /**
     * 从黑名单中移除 Token（一般不需要主动调用，Redis 会自动过期）
     *
     * @param token JWT Token
     */
    public void removeFromBlacklist(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            stringRedisTemplate.delete(key);
            log.info("Token 已从黑名单中移除");
        } catch (Exception e) {
            log.error("移除黑名单失败: {}", e.getMessage(), e);
        }
    }
}
