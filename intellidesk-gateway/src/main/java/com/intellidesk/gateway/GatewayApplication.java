package com.intellidesk.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关启动类
 *
 * @author IntelliDesk
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.java, args);
        System.out.println("========================================");
        System.out.println("    IntelliDesk Gateway 启动成功！");
        System.out.println("========================================");
    }
}
