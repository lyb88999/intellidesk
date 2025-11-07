package com.intellidesk.user.service;

import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.user.domain.dto.RoleCreateRequest;
import com.intellidesk.user.domain.dto.RoleQueryRequest;
import com.intellidesk.user.domain.dto.RoleUpdateRequest;
import com.intellidesk.user.domain.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author IntelliDesk
 */
public interface IRoleService {

    /**
     * 根据角色ID获取角色信息
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    RoleVO getRoleById(Long roleId);

    /**
     * 创建角色
     *
     * @param request 创建请求
     * @return 角色ID
     */
    Long createRole(RoleCreateRequest request);

    /**
     * 更新角色
     *
     * @param roleId 角色ID
     * @param request 更新请求
     */
    void updateRole(Long roleId, RoleUpdateRequest request);

    /**
     * 删除角色（逻辑删除）
     *
     * @param roleId 角色ID
     */
    void deleteRole(Long roleId);

    /**
     * 分页查询角色列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<RoleVO> listRoles(RoleQueryRequest request);

    /**
     * 查询所有启用的角色
     *
     * @return 角色列表
     */
    List<RoleVO> listEnabledRoles();

    /**
     * 检查角色编码是否存在
     *
     * @param roleCode 角色编码
     * @return 是否存在
     */
    boolean existsByRoleCode(String roleCode);
}
