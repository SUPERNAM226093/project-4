package com.myproject.clinic.specialization.controller;

import com.myproject.clinic.specialization.dto.SpecializationRequest;
import com.myproject.clinic.specialization.dto.SpecializationResponse;
import com.myproject.clinic.specialization.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể Specialization.
 */
@RestController
@RequestMapping("/api/specializations")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationService specializationService;

    @GetMapping
    public ResponseEntity<List<SpecializationResponse>> getAll(
            @RequestParam(required = false, defaultValue = "false") boolean includeInactive) {
        return ResponseEntity.ok(specializationService.findAll(includeInactive));
    }

    /**
     * Tìm kiếm và lấy thông tin chi tiết của bản ghi theo mã định danh ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpecializationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(specializationService.findById(id));
    }

    /**
     * Tạo mới và lưu bản ghi vào hệ thống.
     */
    @PostMapping
    public ResponseEntity<SpecializationResponse> create(@RequestBody SpecializationRequest request) {
        return ResponseEntity.ok(specializationService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecializationResponse> update(@PathVariable Long id,
            @RequestBody SpecializationRequest request) {
        return ResponseEntity.ok(specializationService.update(id, request));
    }

    /**
     * Xóa bản ghi khỏi hệ thống.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        specializationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/feature-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SpecializationResponse> uploadFeatureImage(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(specializationService.uploadFeatureImage(id, file));
    }

    /**
     * Phương thức: Xóa feature image.
     */
    @DeleteMapping("/{id}/feature-image")
    public ResponseEntity<SpecializationResponse> deleteFeatureImage(@PathVariable Long id) {
        return ResponseEntity.ok(specializationService.deleteFeatureImage(id));
    }
}
