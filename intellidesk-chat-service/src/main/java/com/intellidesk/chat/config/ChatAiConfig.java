package com.intellidesk.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI集成配置
 *
 * @author IntelliDesk
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chat.ai")
public class ChatAiConfig {

    /**
     * 是否启用AI自动回复
     */
    private Boolean enabled = true;

    /**
     * 客户消息是否自动触发AI回复
     */
    private Boolean autoReplyCustomer = true;

    /**
     * AI判断需要转人工时自动分配客服
     */
    private Boolean transferOnAiRequest = true;
}
