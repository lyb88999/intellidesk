package com.intellidesk.common.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 登录用户信息
 *
 * @author IntelliDesk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户类型 (1-客服, 2-客户, 3-管理员)
     */
    private Integer userType;

    /**
     * 部门 ID
     */
    private Long deptId;

    /**
     * 角色列表
     */
    private Set<String> roles;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    /**
     * Token
     */
    private String token;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录 IP
     */
    private String ipAddress;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 判断是否有某个权限
     */
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    /**
     * 判断是否有某个角色
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * 判断是否是管理员
     */
    public boolean isAdmin() {
        return roles != null && (roles.contains("ROLE_SUPER_ADMIN") || roles.contains("ROLE_ADMIN"));
    }

    /**
     * 判断是否是客服
     */
    public boolean isAgent() {
        return userType != null && userType == 1;
    }

    /**
     * 判断是否是客户
     */
    public boolean isCustomer() {
        return userType != null && userType == 2;
    }
}
