package com.myproject.clinic.permissionmatrix.dto;

import com.myproject.clinic.roleurl.dto.RoleUrlResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của PermissionMatrix.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionMatrixResponse {
    private Long roleId;
    private String roleName;
    private Map<String, ModuleActionsDto> modules;
    private List<RoleUrlResponse> manualPermissions;
    private List<PermissionWarningDto> warnings;
}
