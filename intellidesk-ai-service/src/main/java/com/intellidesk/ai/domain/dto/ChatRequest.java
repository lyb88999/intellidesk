package com.intellidesk.ai.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI对话请求
 *
 * @author IntelliDesk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    /**
     * 用户ID（用于维护对话上下文）
     */
    private Long userId;

    /**
     * 会话ID（可选，关联到会话服务）
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
     * 是否使用知识库（RAG）
     */
    private Boolean useKnowledge = true;
}
