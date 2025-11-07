package com.intellidesk.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 会话服务启动类
 *
 * @author IntelliDesk
 */
@SpringBootApplication(scanBasePackages = {"com.intellidesk.chat", "com.intellidesk.common"})
@EnableDiscoveryClient
@MapperScan("com.intellidesk.chat.mapper")
public class ChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  IntelliDesk 会话服务启动成功！");
        System.out.println("  Port: 8082");
        System.out.println("  WebSocket: /ws/chat");
        System.out.println("========================================\n");
    }
}
