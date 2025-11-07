package com.intellidesk.ticket.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建工单请求
 *
 * @author IntelliDesk
 */
@Data
public class TicketCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单标题
     */
    @NotBlank(message = "工单标题不能为空")
    private String title;

    /**
     * 工单描述
     */
    @NotBlank(message = "工单描述不能为空")
    private String description;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 优先级: 1-低, 2-中, 3-高, 4-紧急（默认2-中）
     */
    private Integer priority;

    /**
     * 提交人ID（从JWT中获取，或客户提交时填写）
     */
    private Long submitterId;

    /**
     * 提交人姓名
     */
    private String submitterName;

    /**
     * 提交人邮箱
     */
    private String submitterEmail;

    /**
     * 提交人电话
     */
    private String submitterPhone;

    /**
     * 处理人ID（可选，支持直接分配）
     */
    private Long assigneeId;

    /**
     * 部门ID（可选）
     */
    private Long departmentId;

    /**
     * 来源: 1-Web, 2-移动端, 3-邮件, 4-API, 5-会话转工单（默认1-Web）
     */
    private Integer source;

    /**
     * 标签（多个标签用逗号分隔）
     */
    private String tags;

    /**
     * 截止时间（可选）
     */
    private LocalDateTime dueTime;
}
