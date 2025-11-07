package com.intellidesk.user.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询请求
 *
 * @author IntelliDesk
 */
@Data
public class UserQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 昵称（模糊查询）
     */
    private String nickname;

    /**
     * 邮箱（模糊查询）
     */
    private String email;

    /**
     * 手机号（精确查询）
     */
    private String phone;

    /**
     * 用户类型（1-客户 2-客服 3-管理员）
     */
    private Integer userType;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 技能组ID
     */
    private Long skillGroupId;

    /**
     * 当前页码（默认1）
     */
    private Integer pageNum = 1;

    /**
     * 每页大小（默认10）
     */
    private Integer pageSize = 10;
}
