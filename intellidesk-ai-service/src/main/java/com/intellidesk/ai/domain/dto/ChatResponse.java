package com.intellidesk.ai.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI对话响应
 *
 * @author IntelliDesk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * AI回复内容
     */
    private String content;

    /**
     * 意图识别结果
     */
    private Intent intent;

    /**
     * 是否需要转人工
     */
    private Boolean needHumanAgent = false;

    /**
     * 引用的知识库ID列表
     */
    private List<Long> knowledgeIds;

    /**
     * Token使用量
     */
    private TokenUsage tokenUsage;

    /**
     * 意图识别结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Intent {
        /**
         * 意图类型：greeting(问候), question(咨询), complaint(投诉), transfer(转人工), other(其他)
         */
        private String type;

        /**
         * 置信度 (0-1)
         */
        private Double confidence;
    }

    /**
     * Token使用量
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenUsage {
        /**
         * 提示词Token数
         */
        private Integer promptTokens;

        /**
         * 补全Token数
         */
        private Integer completionTokens;

        /**
         * 总Token数
         */
        private Integer totalTokens;
    }
}
