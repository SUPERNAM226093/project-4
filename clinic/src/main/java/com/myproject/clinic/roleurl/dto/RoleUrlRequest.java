package com.myproject.clinic.roleurl.dto;

import lombok.*;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho RoleUrl.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleUrlRequest {
    private Long roleId;
    private String urlPattern;
    private String httpMethod;
    private String description;
}
