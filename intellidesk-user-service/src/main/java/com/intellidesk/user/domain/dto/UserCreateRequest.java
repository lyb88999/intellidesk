package com.intellidesk.user.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 用户创建请求
 *
 * @author IntelliDesk
 */
@Data
public class UserCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
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
    @NotNull(message = "用户类型不能为空")
    @Min(value = 1, message = "用户类型必须在1-3之间")
    @Max(value = 3, message = "用户类型必须在1-3之间")
    private Integer userType;

    /**
     * 头像URL
     */
    private String avatar;

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
