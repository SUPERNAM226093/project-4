package com.myproject.clinic.permissionmatrix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lớp class PermissionWarningDto trong hệ thống.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionWarningDto {
    private String code;
    private String message;
    private String moduleKey;
}
