package com.myproject.clinic.consultation.controller;

import com.myproject.clinic.consultation.dto.*;
import com.myproject.clinic.consultation.service.OnlineConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/online-consultations")
@RequiredArgsConstructor
public class OnlineConsultationController {

    private final OnlineConsultationService consultationService;

    /** Bệnh nhân tạo đơn tư vấn mới */
    @PostMapping
    public ResponseEntity<OnlineConsultationResponse> create(
            @RequestBody OnlineConsultationRequest request) {
        return ResponseEntity.ok(consultationService.create(request));
    }

    /** Lấy chi tiết đơn (dùng cho trang thanh toán) - có kiểm tra quyền sở hữu */
    @GetMapping("/{id}")
    public ResponseEntity<OnlineConsultationResponse> getById(
            @PathVariable Long id,
            @RequestParam(required = false) Long patientId) {
        return ResponseEntity.ok(consultationService.getById(id, patientId));
    }

    /** Bệnh nhân xem lịch sử tư vấn online của mình */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<OnlineConsultationResponse>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(consultationService.getByPatientId(patientId));
    }

    @GetMapping("/booked-slots/{doctorId}")
    public ResponseEntity<List<String>> getBookedSlots(
            @PathVariable Long doctorId, 
            @RequestParam("date") String dateStr) {
        return ResponseEntity.ok(consultationService.getBookedSlots(doctorId, java.time.LocalDate.parse(dateStr)));
    }

    /** Nhân viên/Admin xem tất cả đơn (lọc theo status) */
    @GetMapping
    public ResponseEntity<List<OnlineConsultationResponse>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long doctorId) {
        return ResponseEntity.ok(consultationService.getAllFiltered(status, doctorId));
    }

    /** Nhân viên duyệt đơn + thêm meeting link */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<OnlineConsultationResponse> approve(
            @PathVariable Long id,
            @RequestBody ApproveConsultationRequest request) {
        return ResponseEntity.ok(consultationService.approve(id, request));
    }

    /** Bệnh nhân hủy đơn */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            @RequestParam Long patientId) {
        consultationService.cancel(id, patientId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        consultationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<OnlineConsultationResponse> update(
        @PathVariable Long id,
        @RequestBody OnlineConsultationRequest request) {
        return ResponseEntity.ok(consultationService.update(id, request));
    }

    @GetMapping("/{id}/vnpay-payment-url")
    public ResponseEntity<java.util.Map<String, String>> getVnPayPaymentUrl(
        @PathVariable Long id,
        jakarta.servlet.http.HttpServletRequest request) {
        String ipAddr = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ipAddr)) {
            ipAddr = "127.0.0.1";
        }
        String paymentUrl = consultationService.createVnPayPaymentUrl(id, ipAddr);
        return ResponseEntity.ok(java.util.Map.of("paymentUrl", paymentUrl));
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<java.util.Map<String, Object>> vnpayCallback(
        @RequestParam java.util.Map<String, String> params) {
        boolean success = consultationService.processVnPayCallback(params);
        return ResponseEntity.ok(java.util.Map.of("success", success));
    }
}
