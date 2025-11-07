package com.intellidesk.user.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 角色创建请求
 *
 * @author IntelliDesk
 */
@Data
public class RoleCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 100, message = "角色编码长度不能超过100个字符")
    @Pattern(regexp = "^[A-Z_]+$", message = "角色编码只能包含大写字母和下划线")
    private String roleCode;

    /**
     * 角色描述
     */
    @Size(max = 200, message = "角色描述长度不能超过200个字符")
    private String description;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status = 1;

    /**
     * 排序
     */
    private Integer sort = 0;

    /**
     * 权限ID列表
     */
    private Set<Long> permissionIds;
}
