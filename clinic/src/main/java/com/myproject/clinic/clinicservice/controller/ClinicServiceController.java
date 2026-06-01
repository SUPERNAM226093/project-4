package com.myproject.clinic.clinicservice.controller;

import com.myproject.clinic.clinicservice.dto.ClinicServiceRequest;
import com.myproject.clinic.clinicservice.dto.ClinicServiceResponse;
import com.myproject.clinic.clinicservice.service.ClinicServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//GET  http://localhost:8080/api/services/1
@Slf4j
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ClinicServiceController {

    private final ClinicServiceService clinicServiceService;

    /**
     * Lấy danh sách tất cả các bản ghi.
     */
    @GetMapping
    public ResponseEntity<List<ClinicServiceResponse>> getAll() {
        log.info("REST request to get all clinic services");
        return ResponseEntity.ok(clinicServiceService.findAll());
    }

    /**
     * Tìm kiếm và lấy thông tin chi tiết của bản ghi theo mã định danh ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClinicServiceResponse> getById(@PathVariable Long id) {
        log.info("REST request to get clinic service by id : {}", id);
        return ResponseEntity.ok(clinicServiceService.findById(id));
    }

    /**
     * Tạo mới và lưu bản ghi vào hệ thống.
     */
    @PostMapping
    public ResponseEntity<ClinicServiceResponse> create(@RequestBody ClinicServiceRequest request) {
        log.info("REST request to create clinic service : {}", request);
        return ResponseEntity.ok(clinicServiceService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicServiceResponse> update(@PathVariable Long id,
            @RequestBody ClinicServiceRequest request) {
        log.info("REST request to update clinic service id : {}, data : {}", id, request);
        return ResponseEntity.ok(clinicServiceService.update(id, request));
    }

    /**
     * Xóa bản ghi khỏi hệ thống.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("REST request to delete clinic service by id : {}", id);
        clinicServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/feature-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClinicServiceResponse> uploadFeatureImage(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        log.info("REST request to upload feature image for clinic service id : {}", id);
        return ResponseEntity.ok(clinicServiceService.uploadFeatureImage(id, file));
    }

    /**
     * Phương thức: Xóa feature image.
     */
    @DeleteMapping("/{id}/feature-image")
    public ResponseEntity<ClinicServiceResponse> deleteFeatureImage(@PathVariable Long id) {
        log.info("REST request to delete feature image for clinic service id : {}", id);
        return ResponseEntity.ok(clinicServiceService.deleteFeatureImage(id));
    }
}
