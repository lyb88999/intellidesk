package com.intellidesk.common.security.utils;

import com.intellidesk.common.core.constant.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 *
 * @author IntelliDesk
 */
@Slf4j
public class JwtUtils {

    /**
     * JWT 密钥（生产环境应该从配置中心读取）
     */
    private static final String SECRET_KEY = "IntelliDeskSecretKey2024ForJWTTokenGenerationAndValidation1234567890";

    /**
     * 获取签名密钥
     */
    private static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Access Token
     *
     * @param subject 主题（通常是用户 ID）
     * @param claims  自定义声明
     * @return Token
     */
    public static String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, SecurityConstants.ACCESS_TOKEN_EXPIRE * 1000);
    }

    /**
     * 生成 Refresh Token
     *
     * @param subject 主题（通常是用户 ID）
     * @return Token
     */
    public static String generateRefreshToken(String subject) {
        return generateToken(subject, null, SecurityConstants.REFRESH_TOKEN_EXPIRE * 1000);
    }

    /**
     * 生成 Token
     *
     * @param subject     主题
     * @param claims      自定义声明
     * @param expiration  过期时间（毫秒）
     * @return Token
     */
    private static String generateToken(String subject, Map<String, Object> claims, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        var builder = Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256);

        if (claims != null && !claims.isEmpty()) {
            builder.claims(claims);
        }

        return builder.compact();
    }

    /**
     * 解析 Token
     *
     * @param token Token
     * @return Claims
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析 Token 失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 从 Token 中获取用户 ID
     *
     * @param token Token
     * @return 用户 ID
     */
    public static String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token Token
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get(SecurityConstants.USERNAME, String.class);
    }

    /**
     * 从 Token 中获取声明
     *
     * @param token Token
     * @param key   声明键
     * @param clazz 类型
     * @return 声明值
     */
    public static <T> T getClaimFromToken(String token, String key, Class<T> clazz) {
        Claims claims = parseToken(token);
        return claims.get(key, clazz);
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token Token
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !isTokenExpired(claims);
        } catch (Exception e) {
            log.warn("Token 验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查 Token 是否过期
     *
     * @param claims Claims
     * @return 是否过期
     */
    private static boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 获取 Token 过期时间
     *
     * @param token Token
     * @return 过期时间
     */
    public static Date getExpirationDateFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * 刷新 Token（延长有效期）
     *
     * @param token Token
     * @return 新 Token
     */
    public static String refreshToken(String token) {
        Claims claims = parseToken(token);
        String subject = claims.getSubject();

        // 移除标准声明
        claims.remove("sub");
        claims.remove("iat");
        claims.remove("exp");

        return generateAccessToken(subject, claims);
    }
}
