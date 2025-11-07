package com.intellidesk.user.controller;

import com.intellidesk.common.core.constant.SecurityConstants;
import com.intellidesk.common.core.domain.Result;
import com.intellidesk.user.domain.dto.ChangePasswordRequest;
import com.intellidesk.user.domain.dto.LoginRequest;
import com.intellidesk.user.domain.dto.RegisterRequest;
import com.intellidesk.user.domain.vo.LoginResponse;
import com.intellidesk.user.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证 Controller
 *
 * @author IntelliDesk
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求: username={}", loginRequest.getUsername());
        LoginResponse response = authService.login(loginRequest);
        return Result.success("登录成功", response);
    }

    /**
     * 用户登出
     *
     * @param authorization Authorization Header
     * @return 响应
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        authService.logout(token);
        return Result.success("登出成功");
    }

    /**
     * 刷新Token
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        String newAccessToken = authService.refreshToken(refreshToken);
        return Result.success("刷新成功", newAccessToken);
    }

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 用户ID
     */
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("用户注册请求: username={}", registerRequest.getUsername());
        Long userId = authService.register(registerRequest);
        return Result.success("注册成功", userId);
    }

    /**
     * 修改密码
     *
     * @param userId  用户ID（从JWT Token中获取）
     * @param request 修改密码请求
     * @return 响应
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @RequestHeader(value = SecurityConstants.USER_ID, required = false) Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        log.info("修改密码请求: userId={}", userId);
        authService.changePassword(userId, request);
        return Result.success("密码修改成功");
    }
}
