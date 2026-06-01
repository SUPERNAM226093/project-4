package com.myproject.clinic.permissionmatrix.controller;

import com.myproject.clinic.permissionmatrix.dto.PermissionMatrixCatalogResponse;
import com.myproject.clinic.permissionmatrix.dto.PermissionMatrixPutRequest;
import com.myproject.clinic.permissionmatrix.dto.PermissionMatrixResponse;
import com.myproject.clinic.permissionmatrix.service.PermissionMatrixService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Lớp điều khiển (Controller) xử lý các yêu cầu HTTP API cho thực thể PermissionMatrix.
 */
@RestController
@RequiredArgsConstructor
public class PermissionMatrixController {

    private final PermissionMatrixService permissionMatrixService;

    /**
     * Phương thức: Lấy catalog.
     */
    @GetMapping("/api/permission-matrix/catalog")
    public ResponseEntity<PermissionMatrixCatalogResponse> getCatalog() {
        return ResponseEntity.ok(permissionMatrixService.getCatalog());
    }

    /**
     * Phương thức: Lấy ma trận.
     */
    @GetMapping("/api/roles/{roleId}/permission-matrix")
    public ResponseEntity<PermissionMatrixResponse> getMatrix(@PathVariable Long roleId) {
        return ResponseEntity.ok(permissionMatrixService.getMatrix(roleId));
    }

    @PutMapping("/api/roles/{roleId}/permission-matrix")
    public ResponseEntity<PermissionMatrixResponse> putMatrix(
            @PathVariable Long roleId,
            @RequestBody PermissionMatrixPutRequest request) {
        return ResponseEntity.ok(permissionMatrixService.putMatrix(roleId, request));
    }
}
