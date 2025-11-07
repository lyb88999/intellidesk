package com.intellidesk.chat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话视图对象
 *
 * @author IntelliDesk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String conversationNo;
    private Long customerId;
    private String customerName;
    private Long agentId;
    private String agentName;
    private Integer status;
    private String statusText;
    private Integer source;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime firstResponseTime;
    private Long waitDuration;
    private Long chatDuration;
    private Integer messageCount;
    private Integer satisfaction;
    private String comment;
    private Boolean convertedToTicket;
    private Long ticketId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
