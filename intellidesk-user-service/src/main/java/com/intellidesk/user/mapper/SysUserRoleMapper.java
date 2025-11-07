package com.intellidesk.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intellidesk.user.domain.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联 Mapper
 *
 * @author IntelliDesk
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 批量插入用户角色关联
     *
     * @param userRoles 用户角色列表
     * @return 插入数量
     */
    int insertBatch(@Param("list") List<SysUserRole> userRoles);

    /**
     * 删除用户的所有角色
     *
     * @param userId 用户ID
     * @return 删除数量
     */
    int deleteByUserId(@Param("userId") Long userId);
}
