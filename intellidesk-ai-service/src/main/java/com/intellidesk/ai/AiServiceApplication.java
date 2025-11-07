package com.intellidesk.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * AI服务启动类
 *
 * @author IntelliDesk
 */
@SpringBootApplication(scanBasePackages = {"com.intellidesk.ai", "com.intellidesk.common"})
@EnableDiscoveryClient
public class AiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiServiceApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("🤖 IntelliDesk AI Service 启动成功!");
        System.out.println("========================================\n");
    }
}
