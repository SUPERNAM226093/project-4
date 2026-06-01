package com.myproject.clinic.permissionmatrix.dto;

import com.myproject.clinic.permissionmatrix.PermissionModuleCatalog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của PermissionMatrixCatalog.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionMatrixCatalogResponse {
    private List<PermissionModuleCatalog.ModuleGroup> groups;
}
