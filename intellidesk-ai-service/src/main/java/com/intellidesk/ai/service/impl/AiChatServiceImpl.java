package com.intellidesk.ai.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellidesk.ai.config.AiConfig;
import com.intellidesk.ai.domain.dto.ChatRequest;
import com.intellidesk.ai.domain.dto.ChatResponse;
import com.intellidesk.ai.service.IAiChatService;
import com.intellidesk.ai.util.ZhipuAiClient;
import com.intellidesk.common.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * AI对话服务实现
 *
 * @author IntelliDesk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements IAiChatService {

    private final ZhipuAiClient zhipuAiClient;
    private final AiConfig aiConfig;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CONTEXT_KEY_PREFIX = "ai:context:";
    private static final String SYSTEM_PROMPT = "你是IntelliDesk智能客服助手，专业、友好、高效。" +
            "你的任务是帮助用户解答问题，处理客户服务相关的咨询。" +
            "如果遇到无法回答的问题，请告诉用户需要转接人工客服。" +
            "回复要简洁明了，不超过200字。";

    @Override
    public ChatResponse chat(ChatRequest request) {
        try {
            log.info("收到AI对话请求: userId={}, message={}", request.getUserId(), request.getMessage());

            // 1. 意图识别
            ChatResponse.Intent intent = null;
            if (aiConfig.getIntent().getEnabled()) {
                intent = recognizeIntent(request.getMessage());
                log.info("意图识别结果: type={}, confidence={}", intent.getType(), intent.getConfidence());
            }

            // 2. 判断是否需要转人工
            boolean needHuman = false;
            if (intent != null && "transfer".equals(intent.getType())) {
                needHuman = true;
            }

            // 3. 构建消息历史
            List<Map<String, String>> messages = buildMessages(request.getUserId(), request.getMessage());

            // 4. 调用AI接口
            String aiReply = zhipuAiClient.chat(messages);
            log.info("AI回复: {}", aiReply);

            // 5. 保存对话上下文
            saveContext(request.getUserId(), request.getMessage(), aiReply);

            // 6. 构建响应
            return ChatResponse.builder()
                    .content(aiReply)
                    .intent(intent)
                    .needHumanAgent(needHuman)
                    .build();

        } catch (Exception e) {
            log.error("AI对话失败", e);
            throw new BusinessException("AI对话失败: " + e.getMessage());
        }
    }

    @Override
    public ChatResponse.Intent recognizeIntent(String message) {
        try {
            // 构建意图识别Prompt
            String intentPrompt = "请识别以下用户消息的意图类型，只返回JSON格式的结果，不要有任何其他文字。\n\n" +
                    "意图类型说明：\n" +
                    "- greeting: 问候、打招呼\n" +
                    "- question: 咨询、提问\n" +
                    "- complaint: 投诉、不满\n" +
                    "- transfer: 明确要求转人工\n" +
                    "- other: 其他\n\n" +
                    "用户消息：" + message + "\n\n" +
                    "请返回JSON格式：{\"type\": \"意图类型\", \"confidence\": 置信度(0-1之间的小数)}";

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", intentPrompt));

            String response = zhipuAiClient.chat(messages);
            log.debug("意图识别原始响应: {}", response);

            // 解析JSON响应
            // 提取JSON部分（可能包含markdown代码块）
            String jsonStr = response.trim();
            if (jsonStr.startsWith("```json")) {
                jsonStr = jsonStr.substring(7);
            } else if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.substring(3);
            }
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
            jsonStr = jsonStr.trim();

            Map<String, Object> result = objectMapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {});

            String type = (String) result.get("type");
            Double confidence = ((Number) result.get("confidence")).doubleValue();

            return ChatResponse.Intent.builder()
                    .type(type)
                    .confidence(confidence)
                    .build();

        } catch (Exception e) {
            log.error("意图识别失败", e);
            // 返回默认意图
            return ChatResponse.Intent.builder()
                    .type("other")
                    .confidence(0.0)
                    .build();
        }
    }

    @Override
    public void clearContext(Long userId) {
        String key = CONTEXT_KEY_PREFIX + userId;
        redisTemplate.delete(key);
        log.info("清除用户对话上下文: userId={}", userId);
    }

    /**
     * 构建消息列表（包含系统提示词和历史对话）
     */
    private List<Map<String, String>> buildMessages(Long userId, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 1. 添加系统提示词
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));

        // 2. 添加历史对话
        List<Map<String, String>> history = getContext(userId);
        if (history != null && !history.isEmpty()) {
            messages.addAll(history);
        }

        // 3. 添加当前用户消息
        messages.add(Map.of("role", "user", "content", userMessage));

        return messages;
    }

    /**
     * 获取对话上下文
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, String>> getContext(Long userId) {
        String key = CONTEXT_KEY_PREFIX + userId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return new ArrayList<>();
        }
        return (List<Map<String, String>>) value;
    }

    /**
     * 保存对话上下文
     */
    private void saveContext(Long userId, String userMessage, String aiReply) {
        String key = CONTEXT_KEY_PREFIX + userId;

        // 获取现有上下文
        List<Map<String, String>> context = getContext(userId);

        // 添加新的对话
        context.add(Map.of("role", "user", "content", userMessage));
        context.add(Map.of("role", "assistant", "content", aiReply));

        // 限制历史对话数量（只保留最近的N轮）
        int maxHistory = aiConfig.getContext().getMaxHistory() * 2;  // *2因为包含user和assistant
        if (context.size() > maxHistory) {
            context = context.subList(context.size() - maxHistory, context.size());
        }

        // 保存到Redis
        redisTemplate.opsForValue().set(
                key,
                context,
                aiConfig.getContext().getExpireSeconds(),
                TimeUnit.SECONDS
        );
    }
}
