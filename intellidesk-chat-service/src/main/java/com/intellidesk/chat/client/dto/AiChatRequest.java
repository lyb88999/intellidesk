package com.intellidesk.chat.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI对话请求（调用AI服务）
 *
 * @author IntelliDesk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 用户消息
     */
    private String message;

    /**
     * 是否流式输出
     */
    private Boolean stream = false;

    /**
     * 是否使用知识库
     */
    private Boolean useKnowledge = true;
}
