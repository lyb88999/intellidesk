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
}
