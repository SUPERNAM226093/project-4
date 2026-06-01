package com.myproject.clinic.user.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của User.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String roleName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
