package com.intellidesk.ai.controller;

import com.intellidesk.ai.domain.dto.ChatRequest;
import com.intellidesk.ai.domain.dto.ChatResponse;
import com.intellidesk.ai.service.IAiChatService;
import com.intellidesk.common.core.domain.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * AI对话控制器
 *
 * @author IntelliDesk
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final IAiChatService aiChatService;

    /**
     * AI对话接口
     */
    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("AI对话请求: userId={}", request.getUserId());
        ChatResponse response = aiChatService.chat(request);
        return Result.success(response);
    }

    /**
     * 意图识别接口
     */
    @PostMapping("/intent")
    public Result<ChatResponse.Intent> recognizeIntent(@RequestBody String message) {
        log.info("意图识别请求: message={}", message);
        ChatResponse.Intent intent = aiChatService.recognizeIntent(message);
        return Result.success(intent);
    }

    /**
     * 清除对话上下文
     */
    @DeleteMapping("/context/{userId}")
    public Result<Void> clearContext(@PathVariable Long userId) {
        log.info("清除对话上下文: userId={}", userId);
        aiChatService.clearContext(userId);
        return Result.success();
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("AI Service is running");
    }
}
