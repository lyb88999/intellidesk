package com.intellidesk.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 网关启动类
 *
 * @author IntelliDesk
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.intellidesk.gateway", "com.intellidesk.common"}) // 扫描网关和common包
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("========================================");
        System.out.println("    IntelliDesk Gateway 启动成功！");
        System.out.println("========================================");
    }
}
