package com.myproject.clinic.prescription.controller;

import com.myproject.clinic.prescription.dto.PrescriptionRequest;
import com.myproject.clinic.prescription.dto.PrescriptionResponse;
import com.myproject.clinic.prescription.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể Prescription.
 */
@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    /**
     * Lấy danh sách tất cả các bản ghi.
     */
    @GetMapping
    public ResponseEntity<List<PrescriptionResponse>> getAll() {
        return ResponseEntity.ok(prescriptionService.findAll());
    }

    /**
     * Phương thức: Lấy theo người dùng id.
     */
    @GetMapping("/patient/{userId}")
    public ResponseEntity<List<PrescriptionResponse>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(prescriptionService.findByUserId(userId));
    }

    /**
     * Tìm kiếm và lấy thông tin chi tiết của bản ghi theo mã định danh ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.findById(id));
    }

    /**
     * Tạo mới và lưu bản ghi vào hệ thống.
     */
    @PostMapping
    public ResponseEntity<PrescriptionResponse> create(@RequestBody PrescriptionRequest request) {
        return ResponseEntity.ok(prescriptionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> update(@PathVariable Long id,
            @RequestBody PrescriptionRequest request) {
        return ResponseEntity.ok(prescriptionService.update(id, request));
    }

    /**
     * Xóa bản ghi khỏi hệ thống.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prescriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
