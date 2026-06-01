package com.myproject.clinic.permissionmatrix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lớp class ModuleActionsDto trong hệ thống.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleActionsDto {
    private boolean view;
    private boolean create;
    private boolean edit;
    private boolean delete;
}
