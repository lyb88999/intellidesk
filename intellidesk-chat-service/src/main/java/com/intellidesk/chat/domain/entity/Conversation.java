package com.intellidesk.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话实体类
 *
 * @author IntelliDesk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation")
public class Conversation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话编号（唯一）
     */
    private String conversationNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 客服ID
     */
    private Long agentId;

    /**
     * 客服姓名
     */
    private String agentName;

    /**
     * 状态: 0-等待中, 1-进行中, 2-已结束
     */
    private Integer status;

    /**
     * 来源: 1-Web, 2-移动端, 3-小程序
     */
    private Integer source;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 首次响应时间
     */
    private LocalDateTime firstResponseTime;

    /**
     * 等待时长（秒）
     */
    private Long waitDuration;

    /**
     * 对话时长（秒）
     */
    private Long chatDuration;

    /**
     * 消息数量
     */
    private Integer messageCount;

    /**
     * 满意度评分: 1-5
     */
    private Integer satisfaction;

    /**
     * 评价内容
     */
    private String comment;

    /**
     * 是否转工单
     */
    private Boolean convertedToTicket;

    /**
     * 工单ID
     */
    private Long ticketId;

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
