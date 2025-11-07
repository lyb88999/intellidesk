package com.intellidesk.user.controller;

import com.intellidesk.common.core.domain.Result;
import com.intellidesk.user.domain.dto.LoginRequest;
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
}
