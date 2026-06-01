package com.myproject.clinic.dashboard.controller;

import com.myproject.clinic.dashboard.dto.DashboardStatsResponse;
import com.myproject.clinic.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể
 * Dashboard.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardStatsResponse getStats(@RequestParam(required = false) String date) {
        return dashboardService.getStats(date);
    }
}
