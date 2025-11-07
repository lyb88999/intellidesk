package com.intellidesk.user.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 权限创建请求
 *
 * @author IntelliDesk
 */
@Data
public class PermissionCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父权限ID（0表示顶级权限）
     */
    @NotNull(message = "父权限ID不能为空")
    private Long parentId = 0L;

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 50, message = "权限名称长度不能超过50个字符")
    private String permissionName;

    /**
     * 权限编码
     */
    @NotBlank(message = "权限编码不能为空")
    @Size(max = 100, message = "权限编码长度不能超过100个字符")
    @Pattern(regexp = "^[a-z:_]+$", message = "权限编码只能包含小写字母、冒号和下划线")
    private String permissionCode;

    /**
     * 权限类型（1-菜单 2-按钮 3-接口）
     */
    @NotNull(message = "权限类型不能为空")
    @Min(value = 1, message = "权限类型必须在1-3之间")
    @Max(value = 3, message = "权限类型必须在1-3之间")
    private Integer permissionType;

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
    private Integer sort = 0;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status = 1;
}
