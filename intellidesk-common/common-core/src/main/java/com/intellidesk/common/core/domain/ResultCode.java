package com.intellidesk.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author IntelliDesk
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // ==================== 通用响应码 ====================
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    FAIL(400, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未认证
     */
    UNAUTHORIZED(401, "未认证，请先登录"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),

    // ==================== 业务响应码 ====================
    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1001, "用户不存在"),

    /**
     * 用户名或密码错误
     */
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(1003, "用户已被禁用"),

    /**
     * 用户名已存在
     */
    USERNAME_EXISTS(1004, "用户名已存在"),

    /**
     * 手机号已存在
     */
    PHONE_EXISTS(1005, "手机号已存在"),

    /**
     * 邮箱已存在
     */
    EMAIL_EXISTS(1006, "邮箱已存在"),

    /**
     * Token 无效
     */
    TOKEN_INVALID(1007, "Token 无效"),

    /**
     * Token 已过期
     */
    TOKEN_EXPIRED(1008, "Token 已过期"),

    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(1009, "验证码错误"),

    /**
     * 旧密码错误
     */
    OLD_PASSWORD_ERROR(1010, "旧密码错误"),

    /**
     * 工单不存在
     */
    TICKET_NOT_FOUND(2001, "工单不存在"),

    /**
     * 工单状态不允许操作
     */
    TICKET_STATUS_ERROR(2002, "工单状态不允许此操作"),

    /**
     * 工单已关闭
     */
    TICKET_CLOSED(2003, "工单已关闭"),

    /**
     * 会话不存在
     */
    CONVERSATION_NOT_FOUND(3001, "会话不存在"),

    /**
     * 会话已结束
     */
    CONVERSATION_ENDED(3002, "会话已结束"),

    /**
     * 客服不在线
     */
    AGENT_OFFLINE(3003, "客服不在线"),

    /**
     * 客服忙碌
     */
    AGENT_BUSY(3004, "客服忙碌，请稍后再试"),

    /**
     * 知识库不存在
     */
    KNOWLEDGE_NOT_FOUND(4001, "知识不存在"),

    /**
     * AI 服务异常
     */
    AI_SERVICE_ERROR(5001, "AI 服务异常"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(6001, "文件上传失败"),

    /**
     * 文件类型不支持
     */
    FILE_TYPE_ERROR(6002, "文件类型不支持"),

    /**
     * 文件大小超限
     */
    FILE_SIZE_ERROR(6003, "文件大小超过限制"),

    /**
     * 限流
     */
    RATE_LIMIT(9001, "请求过于频繁，请稍后再试"),

    /**
     * 服务降级
     */
    DEGRADE(9002, "系统繁忙，请稍后再试"),

    /**
     * 系统维护
     */
    SYSTEM_MAINTENANCE(9999, "系统维护中，请稍后再试");

    /**
     * 响应码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;
}
