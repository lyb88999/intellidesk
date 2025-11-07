package com.intellidesk.ai.service;

import com.intellidesk.ai.domain.dto.ChatRequest;
import com.intellidesk.ai.domain.dto.ChatResponse;

/**
 * AI对话服务接口
 *
 * @author IntelliDesk
 */
public interface IAiChatService {

    /**
     * AI对话
     *
     * @param request 对话请求
     * @return AI响应
     */
    ChatResponse chat(ChatRequest request);

    /**
     * 意图识别
     *
     * @param message 用户消息
     * @return 意图识别结果
     */
    ChatResponse.Intent recognizeIntent(String message);

    /**
     * 清除对话上下文
     *
     * @param userId 用户ID
     */
    void clearContext(Long userId);
}
