package com.myproject.clinic.hospital.service;

import com.myproject.clinic.entity.Hospital;
import com.myproject.clinic.hospital.dto.HospitalResponse;
import com.myproject.clinic.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp dịch vụ (Service) xử lý logic nghiệp vụ và dữ liệu cho thực thể Hospital.
 */
@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    /**
     * Lấy danh sách tất cả các bản ghi.
     */
    public List<HospitalResponse> findAll() {
        return hospitalRepository.findAllByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Phương thức: Tìm kiếm theo đường dẫn rút gọn (slug).
     */
    public HospitalResponse findBySlug(String slug) {
        Hospital hospital = hospitalRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hospital not found: " + slug));
        return toResponse(hospital);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    /**
     * Chuyển đổi đối tượng thực thể (Entity) sang định dạng phản hồi (Response DTO).
     */
    private HospitalResponse toResponse(Hospital h) {
        List<String> specialtyList = (h.getSpecialties() != null && !h.getSpecialties().isBlank())
                ? Arrays.stream(h.getSpecialties().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return HospitalResponse.builder()
                .id(h.getId())
                .name(h.getName())
                .slug(h.getSlug())
                .shortDescription(h.getShortDescription())
                .description(h.getDescription())
                .address(h.getAddress())
                .hotline(h.getHotline())
                .workingHours(h.getWorkingHours())
                .specialties(specialtyList)
                .imageUrl(h.getImageUrl())
                .bannerUrl(h.getBannerUrl())
                .website(h.getWebsite())
                .verified(h.getVerified())
                .build();
    }
}
