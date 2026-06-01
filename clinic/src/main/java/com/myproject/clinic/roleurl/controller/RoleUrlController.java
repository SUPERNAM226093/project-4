package com.myproject.clinic.roleurl.controller;

import com.myproject.clinic.roleurl.dto.RoleUrlResponse;
import com.myproject.clinic.roleurl.service.RoleUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RoleUrlController — Quản lý API phân quyền URL.
 *
 * Sau khi đơn giản hóa hệ thống:
 * - Chỉ còn 1 endpoint: GET /api/role-urls/by-role
 * - Endpoint này được Sidebar FE gọi để biết user có quyền truy cập menu nào không.
 *
 * Đã xóa: POST, PUT, DELETE (không còn phân quyền thủ công).
 */
@RestController
@RequestMapping("/api/role-urls")
@RequiredArgsConstructor
public class RoleUrlController {

    private final RoleUrlService roleUrlService;

    /**
     * Lấy tất cả URL pattern mà role này được phép truy cập.
     * Sidebar FE dùng để lọc danh sách menu hiển thị.
     *
     * @param roleName Tên role (STAFF / DOCTOR)
     * @return Danh sách { urlPattern, httpMethod, ... }
     */
    @GetMapping("/my-permissions")
    public ResponseEntity<List<RoleUrlResponse>> getMyPermissions() {
        String roleName = com.myproject.clinic.utils.SecurityUtils.getCurrentUserRole();
        if (roleName == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(roleUrlService.findByRoleName(roleName));
    }
}
