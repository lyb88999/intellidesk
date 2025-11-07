package com.intellidesk.user.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新部门请求
 *
 * @author IntelliDesk
 */
@Data
public class DepartmentUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @NotNull(message = "部门ID不能为空")
    private Long id;

    /**
     * 父部门ID
     */
    @NotNull(message = "父部门ID不能为空")
    private Long parentId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    /**
     * 部门编码
     */
    @NotBlank(message = "部门编码不能为空")
    private String deptCode;

    /**
     * 负责人ID
     */
    private Long leaderId;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态: 0-禁用, 1-正常
     */
    private Integer status;
}
