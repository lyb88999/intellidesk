package com.intellidesk.chat.client.dto;

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
public class AiChatResponse {

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
    private Boolean needHumanAgent;

    /**
     * 引用的知识库ID列表
     */
    private List<Long> knowledgeIds;

    /**
     * 意图识别结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Intent {
        /**
         * 意图类型
         */
        private String type;

        /**
         * 置信度
         */
        private Double confidence;
    }
}
