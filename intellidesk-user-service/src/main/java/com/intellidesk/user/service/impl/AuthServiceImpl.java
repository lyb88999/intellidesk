package com.intellidesk.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.intellidesk.common.core.constant.SecurityConstants;
import com.intellidesk.common.core.domain.ResultCode;
import com.intellidesk.common.core.exception.BusinessException;
import com.intellidesk.common.core.utils.Assert;
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
        // TODO: 将Token加入黑名单（Redis）
        log.info("用户登出成功");
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
}
