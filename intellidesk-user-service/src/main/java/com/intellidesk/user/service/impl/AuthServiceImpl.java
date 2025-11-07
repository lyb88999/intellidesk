package com.intellidesk.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.intellidesk.common.core.constant.SecurityConstants;
import com.intellidesk.common.core.domain.ResultCode;
import com.intellidesk.common.core.exception.BusinessException;
import com.intellidesk.common.core.utils.Assert;
import com.intellidesk.common.redis.service.TokenBlacklistService;
import com.intellidesk.common.security.utils.JwtUtils;
import com.intellidesk.common.security.utils.PasswordUtils;
import com.intellidesk.user.domain.dto.LoginRequest;
import com.intellidesk.user.domain.entity.SysUser;
import com.intellidesk.user.domain.vo.LoginResponse;
import com.intellidesk.user.mapper.SysUserMapper;
import com.intellidesk.user.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 认证服务实现类
 *
 * @author IntelliDesk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final SysUserMapper userMapper;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. 参数校验
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // 2. 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = userMapper.selectOne(wrapper);

        Assert.notNull(user, ResultCode.USER_NOT_FOUND);

        // 3. 验证密码
        if (!PasswordUtils.matches(password, user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 4. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 5. 查询用户角色和权限
        Set<String> roles = userMapper.selectRoleCodesByUserId(user.getId());
        Set<String> permissions = userMapper.selectPermissionCodesByUserId(user.getId());

        // 6. 生成JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.USER_ID, user.getId());
        claims.put(SecurityConstants.USERNAME, user.getUsername());
        claims.put(SecurityConstants.USER_TYPE, user.getUserType());
        claims.put(SecurityConstants.ROLES, roles);
        claims.put(SecurityConstants.PERMISSIONS, permissions);

        String accessToken = JwtUtils.generateAccessToken(user.getId().toString(), claims);
        String refreshToken = JwtUtils.generateRefreshToken(user.getId().toString());

        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());

        // 7. 构建响应
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(SecurityConstants.ACCESS_TOKEN_EXPIRE)
                .userInfo(LoginResponse.UserInfo.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .avatar(user.getAvatar())
                        .email(user.getEmail())
                        .userType(user.getUserType())
                        .roles(roles)
                        .permissions(permissions)
                        .build())
                .build();
    }

    @Override
    public void logout(String token) {
        // 将 Token 加入黑名单
        tokenBlacklistService.addToBlacklist(token);

        // 获取用户信息
        String userId = JwtUtils.getUserIdFromToken(token);
        log.info("用户登出成功: userId={}", userId);
    }

    @Override
    public String refreshToken(String refreshToken) {
        // 1. 验证Refresh Token
        if (!JwtUtils.validateToken(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        // 2. 获取用户ID
        String userId = JwtUtils.getUserIdFromToken(refreshToken);

        // 3. 查询用户
        SysUser user = userMapper.selectById(Long.parseLong(userId));
        Assert.notNull(user, ResultCode.USER_NOT_FOUND);

        // 4. 查询角色和权限
        Set<String> roles = userMapper.selectRoleCodesByUserId(user.getId());
        Set<String> permissions = userMapper.selectPermissionCodesByUserId(user.getId());

        // 5. 生成新的Access Token
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.USER_ID, user.getId());
        claims.put(SecurityConstants.USERNAME, user.getUsername());
        claims.put(SecurityConstants.USER_TYPE, user.getUserType());
        claims.put(SecurityConstants.ROLES, roles);
        claims.put(SecurityConstants.PERMISSIONS, permissions);

        return JwtUtils.generateAccessToken(user.getId().toString(), claims);
    }

    @Override
    public Long register(com.intellidesk.user.domain.dto.RegisterRequest registerRequest) {
        // 1. 校验用户名唯一性
        LambdaQueryWrapper<SysUser> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(SysUser::getUsername, registerRequest.getUsername());
        usernameWrapper.eq(SysUser::getDeleted, 0);
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 2. 校验邮箱唯一性
        LambdaQueryWrapper<SysUser> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(SysUser::getEmail, registerRequest.getEmail());
        emailWrapper.eq(SysUser::getDeleted, 0);
        if (userMapper.selectCount(emailWrapper) > 0) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }

        // 3. 校验手机号唯一性
        LambdaQueryWrapper<SysUser> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(SysUser::getPhone, registerRequest.getPhone());
        phoneWrapper.eq(SysUser::getDeleted, 0);
        if (userMapper.selectCount(phoneWrapper) > 0) {
            throw new BusinessException(ResultCode.PHONE_EXISTS);
        }

        // 4. 创建用户
        SysUser user = SysUser.builder()
                .username(registerRequest.getUsername())
                .password(PasswordUtils.encode(registerRequest.getPassword()))
                .nickname(registerRequest.getNickname())
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .userType(1) // 默认为客户
                .status(1)   // 默认启用
                .deleted(0)
                .build();

        userMapper.insert(user);
        log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());

        return user.getId();
    }

    @Override
    public void changePassword(Long userId, com.intellidesk.user.domain.dto.ChangePasswordRequest request) {
        // 1. 查询用户
        SysUser user = userMapper.selectById(userId);
        Assert.notNull(user, ResultCode.USER_NOT_FOUND);

        // 2. 验证旧密码
        if (!PasswordUtils.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "旧密码错误");
        }

        // 3. 更新密码
        SysUser updateEntity = new SysUser();
        updateEntity.setId(userId);
        updateEntity.setPassword(PasswordUtils.encode(request.getNewPassword()));
        userMapper.updateById(updateEntity);

        log.info("修改密码成功: userId={}", userId);
    }
}
