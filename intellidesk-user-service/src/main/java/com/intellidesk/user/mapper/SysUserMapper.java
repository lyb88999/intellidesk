package com.intellidesk.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intellidesk.user.domain.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * 用户 Mapper
 *
 * @author IntelliDesk
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户ID查询角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码集合
     */
    Set<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码集合
     */
    Set<String> selectPermissionCodesByUserId(@Param("userId") Long userId);
}
