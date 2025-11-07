package com.intellidesk.chat.controller;

import com.intellidesk.common.core.domain.Result;
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
@RestController
@RequestMapping
public class HealthController {

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("service", "intellidesk-chat-service");
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        return Result.success(data);
    }
}
