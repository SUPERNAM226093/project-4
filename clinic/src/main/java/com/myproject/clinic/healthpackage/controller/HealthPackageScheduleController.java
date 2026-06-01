package com.myproject.clinic.healthpackage.controller;

import com.myproject.clinic.healthpackage.dto.HealthPackageScheduleRequest;
import com.myproject.clinic.healthpackage.dto.HealthPackageScheduleResponse;
import com.myproject.clinic.healthpackage.service.HealthPackageScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể HealthPackageSchedule.
 */
@RestController
@RequestMapping("/api/health-package-schedules")
@RequiredArgsConstructor
public class HealthPackageScheduleController {

    private final HealthPackageScheduleService scheduleService;

    /**
     * Lấy danh sách tất cả các bản ghi.
     */
    @GetMapping
    public ResponseEntity<List<HealthPackageScheduleResponse>> getAll() {
        return ResponseEntity.ok(scheduleService.findAll());
    }

    /**
     * Tìm kiếm và lấy thông tin chi tiết của bản ghi theo mã định danh ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HealthPackageScheduleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.findById(id));
    }

    /**
     * Tạo mới và lưu bản ghi vào hệ thống.
     */
    @PostMapping
    public ResponseEntity<HealthPackageScheduleResponse> create(@RequestBody HealthPackageScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthPackageScheduleResponse> update(@PathVariable Long id,
            @RequestBody HealthPackageScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.update(id, request));
    }

    /**
     * Xóa bản ghi khỏi hệ thống.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
