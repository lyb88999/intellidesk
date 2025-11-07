package com.intellidesk.common.core.constant;

/**
 * 安全常量
 *
 * @author IntelliDesk
 */
public interface SecurityConstants {

    /**
     * Token 请求头
     */
    String TOKEN_HEADER = "Authorization";

    /**
     * Token 前缀
     */
    String TOKEN_PREFIX = "Bearer ";

    /**
     * Token 类型
     */
    String TOKEN_TYPE = "JWT";

    /**
     * 用户 ID
     */
    String USER_ID = "userId";

    /**
     * 用户名
     */
    String USERNAME = "username";

    /**
     * 用户类型
     */
    String USER_TYPE = "userType";

    /**
     * 角色集合
     */
    String ROLES = "roles";

    /**
     * 权限集合
     */
    String PERMISSIONS = "permissions";

    /**
     * 登录用户 Key
     */
    String LOGIN_USER_KEY = "login_user_key";

    /**
     * Access Token 过期时间（秒）- 2 小时
     */
    Long ACCESS_TOKEN_EXPIRE = 7200L;

    /**
     * Refresh Token 过期时间（秒）- 7 天
     */
    Long REFRESH_TOKEN_EXPIRE = 604800L;

    /**
     * Redis 用户信息 Key 前缀
     */
    String REDIS_USER_KEY = "login:user:";

    /**
     * Redis Token Key 前缀
     */
    String REDIS_TOKEN_KEY = "login:token:";

    /**
     * 验证码 Redis Key 前缀
     */
    String CAPTCHA_KEY = "captcha:";

    /**
     * 验证码有效期（分钟）
     */
    Integer CAPTCHA_EXPIRE = 5;

    /**
     * 超级管理员角色编码
     */
    String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    /**
     * 管理员角色编码
     */
    String ROLE_ADMIN = "ROLE_ADMIN";

    /**
     * 客服角色编码
     */
    String ROLE_AGENT = "ROLE_AGENT";

    /**
     * 客户角色编码
     */
    String ROLE_CUSTOMER = "ROLE_CUSTOMER";
}
