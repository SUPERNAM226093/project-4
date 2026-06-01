package com.myproject.clinic.role.dto;

import lombok.*;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho Role.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {
    private String name;
    private Boolean isActive;
}
