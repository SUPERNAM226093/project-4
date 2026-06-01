package com.myproject.clinic.room.controller;

import com.myproject.clinic.room.dto.RoomDTO;
import com.myproject.clinic.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể Room.
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     * Phương thức: Lấy hoạt động rooms.
     */
    @GetMapping
    public ResponseEntity<List<RoomDTO>> getActiveRooms() {
        return ResponseEntity.ok(roomService.getActiveRooms());
    }

    /**
     * Phương thức: Lấy tất cả rooms.
     */
    @GetMapping("/all")
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    /**
     * Phương thức: Lấy phòng theo id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    /**
     * Phương thức: Tạo mới phòng.
     */
    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO dto) {
        return ResponseEntity.ok(roomService.createRoom(dto));
    }

    /**
     * Phương thức: Cập nhật phòng.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Long id, @RequestBody RoomDTO dto) {
        return ResponseEntity.ok(roomService.updateRoom(id, dto));
    }

    /**
     * Phương thức: Xóa phòng.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<RoomDTO> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(roomService.uploadImage(id, file));
    }
}
