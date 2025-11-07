package com.intellidesk.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intellidesk.user.domain.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限关联 Mapper
 *
 * @author IntelliDesk
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 批量插入角色权限关联
     *
     * @param rolePermissions 角色权限列表
     * @return 插入数量
     */
    int insertBatch(@Param("list") List<SysRolePermission> rolePermissions);

    /**
     * 删除角色的所有权限
     *
     * @param roleId 角色ID
     * @return 删除数量
     */
    int deleteByRoleId(@Param("roleId") Long roleId);
}
