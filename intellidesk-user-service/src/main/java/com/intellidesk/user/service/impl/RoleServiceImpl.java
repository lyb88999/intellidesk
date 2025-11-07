package com.intellidesk.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.common.core.domain.ResultCode;
import com.intellidesk.common.core.exception.BusinessException;
import com.intellidesk.common.core.utils.Assert;
import com.intellidesk.user.domain.dto.RoleCreateRequest;
import com.intellidesk.user.domain.dto.RoleQueryRequest;
import com.intellidesk.user.domain.dto.RoleUpdateRequest;
import com.intellidesk.user.domain.entity.SysRole;
import com.intellidesk.user.domain.entity.SysRolePermission;
import com.intellidesk.user.domain.vo.RoleVO;
import com.intellidesk.user.mapper.SysRoleMapper;
import com.intellidesk.user.mapper.SysRolePermissionMapper;
import com.intellidesk.user.service.IRoleService;
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
 * 角色服务实现类
 *
 * @author IntelliDesk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public RoleVO getRoleById(Long roleId) {
        Assert.notNull(roleId, "角色ID不能为空");

        // 查询角色
        SysRole role = roleMapper.selectById(roleId);
        Assert.notNull(role, "角色不存在");

        // 检查是否已删除
        if (role.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 查询权限
        Set<String> permissions = roleMapper.selectPermissionCodesByRoleId(roleId);

        // 转换为VO
        return convertToVO(role, permissions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateRequest request) {
        // 1. 校验角色编码唯一性
        if (existsByRoleCode(request.getRoleCode())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "角色编码已存在");
        }

        // 2. 创建角色实体
        SysRole role = SysRole.builder()
                .roleName(request.getRoleName())
                .roleCode(request.getRoleCode())
                .description(request.getDescription())
                .status(request.getStatus())
                .sort(request.getSort())
                .deleted(0)
                .build();

        // 3. 插入角色
        roleMapper.insert(role);
        log.info("创建角色成功: roleId={}, roleCode={}", role.getId(), role.getRoleCode());

        // 4. 绑定权限
        if (!CollectionUtils.isEmpty(request.getPermissionIds())) {
            bindRolePermissions(role.getId(), request.getPermissionIds());
        }

        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long roleId, RoleUpdateRequest request) {
        // 1. 查询角色
        SysRole role = roleMapper.selectById(roleId);
        Assert.notNull(role, "角色不存在");

        // 2. 检查是否已删除
        if (role.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 3. 更新角色信息
        SysRole updateEntity = new SysRole();
        updateEntity.setId(roleId);
        if (StringUtils.hasText(request.getRoleName())) {
            updateEntity.setRoleName(request.getRoleName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            updateEntity.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            updateEntity.setStatus(request.getStatus());
        }
        if (request.getSort() != null) {
            updateEntity.setSort(request.getSort());
        }

        roleMapper.updateById(updateEntity);
        log.info("更新角色成功: roleId={}, roleCode={}", roleId, role.getRoleCode());

        // 4. 更新权限绑定（如果提供了权限列表）
        if (request.getPermissionIds() != null) {
            // 删除旧的权限绑定
            rolePermissionMapper.deleteByRoleId(roleId);
            // 绑定新的权限
            if (!CollectionUtils.isEmpty(request.getPermissionIds())) {
                bindRolePermissions(roleId, request.getPermissionIds());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        // 1. 查询角色
        SysRole role = roleMapper.selectById(roleId);
        Assert.notNull(role, "角色不存在");

        // 2. 逻辑删除
        SysRole updateEntity = new SysRole();
        updateEntity.setId(roleId);
        updateEntity.setDeleted(1);
        roleMapper.updateById(updateEntity);

        log.info("删除角色成功: roleId={}, roleCode={}", roleId, role.getRoleCode());
    }

    @Override
    public PageResult listRoles(RoleQueryRequest request) {
        // 1. 构建查询条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getDeleted, 0);

        if (StringUtils.hasText(request.getRoleName())) {
            wrapper.like(SysRole::getRoleName, request.getRoleName());
        }
        if (StringUtils.hasText(request.getRoleCode())) {
            wrapper.like(SysRole::getRoleCode, request.getRoleCode());
        }
        if (request.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, request.getStatus());
        }

        wrapper.orderByAsc(SysRole::getSort)
                .orderByDesc(SysRole::getCreateTime);

        // 2. 分页查询
        Page<SysRole> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<SysRole> result = roleMapper.selectPage(page, wrapper);

        // 3. 转换为VO
        List<RoleVO> voList = result.getRecords().stream()
                .map(role -> {
                    Set<String> permissions = roleMapper.selectPermissionCodesByRoleId(role.getId());
                    return convertToVO(role, permissions);
                })
                .collect(Collectors.toList());

        // 4. 构建分页结果
        return PageResult.of(voList, result.getTotal(), Long.valueOf(request.getPageNum()), Long.valueOf(request.getPageSize()));
    }

    @Override
    public List<RoleVO> listEnabledRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getDeleted, 0);
        wrapper.eq(SysRole::getStatus, 1);
        wrapper.orderByAsc(SysRole::getSort);

        List<SysRole> roles = roleMapper.selectList(wrapper);

        return roles.stream()
                .map(role -> {
                    Set<String> permissions = roleMapper.selectPermissionCodesByRoleId(role.getId());
                    return convertToVO(role, permissions);
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByRoleCode(String roleCode) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode);
        wrapper.eq(SysRole::getDeleted, 0);
        return roleMapper.selectCount(wrapper) > 0;
    }

    /**
     * 绑定角色权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     */
    private void bindRolePermissions(Long roleId, Set<Long> permissionIds) {
        List<SysRolePermission> rolePermissions = permissionIds.stream()
                .map(permissionId -> SysRolePermission.builder()
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .build())
                .collect(Collectors.toList());

        rolePermissionMapper.insertBatch(rolePermissions);
        log.info("绑定角色权限成功: roleId={}, permissionIds={}", roleId, permissionIds);
    }

    /**
     * 转换为VO对象
     *
     * @param role        角色实体
     * @param permissions 权限列表
     * @return RoleVO
     */
    private RoleVO convertToVO(SysRole role, Set<String> permissions) {
        return RoleVO.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .roleCode(role.getRoleCode())
                .description(role.getDescription())
                .status(role.getStatus())
                .sort(role.getSort())
                .permissions(permissions)
                .createTime(role.getCreateTime())
                .updateTime(role.getUpdateTime())
                .build();
    }
}
