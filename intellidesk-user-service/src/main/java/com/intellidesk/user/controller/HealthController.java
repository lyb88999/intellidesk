package com.intellidesk.user.controller;

import com.intellidesk.common.core.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查 Controller
 *
 * @author IntelliDesk
 */
@Slf4j
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public Result<Map<String, Object>> health() {
        log.info("健康检查请求");

        Map<String, Object> data = new HashMap<>();
        data.put("service", "intellidesk-user-service");
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());

        return Result.success(data);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("serviceName", "IntelliDesk User Service");
        data.put("version", "1.0.0-SNAPSHOT");
        data.put("description", "用户服务，负责用户、角色、权限、部门管理");
        data.put("author", "IntelliDesk Team");

        return Result.success(data);
    }
}
