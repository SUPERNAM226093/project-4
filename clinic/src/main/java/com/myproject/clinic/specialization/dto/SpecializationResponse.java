package com.myproject.clinic.specialization.dto;

import lombok.*;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của Specialization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecializationResponse {
    private Long id;
    private String name;
    private String description;
    private String featureImageUrl;
    private String status;
}
