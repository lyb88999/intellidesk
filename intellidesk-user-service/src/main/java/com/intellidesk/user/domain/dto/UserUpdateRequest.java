package com.intellidesk.user.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 用户更新请求
 *
 * @author IntelliDesk
 */
@Data
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 用户类型（1-客户 2-客服 3-管理员）
     */
    @Min(value = 1, message = "用户类型必须在1-3之间")
    @Max(value = 3, message = "用户类型必须在1-3之间")
    private Integer userType;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 状态（0-禁用 1-正常）
     */
    @Min(value = 0, message = "状态必须为0或1")
    @Max(value = 1, message = "状态必须为0或1")
    private Integer status;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 技能组ID（仅客服）
     */
    private Long skillGroupId;

    /**
     * 角色ID列表
     */
    private Set<Long> roleIds;
}
