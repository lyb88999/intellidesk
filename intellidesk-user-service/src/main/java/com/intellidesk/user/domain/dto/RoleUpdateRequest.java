package com.intellidesk.user.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 角色更新请求
 *
 * @author IntelliDesk
 */
@Data
public class RoleUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    /**
     * 角色描述
     */
    @Size(max = 200, message = "角色描述长度不能超过200个字符")
    private String description;

    /**
     * 状态（0-禁用 1-正常）
     */
    @Min(value = 0, message = "状态必须为0或1")
    @Max(value = 1, message = "状态必须为0或1")
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 权限ID列表
     */
    private Set<Long> permissionIds;
}
