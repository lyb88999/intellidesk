package com.intellidesk.ticket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 工单服务启动类
 *
 * @author IntelliDesk
 */
@SpringBootApplication(scanBasePackages = {"com.intellidesk.ticket", "com.intellidesk.common"})
@EnableDiscoveryClient
@MapperScan("com.intellidesk.ticket.mapper")
public class TicketServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketServiceApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  IntelliDesk 工单服务启动成功！");
        System.out.println("  Port: 8083");
        System.out.println("========================================\n");
    }
}
