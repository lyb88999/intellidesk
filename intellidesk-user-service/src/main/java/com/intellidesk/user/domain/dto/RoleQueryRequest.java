package com.intellidesk.user.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色查询请求
 *
 * @author IntelliDesk
 */
@Data
public class RoleQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称（模糊查询）
     */
    private String roleName;

    /**
     * 角色编码（模糊查询）
     */
    private String roleCode;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;

    /**
     * 当前页码（默认1）
     */
    private Integer pageNum = 1;

    /**
     * 每页大小（默认10）
     */
    private Integer pageSize = 10;
}
