package com.surefor.service.api.controller;

import com.surefor.service.common.monitor.MetricsHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(name = "Healthcheck Endpoint")
@RequiredArgsConstructor
public class HealthCheckController {

    @Operation(summary = "Verify this instance is healthy.")
    @RequestMapping("/ping")
    public String ping() {
        log.info("health check requested !!");
        return "pong";
    }
}
