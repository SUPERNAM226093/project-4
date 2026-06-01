package com.myproject.clinic.role.dto;

import lombok.*;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của Role.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private Long id;
    private String name;
    private Boolean isActive;
}
