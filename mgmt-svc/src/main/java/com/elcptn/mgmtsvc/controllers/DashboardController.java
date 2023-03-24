package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.dto.DashboardMetricsDto;
import com.elcptn.mgmtsvc.services.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/* @author: kc, created on 3/9/23 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class DashboardController {

    public static final Long MAX_INTERVAL = 1440L;
    private final DashboardService dashboardService;

    @GetMapping("/api/dashboard/metrics")
    public ResponseEntity<DashboardMetricsDto> getDashboardMetrics(@RequestParam(required =
            false, defaultValue = "1440") String interval) {
        Long intervalVal = parseInterval(interval);
        DashboardMetricsDto metricsDto = dashboardService.getMetrics(intervalVal);
        return ResponseEntity.ok(metricsDto);
    }

    @GetMapping("/api/dashboard/source/{id}/metrics")
    public ResponseEntity<DashboardMetricsDto> getSourceMetrics(@PathVariable UUID id, @RequestParam(required =
            false, defaultValue = "1440") String interval) {
        Long intervalVal = parseInterval(interval);

        DashboardMetricsDto metricsDto = dashboardService.getSourceMetrics(id, intervalVal);
        return ResponseEntity.ok(metricsDto);
    }

    private Long parseInterval(String interval) {
        Long intervalVal;
        try {
            intervalVal = Long.parseLong(interval);
            if (intervalVal > MAX_INTERVAL) {
                intervalVal = MAX_INTERVAL;
            }
        } catch (Exception e) {
            intervalVal = MAX_INTERVAL;
        }
        return intervalVal;
    }
}
