package com.myproject.clinic.consultation.dto;

import lombok.*;

/**
 * Đối tượng chứa thông tin yêu cầu (Request DTO) để xử lý dữ liệu cho ApproveConsultation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveConsultationRequest {
    private String meetingLink;
}
