package com.myproject.clinic.medicalrecord.controller;

import com.myproject.clinic.medicalrecord.dto.MedicalRecordRequest;
import com.myproject.clinic.medicalrecord.dto.MedicalRecordResponse;
import com.myproject.clinic.medicalrecord.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể MedicalRecord.
 */
@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    /**
     * Lấy danh sách tất cả các bản ghi.
     */
    @GetMapping
    public ResponseEntity<List<MedicalRecordResponse>> getAll() {
        return ResponseEntity.ok(medicalRecordService.findAll());
    }

    /**
     * Tìm kiếm và lấy thông tin chi tiết của bản ghi theo mã định danh ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordService.findById(id));
    }

    /**
     * Tạo mới và lưu bản ghi vào hệ thống.
     */
    @PostMapping
    public ResponseEntity<MedicalRecordResponse> create(@RequestBody MedicalRecordRequest request) {
        return ResponseEntity.ok(medicalRecordService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> update(@PathVariable Long id,
            @RequestBody MedicalRecordRequest request) {
        return ResponseEntity.ok(medicalRecordService.update(id, request));
    }

    /**
     * Xóa bản ghi khỏi hệ thống.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicalRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Phương thức: Lấy my records.
     */
    @GetMapping("/my-records")
    public ResponseEntity<List<MedicalRecordResponse>> getMyRecords() {
        return ResponseEntity.ok(medicalRecordService.findMyRecords());
    }

    /**
     * Phương thức: Lấy theo bệnh nhân id.
     */
    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponse>> getByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.findByPatientId(patientId));
    }
}
