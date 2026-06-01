package com.myproject.clinic.serviceregistration.controller;

import com.myproject.clinic.serviceregistration.dto.ServiceRegistrationRequest;
import com.myproject.clinic.serviceregistration.dto.ServiceRegistrationResponse;
import com.myproject.clinic.serviceregistration.service.ServiceRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể ServiceRegistration.
 */
@RestController
@RequestMapping("/api/service-registrations")
@RequiredArgsConstructor
public class ServiceRegistrationController {

    private final ServiceRegistrationService registrationService;

    /**
     * Lấy danh sách tất cả các bản ghi.
     */
    @GetMapping
    public ResponseEntity<List<ServiceRegistrationResponse>> getAll() {
        return ResponseEntity.ok(registrationService.findAll());
    }

    /**
     * Tìm kiếm và lấy thông tin chi tiết của bản ghi theo mã định danh ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceRegistrationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.findById(id));
    }

    /**
     * Tạo mới và lưu bản ghi vào hệ thống.
     */
    @PostMapping
    public ResponseEntity<ServiceRegistrationResponse> create(@RequestBody ServiceRegistrationRequest request) {
        return ResponseEntity.ok(registrationService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceRegistrationResponse> update(@PathVariable Long id,
            @RequestBody ServiceRegistrationRequest request) {
        return ResponseEntity.ok(registrationService.update(id, request));
    }

    /**
     * Xóa bản ghi khỏi hệ thống.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        registrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
