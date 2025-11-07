package com.intellidesk.chat.client;

import com.intellidesk.chat.client.dto.AiChatRequest;
import com.intellidesk.chat.client.dto.AiChatResponse;
import com.intellidesk.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * AI服务Feign客户端
 *
 * @author IntelliDesk
 */
@FeignClient(name = "intellidesk-ai-service", path = "/api/v1/ai")
public interface AiServiceClient {

    /**
     * AI对话
     *
     * @param request 对话请求
     * @return AI响应
     */
    @PostMapping("/chat")
    Result<AiChatResponse> chat(@RequestBody AiChatRequest request);
}
