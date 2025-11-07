package com.intellidesk.user.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 部门查询请求
 *
 * @author IntelliDesk
 */
@Data
public class DepartmentQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门名称（模糊查询）
     */
    private String deptName;

    /**
     * 部门编码（模糊查询）
     */
    private String deptCode;

    /**
     * 状态: 0-禁用, 1-正常
     */
    private Integer status;

    /**
     * 页码
     */
    private Long pageNum = 1L;

    /**
     * 每页大小
     */
    private Long pageSize = 10L;
}
