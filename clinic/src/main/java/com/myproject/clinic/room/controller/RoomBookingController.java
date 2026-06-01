package com.myproject.clinic.room.controller;

import com.myproject.clinic.room.dto.RoomBookingDTO;
import com.myproject.clinic.room.dto.RoomBookingRequest;
import com.myproject.clinic.room.service.RoomBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể RoomBooking.
 */
@RestController
@RequestMapping("/api/room-bookings")
@RequiredArgsConstructor
public class RoomBookingController {

    private final RoomBookingService bookingService;

    @PostMapping("/{userId}")
    public ResponseEntity<RoomBookingDTO> createBooking(
            @PathVariable Long userId,
            @RequestBody RoomBookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(userId, request));
    }

    /**
     * Phương thức: Lấy my bookings.
     */
    @GetMapping("/my/{userId}")
    public ResponseEntity<List<RoomBookingDTO>> getMyBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getMyBookings(userId));
    }

    /**
     * Phương thức: Lấy tất cả bookings.
     */
    @GetMapping("/all")
    public ResponseEntity<List<RoomBookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    /**
     * Phương thức: Lấy bookings theo người dùng.
     */
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<RoomBookingDTO>> getBookingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RoomBookingDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingService.updateStatus(id, status, reason));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomBookingDTO> updateBooking(
            @PathVariable Long id,
            @RequestBody RoomBookingRequest request) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

    /**
     * Phương thức: Xóa đặt chỗ.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/cancel/{userId}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestParam String reason) {
        bookingService.cancelBooking(userId, id, reason);
        return ResponseEntity.noContent().build();
    }
}
