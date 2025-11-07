package com.intellidesk.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.intellidesk.common.core.domain.ResultCode;
import com.intellidesk.common.core.exception.BusinessException;
import com.intellidesk.common.core.utils.Assert;
import com.intellidesk.user.domain.dto.PermissionCreateRequest;
import com.intellidesk.user.domain.dto.PermissionUpdateRequest;
import com.intellidesk.user.domain.entity.SysPermission;
import com.intellidesk.user.domain.vo.PermissionVO;
import com.intellidesk.user.mapper.SysPermissionMapper;
import com.intellidesk.user.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 *
 * @author IntelliDesk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements IPermissionService {

    private final SysPermissionMapper permissionMapper;

    @Override
    public PermissionVO getPermissionById(Long permissionId) {
        Assert.notNull(permissionId, "权限ID不能为空");

        // 查询权限
        SysPermission permission = permissionMapper.selectById(permissionId);
        Assert.notNull(permission, "权限不存在");

        // 检查是否已删除
        if (permission.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        return convertToVO(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(PermissionCreateRequest request) {
        // 1. 校验权限编码唯一性
        if (existsByPermissionCode(request.getPermissionCode())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "权限编码已存在");
        }

        // 2. 创建权限实体
        SysPermission permission = SysPermission.builder()
                .parentId(request.getParentId())
                .permissionName(request.getPermissionName())
                .permissionCode(request.getPermissionCode())
                .permissionType(request.getPermissionType())
                .path(request.getPath())
                .component(request.getComponent())
                .icon(request.getIcon())
                .sort(request.getSort())
                .status(request.getStatus())
                .deleted(0)
                .build();

        // 3. 插入权限
        permissionMapper.insert(permission);
        log.info("创建权限成功: permissionId={}, permissionCode={}", permission.getId(), permission.getPermissionCode());

        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(Long permissionId, PermissionUpdateRequest request) {
        // 1. 查询权限
        SysPermission permission = permissionMapper.selectById(permissionId);
        Assert.notNull(permission, "权限不存在");

        // 2. 检查是否已删除
        if (permission.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 3. 更新权限信息
        SysPermission updateEntity = new SysPermission();
        updateEntity.setId(permissionId);
        if (request.getParentId() != null) {
            // 检查是否会形成循环
            if (request.getParentId().equals(permissionId)) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "父权限不能是自己");
            }
            updateEntity.setParentId(request.getParentId());
        }
        if (StringUtils.hasText(request.getPermissionName())) {
            updateEntity.setPermissionName(request.getPermissionName());
        }
        if (StringUtils.hasText(request.getPath())) {
            updateEntity.setPath(request.getPath());
        }
        if (StringUtils.hasText(request.getComponent())) {
            updateEntity.setComponent(request.getComponent());
        }
        if (StringUtils.hasText(request.getIcon())) {
            updateEntity.setIcon(request.getIcon());
        }
        if (request.getSort() != null) {
            updateEntity.setSort(request.getSort());
        }
        if (request.getStatus() != null) {
            updateEntity.setStatus(request.getStatus());
        }

        permissionMapper.updateById(updateEntity);
        log.info("更新权限成功: permissionId={}, permissionCode={}", permissionId, permission.getPermissionCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long permissionId) {
        // 1. 查询权限
        SysPermission permission = permissionMapper.selectById(permissionId);
        Assert.notNull(permission, "权限不存在");

        // 2. 检查是否有子权限
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getParentId, permissionId);
        wrapper.eq(SysPermission::getDeleted, 0);
        long count = permissionMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "存在子权限，无法删除");
        }

        // 3. 逻辑删除
        SysPermission updateEntity = new SysPermission();
        updateEntity.setId(permissionId);
        updateEntity.setDeleted(1);
        permissionMapper.updateById(updateEntity);

        log.info("删除权限成功: permissionId={}, permissionCode={}", permissionId, permission.getPermissionCode());
    }

    @Override
    public List<PermissionVO> listPermissionTree() {
        // 查询所有权限
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getDeleted, 0);
        wrapper.orderByAsc(SysPermission::getSort);
        List<SysPermission> allPermissions = permissionMapper.selectList(wrapper);

        // 构建树形结构
        return buildTree(allPermissions, 0L);
    }

    @Override
    public List<PermissionVO> listEnabledPermissionTree() {
        // 查询所有启用的权限
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getDeleted, 0);
        wrapper.eq(SysPermission::getStatus, 1);
        wrapper.orderByAsc(SysPermission::getSort);
        List<SysPermission> allPermissions = permissionMapper.selectList(wrapper);

        // 构建树形结构
        return buildTree(allPermissions, 0L);
    }

    @Override
    public List<PermissionVO> listAllPermissions() {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getDeleted, 0);
        wrapper.orderByAsc(SysPermission::getSort);
        List<SysPermission> permissions = permissionMapper.selectList(wrapper);

        return permissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> listPermissionsByType(Integer permissionType) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getDeleted, 0);
        wrapper.eq(SysPermission::getPermissionType, permissionType);
        wrapper.orderByAsc(SysPermission::getSort);
        List<SysPermission> permissions = permissionMapper.selectList(wrapper);

        return permissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByPermissionCode(String permissionCode) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getPermissionCode, permissionCode);
        wrapper.eq(SysPermission::getDeleted, 0);
        return permissionMapper.selectCount(wrapper) > 0;
    }

    /**
     * 构建权限树
     *
     * @param allPermissions 所有权限列表
     * @param parentId       父权限ID
     * @return 权限树
     */
    private List<PermissionVO> buildTree(List<SysPermission> allPermissions, Long parentId) {
        // 按父ID分组
        Map<Long, List<SysPermission>> parentMap = allPermissions.stream()
                .collect(Collectors.groupingBy(SysPermission::getParentId));

        return buildTreeRecursive(parentMap, parentId);
    }

    /**
     * 递归构建权限树
     *
     * @param parentMap 父ID分组的权限Map
     * @param parentId  当前父ID
     * @return 子权限列表
     */
    private List<PermissionVO> buildTreeRecursive(Map<Long, List<SysPermission>> parentMap, Long parentId) {
        List<SysPermission> children = parentMap.get(parentId);
        if (CollectionUtils.isEmpty(children)) {
            return new ArrayList<>();
        }

        return children.stream()
                .map(permission -> {
                    PermissionVO vo = convertToVO(permission);
                    // 递归查找子权限
                    List<PermissionVO> childrenVOs = buildTreeRecursive(parentMap, permission.getId());
                    if (!CollectionUtils.isEmpty(childrenVOs)) {
                        vo.setChildren(childrenVOs);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO对象
     *
     * @param permission 权限实体
     * @return PermissionVO
     */
    private PermissionVO convertToVO(SysPermission permission) {
        return PermissionVO.builder()
                .id(permission.getId())
                .parentId(permission.getParentId())
                .permissionName(permission.getPermissionName())
                .permissionCode(permission.getPermissionCode())
                .permissionType(permission.getPermissionType())
                .path(permission.getPath())
                .component(permission.getComponent())
                .icon(permission.getIcon())
                .sort(permission.getSort())
                .status(permission.getStatus())
                .createTime(permission.getCreateTime())
                .updateTime(permission.getUpdateTime())
                .build();
    }
}
