package com.intellidesk.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.common.core.domain.ResultCode;
import com.intellidesk.common.core.exception.BusinessException;
import com.intellidesk.common.core.utils.Assert;
import com.intellidesk.common.security.utils.PasswordUtils;
import com.intellidesk.user.domain.dto.UserCreateRequest;
import com.intellidesk.user.domain.dto.UserQueryRequest;
import com.intellidesk.user.domain.dto.UserUpdateRequest;
import com.intellidesk.user.domain.entity.SysUser;
import com.intellidesk.user.domain.entity.SysUserRole;
import com.intellidesk.user.domain.vo.UserVO;
import com.intellidesk.user.mapper.SysUserMapper;
import com.intellidesk.user.mapper.SysUserRoleMapper;
import com.intellidesk.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author IntelliDesk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public UserVO getUserById(Long userId) {
        Assert.notNull(userId, "用户ID不能为空");

        // 查询用户
        SysUser user = userMapper.selectById(userId);
        Assert.notNull(user, ResultCode.USER_NOT_FOUND, "用户不存在");

        // 检查是否已删除
        if (user.getDeleted() == 1) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户已被删除");
        }

        // 查询角色和权限
        Set<String> roles = userMapper.selectRoleCodesByUserId(userId);
        Set<String> permissions = userMapper.selectPermissionCodesByUserId(userId);

        // 转换为VO
        return convertToVO(user, roles, permissions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateRequest request) {
        // 1. 校验用户名唯一性
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 2. 校验邮箱唯一性
        if (StringUtils.hasText(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }

        // 3. 校验手机号唯一性
        if (StringUtils.hasText(request.getPhone()) && existsByPhone(request.getPhone())) {
            throw new BusinessException(ResultCode.PHONE_EXISTS);
        }

        // 4. 创建用户实体
        SysUser user = SysUser.builder()
                .username(request.getUsername())
                .password(PasswordUtils.encode(request.getPassword()))
                .nickname(request.getNickname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .userType(request.getUserType())
                .avatar(request.getAvatar())
                .deptId(request.getDeptId())
                .skillGroupId(request.getSkillGroupId())
                .status(1) // 默认启用
                .deleted(0)
                .build();

        // 5. 插入用户
        userMapper.insert(user);
        log.info("创建用户成功: userId={}, username={}", user.getId(), user.getUsername());

        // 6. 绑定角色
        if (!CollectionUtils.isEmpty(request.getRoleIds())) {
            bindUserRoles(user.getId(), request.getRoleIds());
        }

        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long userId, UserUpdateRequest request) {
        // 1. 查询用户
        SysUser user = userMapper.selectById(userId);
        Assert.notNull(user, ResultCode.USER_NOT_FOUND, "用户不存在");

        // 2. 检查是否已删除
        if (user.getDeleted() == 1) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户已被删除");
        }

        // 3. 校验邮箱唯一性（如果修改了邮箱）
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (existsByEmail(request.getEmail())) {
                throw new BusinessException(ResultCode.EMAIL_EXISTS, "邮箱已被注册");
            }
        }

        // 4. 校验手机号唯一性（如果修改了手机号）
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
            if (existsByPhone(request.getPhone())) {
                throw new BusinessException(ResultCode.PHONE_EXISTS);
            }
        }

        // 5. 更新用户信息
        SysUser updateEntity = new SysUser();
        updateEntity.setId(userId);
        if (StringUtils.hasText(request.getNickname())) {
            updateEntity.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getEmail())) {
            updateEntity.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) {
            updateEntity.setPhone(request.getPhone());
        }
        if (request.getUserType() != null) {
            updateEntity.setUserType(request.getUserType());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            updateEntity.setAvatar(request.getAvatar());
        }
        if (request.getStatus() != null) {
            updateEntity.setStatus(request.getStatus());
        }
        if (request.getDeptId() != null) {
            updateEntity.setDeptId(request.getDeptId());
        }
        if (request.getSkillGroupId() != null) {
            updateEntity.setSkillGroupId(request.getSkillGroupId());
        }

        userMapper.updateById(updateEntity);
        log.info("更新用户成功: userId={}, username={}", userId, user.getUsername());

        // 6. 更新角色绑定（如果提供了角色列表）
        if (request.getRoleIds() != null) {
            // 删除旧的角色绑定
            userRoleMapper.deleteByUserId(userId);
            // 绑定新的角色
            if (!CollectionUtils.isEmpty(request.getRoleIds())) {
                bindUserRoles(userId, request.getRoleIds());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        // 1. 查询用户
        SysUser user = userMapper.selectById(userId);
        Assert.notNull(user, ResultCode.USER_NOT_FOUND, "用户不存在");

        // 2. 逻辑删除
        SysUser updateEntity = new SysUser();
        updateEntity.setId(userId);
        updateEntity.setDeleted(1);
        userMapper.updateById(updateEntity);

        log.info("删除用户成功: userId={}, username={}", userId, user.getUsername());
    }

    @Override
    public PageResult listUsers(UserQueryRequest request) {
        // 1. 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeleted, 0);

        if (StringUtils.hasText(request.getUsername())) {
            wrapper.like(SysUser::getUsername, request.getUsername());
        }
        if (StringUtils.hasText(request.getNickname())) {
            wrapper.like(SysUser::getNickname, request.getNickname());
        }
        if (StringUtils.hasText(request.getEmail())) {
            wrapper.like(SysUser::getEmail, request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) {
            wrapper.eq(SysUser::getPhone, request.getPhone());
        }
        if (request.getUserType() != null) {
            wrapper.eq(SysUser::getUserType, request.getUserType());
        }
        if (request.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, request.getStatus());
        }
        if (request.getDeptId() != null) {
            wrapper.eq(SysUser::getDeptId, request.getDeptId());
        }
        if (request.getSkillGroupId() != null) {
            wrapper.eq(SysUser::getSkillGroupId, request.getSkillGroupId());
        }

        wrapper.orderByDesc(SysUser::getCreateTime);

        // 2. 分页查询
        Page<SysUser> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<SysUser> result = userMapper.selectPage(page, wrapper);

        // 3. 转换为VO
        List<UserVO> voList = result.getRecords().stream()
                .map(user -> {
                    Set<String> roles = userMapper.selectRoleCodesByUserId(user.getId());
                    Set<String> permissions = userMapper.selectPermissionCodesByUserId(user.getId());
                    return convertToVO(user, roles, permissions);
                })
                .collect(Collectors.toList());

        // 4. 构建分页结果
        return PageResult.of(voList, result.getTotal(), Long.valueOf(request.getPageNum()), Long.valueOf(request.getPageSize()));
    }

    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        wrapper.eq(SysUser::getDeleted, 0);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, email);
        wrapper.eq(SysUser::getDeleted, 0);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByPhone(String phone) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, phone);
        wrapper.eq(SysUser::getDeleted, 0);
        return userMapper.selectCount(wrapper) > 0;
    }

    /**
     * 绑定用户角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    private void bindUserRoles(Long userId, Set<Long> roleIds) {
        List<SysUserRole> userRoles = roleIds.stream()
                .map(roleId -> SysUserRole.builder()
                        .userId(userId)
                        .roleId(roleId)
                        .build())
                .collect(Collectors.toList());

        userRoleMapper.insertBatch(userRoles);
        log.info("绑定用户角色成功: userId={}, roleIds={}", userId, roleIds);
    }

    /**
     * 转换为VO对象
     *
     * @param user        用户实体
     * @param roles       角色列表
     * @param permissions 权限列表
     * @return UserVO
     */
    private UserVO convertToVO(SysUser user, Set<String> roles, Set<String> permissions) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .status(user.getStatus())
                .deptId(user.getDeptId())
                .skillGroupId(user.getSkillGroupId())
                .roles(roles)
                .permissions(permissions)
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();
    }
}
