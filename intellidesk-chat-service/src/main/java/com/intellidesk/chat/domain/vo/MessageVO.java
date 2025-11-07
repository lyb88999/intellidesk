package com.intellidesk.chat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息视图对象
 *
 * @author IntelliDesk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private Integer senderType;
    private String senderTypeText;
    private Long receiverId;
    private String receiverName;
    private Integer messageType;
    private String messageTypeText;
    private String content;
    private String attachmentUrl;
    private Boolean isRead;
    private LocalDateTime readTime;
    private LocalDateTime createTime;
}
