package com.intellidesk.user.service;

import com.intellidesk.user.domain.dto.LoginRequest;
import com.intellidesk.user.domain.vo.LoginResponse;

/**
 * 认证服务接口
 *
 * @author IntelliDesk
 */
public interface IAuthService {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 用户登出
     *
     * @param token Token
     */
    void logout(String token);

    /**
     * 刷新Token
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    String refreshToken(String refreshToken);
}
