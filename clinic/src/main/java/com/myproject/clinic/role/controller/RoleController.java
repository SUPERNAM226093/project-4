package com.myproject.clinic.role.controller;

import com.myproject.clinic.role.dto.RoleRequest;
import com.myproject.clinic.role.dto.RoleResponse;
import com.myproject.clinic.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể Role.
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * Lấy danh sách tất cả các bản ghi.
     */
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    /**
     * Tìm kiếm và lấy thông tin chi tiết của bản ghi theo mã định danh ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    /**
     * Tạo mới và lưu bản ghi vào hệ thống.
     */
    @PostMapping
    public ResponseEntity<RoleResponse> create(@RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.create(request));
    }

    /**
     * Cập nhật thông tin chi tiết cho bản ghi.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> update(@PathVariable Long id, @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.update(id, request));
    }

    /**
     * Xóa bản ghi khỏi hệ thống.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
