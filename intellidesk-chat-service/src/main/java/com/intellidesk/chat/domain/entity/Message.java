package com.intellidesk.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息实体类
 *
 * @author IntelliDesk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者姓名
     */
    private String senderName;

    /**
     * 发送者类型: 1-客户, 2-客服, 3-系统, 4-AI机器人
     */
    private Integer senderType;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 接收者姓名
     */
    private String receiverName;

    /**
     * 消息类型: 1-文本, 2-图片, 3-文件, 4-语音, 5-视频, 6-系统消息
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 附件URL（图片、文件、语音、视频）
     */
    private String attachmentUrl;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 已读时间
     */
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
