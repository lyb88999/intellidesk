package com.intellidesk.chat.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 发送消息请求
 *
 * @author IntelliDesk
 */
@Data
public class SendMessageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @NotNull(message = "会话ID不能为空")
    private Long conversationId;

    /**
     * 消息类型: 1-文本, 2-图片, 3-文件, 4-语音, 5-视频
     */
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 附件URL
     */
    private String attachmentUrl;

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
     * WebSocket消息类型（与messageType区分）:
     * 1-文本消息, 2-图片消息, 3-文件消息,
     * 10-连接成功, 11-心跳, 12-系统消息
     */
    private Integer type;
}
