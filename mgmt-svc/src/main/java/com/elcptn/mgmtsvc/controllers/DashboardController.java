package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.dto.DashboardMetricsDto;
import com.elcptn.mgmtsvc.services.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/* @author: kc, created on 3/9/23 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/api/dashboard/metrics")
    public ResponseEntity<DashboardMetricsDto> rotateTables() {
        DashboardMetricsDto metricsDto = dashboardService.getMetrics();
        return ResponseEntity.ok(metricsDto);
    }
}
