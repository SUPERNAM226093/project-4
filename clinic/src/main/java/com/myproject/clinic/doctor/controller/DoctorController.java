package com.myproject.clinic.doctor.controller;

import com.myproject.clinic.doctor.dto.DoctorRequest;
import com.myproject.clinic.doctor.dto.DoctorResponse;
import com.myproject.clinic.doctor.service.DoctorService;
import com.myproject.clinic.schedule.dto.DoctorScheduleResponse;
import com.myproject.clinic.schedule.service.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể Doctor.
 */
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "false") boolean includeInactive) {
        if (name != null && !name.trim().isEmpty()) {
            return ResponseEntity.ok(doctorService.searchByName(name, includeInactive));
        }
        return ResponseEntity.ok(doctorService.findAll(includeInactive));
    }

    /**
     * Tìm kiếm và lấy thông tin chi tiết của bản ghi theo mã định danh ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.findById(id));
    }

    /**
     * Tạo mới và lưu bản ghi vào hệ thống.
     */
    @PostMapping
    public ResponseEntity<DoctorResponse> create(@RequestBody DoctorRequest request) {
        return ResponseEntity.ok(doctorService.create(request));
    }

    /**
     * Cập nhật thông tin chi tiết cho bản ghi.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> update(@PathVariable Long id, @RequestBody DoctorRequest request) {
        return ResponseEntity.ok(doctorService.update(id, request));
    }

    /**
     * Phương thức: Cập nhật trạng thái.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<DoctorResponse> updateStatus(@PathVariable Long id, @RequestBody com.myproject.clinic.doctor.dto.DoctorStatusRequest request) {
        return ResponseEntity.ok(doctorService.updateStatus(id, request.getStatus()));
    }

    /**
     * Xóa bản ghi khỏi hệ thống.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/feature-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DoctorResponse> uploadFeatureImage(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(doctorService.uploadFeatureImage(id, file));
    }

    /**
     * Phương thức: Xóa feature image.
     */
    @DeleteMapping("/{id}/feature-image")
    public ResponseEntity<DoctorResponse> deleteFeatureImage(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.deleteFeatureImage(id));
    }

    /**
     * Phương thức: Lấy schedules.
     */
    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<DoctorScheduleResponse>> getSchedules(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.findByDoctorId(id));
    }

    @GetMapping("/{id}/available-slots")
    public ResponseEntity<List<DoctorScheduleResponse>> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam("date") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date) {
        return ResponseEntity.ok(scheduleService.findByDoctorIdAndDate(id, date));
    }
}
