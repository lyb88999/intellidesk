package com.intellidesk.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI配置类
 *
 * @author IntelliDesk
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AiConfig {

    /**
     * 智谱AI配置
     */
    private ZhipuConfig zhipu = new ZhipuConfig();

    /**
     * 对话上下文配置
     */
    private ContextConfig context = new ContextConfig();

    /**
     * 意图识别配置
     */
    private IntentConfig intent = new IntentConfig();

    @Data
    public static class ZhipuConfig {
        /**
         * API Key
         */
        private String apiKey;

        /**
         * API 基础URL
         */
        private String baseUrl = "https://open.bigmodel.cn/api/paas/v4";

        /**
         * 模型名称
         */
        private String model = "glm-4-flash";

        /**
         * 温度参数 (0.0-1.0)
         */
        private Double temperature = 0.7;

        /**
         * 最大Token数
         */
        private Integer maxTokens = 2000;

        /**
         * 超时时间（毫秒）
         */
        private Integer timeout = 30000;
    }

    @Data
    public static class ContextConfig {
        /**
         * 最大历史对话轮数
         */
        private Integer maxHistory = 10;

        /**
         * 上下文过期时间（秒）
         */
        private Integer expireSeconds = 1800;
    }

    @Data
    public static class IntentConfig {
        /**
         * 是否启用意图识别
         */
        private Boolean enabled = true;

        /**
         * 置信度阈值
         */
        private Double confidenceThreshold = 0.6;
    }
}
