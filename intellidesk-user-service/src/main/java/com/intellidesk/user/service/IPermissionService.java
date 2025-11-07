package com.intellidesk.user.service;

import com.intellidesk.user.domain.dto.PermissionCreateRequest;
import com.intellidesk.user.domain.dto.PermissionUpdateRequest;
import com.intellidesk.user.domain.vo.PermissionVO;

import java.util.List;

/**
 * 权限服务接口
 *
 * @author IntelliDesk
 */
public interface IPermissionService {

    /**
     * 根据权限ID获取权限信息
     *
     * @param permissionId 权限ID
     * @return 权限信息
     */
    PermissionVO getPermissionById(Long permissionId);

    /**
     * 创建权限
     *
     * @param request 创建请求
     * @return 权限ID
     */
    Long createPermission(PermissionCreateRequest request);

    /**
     * 更新权限
     *
     * @param permissionId 权限ID
     * @param request      更新请求
     */
    void updatePermission(Long permissionId, PermissionUpdateRequest request);

    /**
     * 删除权限（逻辑删除）
     *
     * @param permissionId 权限ID
     */
    void deletePermission(Long permissionId);

    /**
     * 查询权限树（所有权限）
     *
     * @return 权限树
     */
    List<PermissionVO> listPermissionTree();

    /**
     * 查询启用的权限树
     *
     * @return 权限树
     */
    List<PermissionVO> listEnabledPermissionTree();

    /**
     * 查询所有权限列表（扁平结构）
     *
     * @return 权限列表
     */
    List<PermissionVO> listAllPermissions();

    /**
     * 根据权限类型查询权限
     *
     * @param permissionType 权限类型（1-菜单 2-按钮 3-接口）
     * @return 权限列表
     */
    List<PermissionVO> listPermissionsByType(Integer permissionType);

    /**
     * 检查权限编码是否存在
     *
     * @param permissionCode 权限编码
     * @return 是否存在
     */
    boolean existsByPermissionCode(String permissionCode);
}
