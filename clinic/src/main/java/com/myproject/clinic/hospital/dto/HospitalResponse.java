package com.myproject.clinic.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Đối tượng chứa dữ liệu phản hồi (Response DTO) trả về thông tin của Hospital.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalResponse {

    private Long id;
    private String name;
    private String slug;
    private String shortDescription;
    private String description;
    private String address;
    private String hotline;
    private String workingHours;
    private List<String> specialties;
    private String imageUrl;
    private String bannerUrl;
    private String website;
    private Boolean verified;
}
