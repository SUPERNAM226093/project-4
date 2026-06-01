package com.myproject.clinic.permissionmatrix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho PermissionMatrixPut.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionMatrixPutRequest {
    private Map<String, ModuleActionsDto> modules;
}
