package com.myproject.clinic.specialization.service;

import com.myproject.clinic.config.FileStorageService;
import com.myproject.clinic.entity.Specialization;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.SpecializationRepository;
import com.myproject.clinic.repository.DoctorRepository;
import com.myproject.clinic.repository.OnlineConsultationRepository;
import com.myproject.clinic.repository.AppointmentRepository;
import com.myproject.clinic.specialization.dto.SpecializationRequest;
import com.myproject.clinic.specialization.dto.SpecializationResponse;
import com.myproject.clinic.utils.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final DoctorRepository doctorRepository;
    private final OnlineConsultationRepository onlineConsultationRepository;
    private final AppointmentRepository appointmentRepository;
    private final FileStorageService fileStorageService;
    private final EmbeddingService embeddingService;

    private static final String IMAGE_SUBDIRECTORY = "specializations";

    public List<SpecializationResponse> findAll() {
        return findAll(false);
    }

    public List<SpecializationResponse> findAll(boolean includeInactive) {
        if (includeInactive) {
            return specializationRepository.findAll().stream().map(this::toResponse).toList();
        }
        return specializationRepository.findByStatus("ACTIVE").stream().map(this::toResponse).toList();
    }

    public SpecializationResponse findById(Long id) {
        return toResponse(specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization", id)));
    }

    public SpecializationResponse create(SpecializationRequest request) {
        Specialization specialization = Specialization.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        generateEmbedding(specialization);
        return toResponse(specializationRepository.save(specialization));
    }

    public SpecializationResponse update(Long id, SpecializationRequest request) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization", id));

        if (request.getStatus() != null) {
            String newStatus = request.getStatus();
            if ("INACTIVE".equalsIgnoreCase(newStatus) && !"INACTIVE".equalsIgnoreCase(specialization.getStatus())) {
                // Kiểm tra các lịch hẹn tương lai của các bác sĩ thuộc chuyên khoa này
                java.time.LocalDate today = java.time.LocalDate.now();
                java.time.LocalTime nowTime = java.time.LocalTime.now();
                String nowTimeStr = nowTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                List<String> DONE_STATUSES = List.of("CANCELLED", "REJECTED", "COMPLETED");

                List<com.myproject.clinic.entity.Doctor> doctors = doctorRepository.findBySpecializationId(id);
                for (com.myproject.clinic.entity.Doctor doctor : doctors) {
                    boolean hasFutureAppointments = appointmentRepository
                            .existsFutureActiveAppointmentByDoctorId(doctor.getId(), today, nowTime, DONE_STATUSES);
                    if (hasFutureAppointments) {
                        throw new IllegalArgumentException("Không thể ngưng hoạt động chuyên khoa này vì bác sĩ " 
                                + doctor.getUser().getFullName() + " vẫn còn lịch khám trong tương lai.");
                    }
                    boolean hasFutureConsultations = onlineConsultationRepository
                            .existsFutureActiveConsultationByDoctorId(doctor.getId(), today, nowTimeStr, List.of("CANCELLED"));
                    if (hasFutureConsultations) {
                        throw new IllegalArgumentException("Không thể ngưng hoạt động chuyên khoa này vì bác sĩ " 
                                + doctor.getUser().getFullName() + " vẫn còn lịch tư vấn online trong tương lai.");
                    }
                }
            }
            specialization.setStatus(newStatus.toUpperCase());
        }

        specialization.setName(request.getName());
        specialization.setDescription(request.getDescription());
        generateEmbedding(specialization);
        return toResponse(specializationRepository.save(specialization));
    }

    @org.springframework.transaction.annotation.Transactional
    public void delete(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization", id));

        // Gỡ bỏ chuyên khoa khỏi tất cả bác sĩ đang thuộc chuyên khoa này
        List<com.myproject.clinic.entity.Doctor> doctors = doctorRepository.findBySpecializationId(id);
        for (com.myproject.clinic.entity.Doctor doctor : doctors) {
            doctor.setSpecialization(null);
            doctorRepository.save(doctor);
        }

        // Gỡ bỏ chuyên khoa khỏi các đơn Tư vấn Online
        List<com.myproject.clinic.entity.OnlineConsultation> consultations = onlineConsultationRepository.findBySpecializationId(id);
        for (com.myproject.clinic.entity.OnlineConsultation c : consultations) {
            c.setSpecialization(null);
            onlineConsultationRepository.save(c);
        }

        if (specialization.getFeatureImage() != null) {
            fileStorageService.delete(specialization.getFeatureImage());
        }
        specializationRepository.deleteById(id);
    }

    public SpecializationResponse uploadFeatureImage(Long id, MultipartFile file) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization", id));
        if (specialization.getFeatureImage() != null) {
            fileStorageService.delete(specialization.getFeatureImage());
        }
        String path = fileStorageService.store(file, IMAGE_SUBDIRECTORY);
        specialization.setFeatureImage(path);
        return toResponse(specializationRepository.save(specialization));
    }

    public SpecializationResponse deleteFeatureImage(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization", id));
        if (specialization.getFeatureImage() != null) {
            fileStorageService.delete(specialization.getFeatureImage());
            specialization.setFeatureImage(null);
            specializationRepository.save(specialization);
        }
        return toResponse(specialization);
    }

    private SpecializationResponse toResponse(Specialization s) {
        return SpecializationResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .status(s.getStatus())
                .featureImageUrl(s.getFeatureImage() != null ? "/images/" + s.getFeatureImage() : null)
                .build();
    }

    private void generateEmbedding(Specialization specialization) {
        try {
            String text = specialization.getName() + " " +
                    (specialization.getDescription() != null ? specialization.getDescription() : "");
            List<Double> embedding = embeddingService.getEmbedding(text.trim());
            specialization.setEmbedding(embeddingService.embeddingToJson(embedding));
        } catch (Exception e) {
            // Don't fail the create/update if embedding fails
        }
    }
}
