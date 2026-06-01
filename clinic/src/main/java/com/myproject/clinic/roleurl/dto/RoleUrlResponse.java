package com.myproject.clinic.roleurl.dto;

import lombok.*;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của RoleUrl.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleUrlResponse {
    private Long id;
    private Long roleId;
    private String roleName;
    private String urlPattern;
    private String httpMethod;
    private String description;
    private String permissionSource;
    private String matrixModule;
}
