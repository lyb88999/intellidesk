package com.intellidesk.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证配置属性
 *
 * @author IntelliDesk
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * 白名单路径（不需要认证）
     */
    private List<String> whitelist = new ArrayList<>();

    /**
     * 是否启用认证
     */
    private Boolean enabled = true;
}
