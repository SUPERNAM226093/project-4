package com.myproject.clinic.healthpackage.controller;

import com.myproject.clinic.healthpackage.dto.HealthPackageRequest;
import com.myproject.clinic.healthpackage.dto.HealthPackageResponse;
import com.myproject.clinic.healthpackage.dto.HealthPackageScheduleResponse;
import com.myproject.clinic.healthpackage.service.HealthPackageScheduleService;
import com.myproject.clinic.healthpackage.service.HealthPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể HealthPackage.
 */
@RestController
@RequestMapping("/api/health-packages")
@RequiredArgsConstructor
public class HealthPackageController {

    private final HealthPackageService healthPackageService;
    private final HealthPackageScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<HealthPackageResponse>> getAll(
            @RequestParam(required = false, defaultValue = "false") boolean includeInactive) {
        return ResponseEntity.ok(healthPackageService.findAll(includeInactive));
    }

    /**
     * Tìm kiếm và lấy thông tin chi tiết của bản ghi theo mã định danh ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HealthPackageResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(healthPackageService.findById(id));
    }

    /**
     * Tạo mới và lưu bản ghi vào hệ thống.
     */
    @PostMapping
    public ResponseEntity<HealthPackageResponse> create(@RequestBody HealthPackageRequest request) {
        return ResponseEntity.ok(healthPackageService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthPackageResponse> update(@PathVariable Long id,
            @RequestBody HealthPackageRequest request) {
        return ResponseEntity.ok(healthPackageService.update(id, request));
    }

    /**
     * Xóa bản ghi khỏi hệ thống.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        healthPackageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/feature-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HealthPackageResponse> uploadFeatureImage(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(healthPackageService.uploadFeatureImage(id, file));
    }

    /**
     * Phương thức: Xóa feature image.
     */
    @DeleteMapping("/{id}/feature-image")
    public ResponseEntity<HealthPackageResponse> deleteFeatureImage(@PathVariable Long id) {
        return ResponseEntity.ok(healthPackageService.deleteFeatureImage(id));
    }

    /**
     * Phương thức: Lấy schedules.
     */
    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<HealthPackageScheduleResponse>> getSchedules(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.findByHealthPackageId(id));
    }
}
