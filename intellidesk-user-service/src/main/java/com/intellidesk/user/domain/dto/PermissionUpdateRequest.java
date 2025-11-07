package com.intellidesk.user.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 权限更新请求
 *
 * @author IntelliDesk
 */
@Data
public class PermissionUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 权限名称
     */
    @Size(max = 50, message = "权限名称长度不能超过50个字符")
    private String permissionName;

    /**
     * 路由路径
     */
    @Size(max = 200, message = "路由路径长度不能超过200个字符")
    private String path;

    /**
     * 组件路径
     */
    @Size(max = 200, message = "组件路径长度不能超过200个字符")
    private String component;

    /**
     * 图标
     */
    @Size(max = 100, message = "图标长度不能超过100个字符")
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0-禁用 1-正常）
     */
    @Min(value = 0, message = "状态必须为0或1")
    @Max(value = 1, message = "状态必须为0或1")
    private Integer status;
}
