package com.intellidesk.ai.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellidesk.ai.config.AiConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 智谱AI客户端
 *
 * @author IntelliDesk
 */
@Slf4j
@Component
public class ZhipuAiClient {

    private final AiConfig aiConfig;
    private final ObjectMapper objectMapper;
    private OkHttpClient httpClient;

    public ZhipuAiClient(AiConfig aiConfig, ObjectMapper objectMapper) {
        this.aiConfig = aiConfig;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(aiConfig.getZhipu().getTimeout(), TimeUnit.MILLISECONDS)
                .build();
        log.info("智谱AI客户端初始化成功，模型: {}", aiConfig.getZhipu().getModel());
    }

    /**
     * 调用智谱AI聊天接口
     *
     * @param messages 消息列表，格式：[{"role": "user", "content": "你好"}]
     * @return AI回复
     */
    public String chat(List<Map<String, String>> messages) throws IOException {
        AiConfig.ZhipuConfig config = aiConfig.getZhipu();

        // 构建请求体
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", config.getModel());
        requestBody.put("temperature", config.getTemperature());
        requestBody.put("max_tokens", config.getMaxTokens());

        // 添加消息列表
        ArrayNode messagesNode = requestBody.putArray("messages");
        for (Map<String, String> message : messages) {
            ObjectNode msgNode = messagesNode.addObject();
            msgNode.put("role", message.get("role"));
            msgNode.put("content", message.get("content"));
        }

        // 构建请求
        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(config.getBaseUrl() + "/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + config.getApiKey())
                .addHeader("Content-Type", "application/json")
                .build();

        // 发送请求
        log.debug("调用智谱AI接口，消息数: {}", messages.size());
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                log.error("智谱AI接口调用失败: {} - {}", response.code(), errorBody);
                throw new IOException("智谱AI接口调用失败: " + response.code() + " - " + errorBody);
            }

            String responseBody = response.body().string();
            log.debug("智谱AI接口响应: {}", responseBody);

            // 解析响应
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            JsonNode choices = jsonResponse.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.get("message");
                if (message != null) {
                    return message.get("content").asText();
                }
            }

            log.error("智谱AI响应格式异常: {}", responseBody);
            throw new IOException("智谱AI响应格式异常");
        }
    }

    /**
     * 调用智谱AI Embedding接口（用于向量化）
     *
     * @param text 文本
     * @return 向量数组
     */
    public float[] embedding(String text) throws IOException {
        AiConfig.ZhipuConfig config = aiConfig.getZhipu();

        // 构建请求体
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", "embedding-2");  // 智谱AI的Embedding模型
        requestBody.put("input", text);

        // 构建请求
        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(config.getBaseUrl() + "/embeddings")
                .post(body)
                .addHeader("Authorization", "Bearer " + config.getApiKey())
                .addHeader("Content-Type", "application/json")
                .build();

        // 发送请求
        log.debug("调用智谱AI Embedding接口");
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                log.error("智谱AI Embedding接口调用失败: {} - {}", response.code(), errorBody);
                throw new IOException("智谱AI Embedding接口调用失败: " + response.code());
            }

            String responseBody = response.body().string();

            // 解析响应
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            JsonNode data = jsonResponse.get("data");
            if (data != null && data.isArray() && data.size() > 0) {
                JsonNode firstData = data.get(0);
                JsonNode embedding = firstData.get("embedding");
                if (embedding != null && embedding.isArray()) {
                    float[] vector = new float[embedding.size()];
                    for (int i = 0; i < embedding.size(); i++) {
                        vector[i] = (float) embedding.get(i).asDouble();
                    }
                    return vector;
                }
            }

            log.error("智谱AI Embedding响应格式异常: {}", responseBody);
            throw new IOException("智谱AI Embedding响应格式异常");
        }
    }
}
