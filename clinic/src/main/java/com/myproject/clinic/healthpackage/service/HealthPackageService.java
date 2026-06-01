package com.myproject.clinic.healthpackage.service;

import com.myproject.clinic.config.FileStorageService;
import com.myproject.clinic.entity.HealthPackage;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.healthpackage.dto.HealthPackageRequest;
import com.myproject.clinic.healthpackage.dto.HealthPackageResponse;
import com.myproject.clinic.repository.HealthPackageRepository;
import com.myproject.clinic.repository.HealthPackageBookingRepository;
import com.myproject.clinic.repository.AppointmentRepository;
import com.myproject.clinic.utils.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthPackageService {

    private final HealthPackageRepository healthPackageRepository;
    private final AppointmentRepository appointmentRepository;
    private final HealthPackageBookingRepository healthPackageBookingRepository;
    private final FileStorageService fileStorageService;
    private final EmbeddingService embeddingService;

    private static final String IMAGE_SUBDIRECTORY = "health-packages";

    public List<HealthPackageResponse> findAll() {
        return findAll(false);
    }

    public List<HealthPackageResponse> findAll(boolean includeInactive) {
        if (includeInactive) {
            return healthPackageRepository.findAll().stream().map(this::toResponse).toList();
        }
        return healthPackageRepository.findByStatus("ACTIVE").stream().map(this::toResponse).toList();
    }

    public HealthPackageResponse findById(Long id) {
        return toResponse(healthPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HealthPackage", id)));
    }

    public HealthPackageResponse create(HealthPackageRequest request) {
        HealthPackage hp = HealthPackage.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        generateEmbedding(hp);
        return toResponse(healthPackageRepository.save(hp));
    }

    public HealthPackageResponse update(Long id, HealthPackageRequest request) {
        HealthPackage hp = healthPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HealthPackage", id));

        if (request.getStatus() != null) {
            String newStatus = request.getStatus();
            if ("INACTIVE".equalsIgnoreCase(newStatus) && !"INACTIVE".equalsIgnoreCase(hp.getStatus())) {
                // Kiểm tra lịch tương lai của Gói khám
                java.time.LocalDate today = java.time.LocalDate.now();
                java.time.LocalTime nowTime = java.time.LocalTime.now();
                List<String> DONE_STATUSES = List.of("CANCELLED", "REJECTED", "COMPLETED");

                boolean hasFutureBookings = healthPackageBookingRepository
                        .existsFutureActiveBookingByHealthPackageId(id, today, nowTime, DONE_STATUSES);
                if (hasFutureBookings) {
                    throw new IllegalArgumentException("Không thể ngưng hoạt động gói khám này vì còn lịch đặt gói trong tương lai.");
                }

                boolean hasFutureAppointments = appointmentRepository
                        .existsFutureActiveAppointmentByHealthPackageId(id, today, nowTime, DONE_STATUSES);
                if (hasFutureAppointments) {
                    throw new IllegalArgumentException("Không thể ngưng hoạt động gói khám này vì còn lịch hẹn khám theo gói trong tương lai.");
                }
            }
            hp.setStatus(newStatus.toUpperCase());
        }

        if (request.getName() != null)
            hp.setName(request.getName());
        if (request.getDescription() != null)
            hp.setDescription(request.getDescription());
        if (request.getPrice() != null)
            hp.setPrice(request.getPrice());
        generateEmbedding(hp);
        return toResponse(healthPackageRepository.save(hp));
    }

    public void delete(Long id) {
        HealthPackage hp = healthPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HealthPackage", id));
        
        // Kiểm tra xem gói khám này có đang được sử dụng trong lịch hẹn nào không
        boolean isUsed = !appointmentRepository.findByHealthPackageId(id).isEmpty();
        if (isUsed) {
            throw new RuntimeException("Không thể xóa gói khám này vì đã có bệnh nhân đăng ký sử dụng. Vui lòng giữ lại để bảo toàn lịch sử khám.");
        }

        if (hp.getFeatureImage() != null) {
            fileStorageService.delete(hp.getFeatureImage());
        }
        healthPackageRepository.deleteById(id);
    }

    public HealthPackageResponse uploadFeatureImage(Long id, MultipartFile file) {
        System.out.println(">>> Received request to upload image for HealthPackage ID: " + id);
        System.out.println(">>> File name: " + file.getOriginalFilename() + ", Size: " + file.getSize());
        
        HealthPackage hp = healthPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HealthPackage", id));
        if (hp.getFeatureImage() != null) {
            fileStorageService.delete(hp.getFeatureImage());
        }
        String path = fileStorageService.store(file, IMAGE_SUBDIRECTORY);
        System.out.println(">>> File stored at: " + path);
        
        hp.setFeatureImage(path);
        return toResponse(healthPackageRepository.save(hp));
    }

    public HealthPackageResponse deleteFeatureImage(Long id) {
        HealthPackage hp = healthPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HealthPackage", id));
        if (hp.getFeatureImage() != null) {
            fileStorageService.delete(hp.getFeatureImage());
            hp.setFeatureImage(null);
            healthPackageRepository.save(hp);
        }
        return toResponse(hp);
    }

    private HealthPackageResponse toResponse(HealthPackage hp) {
        return HealthPackageResponse.builder()
                .id(hp.getId())
                .name(hp.getName())
                .description(hp.getDescription())
                .price(hp.getPrice())
                .status(hp.getStatus())
                .featureImageUrl(hp.getFeatureImage() != null ? "/images/" + hp.getFeatureImage() : null)
                .createdAt(hp.getCreatedAt())
                .build();
    }

    private void generateEmbedding(HealthPackage hp) {
        try {
            String text = hp.getName() + " " +
                    (hp.getDescription() != null ? hp.getDescription() : "");
            List<Double> embedding = embeddingService.getEmbedding(text.trim());
            hp.setEmbedding(embeddingService.embeddingToJson(embedding));
        } catch (Exception e) {
            // Don't fail the create/update if embedding fails
        }
    }
}
