package com.intellidesk.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intellidesk.user.domain.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * 角色 Mapper
 *
 * @author IntelliDesk
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色ID查询权限编码列表
     *
     * @param roleId 角色ID
     * @return 权限编码集合
     */
    Set<String> selectPermissionCodesByRoleId(@Param("roleId") Long roleId);
}
