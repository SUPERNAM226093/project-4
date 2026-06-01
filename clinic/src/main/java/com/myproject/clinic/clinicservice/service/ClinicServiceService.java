package com.myproject.clinic.clinicservice.service;

import com.myproject.clinic.clinicservice.dto.ClinicServiceRequest;
import com.myproject.clinic.clinicservice.dto.ClinicServiceResponse;
import com.myproject.clinic.config.FileStorageService;
import com.myproject.clinic.entity.ClinicService;
import com.myproject.clinic.entity.User;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.ClinicServiceRepository;
import com.myproject.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Lớp dịch vụ (Service) xử lý logic nghiệp vụ và dữ liệu cho thực thể ClinicService.
 */
@Service
@RequiredArgsConstructor
public class ClinicServiceService {

    private final ClinicServiceRepository clinicServiceRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    private static final String IMAGE_SUBDIRECTORY = "services";

    /**
     * Lấy danh sách tất cả các bản ghi.
     */
    public List<ClinicServiceResponse> findAll() {
        return clinicServiceRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Tìm kiếm và lấy thông tin chi tiết của bản ghi theo mã định danh ID.
     */
    public ClinicServiceResponse findById(Long id) {
        return toResponse(clinicServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id)));
    }

    /**
     * Tạo mới và lưu bản ghi vào hệ thống.
     */
    public ClinicServiceResponse create(ClinicServiceRequest request) {
        User createdBy = null;
        if (request.getCreatedById() != null) {
            createdBy = userRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getCreatedById()));
        }

        ClinicService service = ClinicService.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .type(request.getType())
                .durationMinutes(request.getDurationMinutes())
                .createdBy(createdBy)
                .build();

        return toResponse(clinicServiceRepository.save(service));
    }

    /**
     * Cập nhật thông tin chi tiết cho bản ghi.
     */
    public ClinicServiceResponse update(Long id, ClinicServiceRequest request) {
        ClinicService service = clinicServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));

        if (request.getName() != null)
            service.setName(request.getName());
        if (request.getDescription() != null)
            service.setDescription(request.getDescription());
        if (request.getPrice() != null)
            service.setPrice(request.getPrice());
        if (request.getType() != null)
            service.setType(request.getType());
        if (request.getDurationMinutes() != null)
            service.setDurationMinutes(request.getDurationMinutes());

        return toResponse(clinicServiceRepository.save(service));
    }

    /**
     * Xóa bản ghi khỏi hệ thống.
     */
    public void delete(Long id) {
        ClinicService service = clinicServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
        if (service.getFeatureImage() != null) {
            fileStorageService.delete(service.getFeatureImage());
        }
        clinicServiceRepository.deleteById(id);
    }

    /**
     * Phương thức: Upload feature image.
     */
    public ClinicServiceResponse uploadFeatureImage(Long id, MultipartFile file) {
        ClinicService service = clinicServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
        if (service.getFeatureImage() != null) {
            fileStorageService.delete(service.getFeatureImage());
        }
        String path = fileStorageService.store(file, IMAGE_SUBDIRECTORY);
        service.setFeatureImage(path);
        return toResponse(clinicServiceRepository.save(service));
    }

    /**
     * Phương thức: Xóa feature image.
     */
    public ClinicServiceResponse deleteFeatureImage(Long id) {
        ClinicService service = clinicServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
        if (service.getFeatureImage() != null) {
            fileStorageService.delete(service.getFeatureImage());
            service.setFeatureImage(null);
            clinicServiceRepository.save(service);
        }
        return toResponse(service);
    }

    /**
     * Chuyển đổi đối tượng thực thể (Entity) sang định dạng phản hồi (Response DTO).
     */
    private ClinicServiceResponse toResponse(ClinicService service) {
        return ClinicServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .price(service.getPrice())
                .type(service.getType())
                .durationMinutes(service.getDurationMinutes())
                .createdByName(service.getCreatedBy() != null ? service.getCreatedBy().getFullName() : null)
                .featureImageUrl(service.getFeatureImage() != null ? "/images/" + service.getFeatureImage() : null)
                .build();
    }
}
