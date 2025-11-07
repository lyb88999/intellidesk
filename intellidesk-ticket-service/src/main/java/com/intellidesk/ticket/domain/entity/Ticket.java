package com.intellidesk.ticket.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工单实体类
 *
 * @author IntelliDesk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ticket")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工单编号（唯一）
     */
    private String ticketNo;

    /**
     * 工单标题
     */
    private String title;

    /**
     * 工单描述
     */
    private String description;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 优先级: 1-低, 2-中, 3-高, 4-紧急
     */
    private Integer priority;

    /**
     * 状态: 0-待处理, 1-处理中, 2-待回复, 3-已解决, 4-已关闭, 5-已取消
     */
    private Integer status;

    /**
     * 提交人ID
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
     * 处理人ID
     */
    private Long assigneeId;

    /**
     * 处理人姓名
     */
    private String assigneeName;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 来源: 1-Web, 2-移动端, 3-邮件, 4-API, 5-会话转工单
     */
    private Integer source;

    /**
     * 标签（多个标签用逗号分隔）
     */
    private String tags;

    /**
     * 截止时间
     */
    private LocalDateTime dueTime;

    /**
     * 首次响应时间
     */
    private LocalDateTime firstResponseTime;

    /**
     * 解决时间
     */
    private LocalDateTime resolvedTime;

    /**
     * 关闭时间
     */
    private LocalDateTime closedTime;

    /**
     * 满意度评分: 1-5
     */
    private Integer satisfaction;

    /**
     * 评价内容
     */
    private String comment;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    @TableLogic
    private Integer deleted;
}
