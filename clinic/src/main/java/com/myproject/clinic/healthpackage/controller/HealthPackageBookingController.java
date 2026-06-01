package com.myproject.clinic.healthpackage.controller;

import com.myproject.clinic.healthpackage.dto.HealthPackageBookingRequest;
import com.myproject.clinic.healthpackage.dto.HealthPackageBookingResponse;
import com.myproject.clinic.healthpackage.service.HealthPackageBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể HealthPackageBooking.
 */
@RestController
@RequestMapping("/api/health-package-bookings")
@RequiredArgsConstructor
public class HealthPackageBookingController {

    private final HealthPackageBookingService bookingService;

    /**
     * Tạo mới và lưu bản ghi vào hệ thống.
     */
    @PostMapping
    public ResponseEntity<HealthPackageBookingResponse> create(@RequestBody HealthPackageBookingRequest request) {
        return ResponseEntity.ok(bookingService.create(request));
    }

    /**
     * Phương thức: Lấy theo bệnh nhân.
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<HealthPackageBookingResponse>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(bookingService.findByPatientId(patientId));
    }

    /**
     * Lấy danh sách tất cả các bản ghi.
     */
    @GetMapping
    public ResponseEntity<List<HealthPackageBookingResponse>> getAll() {
        return ResponseEntity.ok(bookingService.findAll());
    }

    @GetMapping("/booked-slots/{packageId}")
    public ResponseEntity<List<String>> getBookedSlots(@PathVariable Long packageId, @RequestParam("date") String dateStr) {
        return ResponseEntity.ok(bookingService.getBookedSlots(packageId, java.time.LocalDate.parse(dateStr)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<HealthPackageBookingResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(bookingService.updateStatus(id, body.get("status")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthPackageBookingResponse> update(
            @PathVariable Long id,
            @RequestBody HealthPackageBookingRequest request) {
        return ResponseEntity.ok(bookingService.update(id, request));
    }

    /**
     * Xóa bản ghi khỏi hệ thống.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Long patientId = Long.valueOf(body.get("patientId").toString());
        String reason = body.getOrDefault("reason", "").toString();
        bookingService.cancel(id, patientId, reason);
        return ResponseEntity.ok().build();
    }
}
