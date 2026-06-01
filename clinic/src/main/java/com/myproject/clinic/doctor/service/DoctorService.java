package com.myproject.clinic.doctor.service;

import com.myproject.clinic.config.FileStorageService;
import com.myproject.clinic.doctor.dto.DoctorRequest;
import com.myproject.clinic.doctor.dto.DoctorResponse;
import com.myproject.clinic.entity.Doctor;
import com.myproject.clinic.entity.Specialization;
import com.myproject.clinic.entity.User;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.DoctorRepository;
import com.myproject.clinic.repository.SpecializationRepository;
import com.myproject.clinic.repository.UserRepository;
import com.myproject.clinic.repository.AppointmentRepository;
import com.myproject.clinic.repository.MedicalRecordRepository;
import com.myproject.clinic.repository.PrescriptionRepository;
import com.myproject.clinic.repository.OnlineConsultationRepository;
import com.myproject.clinic.repository.DoctorScheduleRepository;
import com.myproject.clinic.utils.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final OnlineConsultationRepository onlineConsultationRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final FileStorageService fileStorageService;
    private final EmbeddingService embeddingService;

    private static final String IMAGE_SUBDIRECTORY = "doctors";

    public List<DoctorResponse> findAll() {
        return findAll(false);
    }

    public List<DoctorResponse> findAll(boolean includeInactive) {
        if (includeInactive) {
            return doctorRepository.findAll().stream().map(this::toResponse).toList();
        }
        return doctorRepository.findByUser_Status("ACTIVE").stream().map(this::toResponse).toList();
    }

    public List<DoctorResponse> searchByName(String name) {
        return searchByName(name, false);
    }

    public List<DoctorResponse> searchByName(String name, boolean includeInactive) {
        if (name == null || name.trim().isEmpty()) {
            return findAll(includeInactive);
        }
        if (includeInactive) {
            return doctorRepository.findByUser_FullNameContainingIgnoreCase(name.trim()).stream()
                    .map(this::toResponse).toList();
        }
        return doctorRepository.findByUser_FullNameContainingIgnoreCaseAndUser_Status(name.trim(), "ACTIVE").stream()
                .map(this::toResponse).toList();
    }

    public DoctorResponse findById(Long id) {
        return toResponse(doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id)));
    }

    public DoctorResponse create(DoctorRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        Specialization specialization = null;
        if (request.getSpecializationId() != null) {
            specialization = specializationRepository.findById(request.getSpecializationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialization", request.getSpecializationId()));
        }

        Doctor doctor = Doctor.builder()
                .user(user)
                .specialization(specialization)
                .licenseNumber(request.getLicenseNumber())
                .experienceYears(request.getExperienceYears())
                .bio(request.getBio())
                .build();

        // Generate embedding
        generateEmbedding(doctor);

        return toResponse(doctorRepository.save(doctor));
    }

    public DoctorResponse update(Long id, DoctorRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));

        if (request.getSpecializationId() != null) {
            Specialization specialization = specializationRepository.findById(request.getSpecializationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialization", request.getSpecializationId()));
            doctor.setSpecialization(specialization);
        }
        if (request.getLicenseNumber() != null)
            doctor.setLicenseNumber(request.getLicenseNumber());
        if (request.getExperienceYears() != null)
            doctor.setExperienceYears(request.getExperienceYears());
        if (request.getBio() != null)
            doctor.setBio(request.getBio());
        if (request.getUserId() != null) {
            doctor.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId())));

        }

        // Regenerate embedding
        generateEmbedding(doctor);

        return toResponse(doctorRepository.save(doctor));
    }

    public DoctorResponse updateStatus(Long id, String newStatus) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        
        User user = doctor.getUser();
        if (newStatus != null && !newStatus.trim().isEmpty()) {
            user.setStatus(newStatus.toUpperCase());
            userRepository.save(user);
        }
        
        return toResponse(doctor);
    }

    @org.springframework.transaction.annotation.Transactional
    public void delete(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));

        // 1. Kiểm tra lịch sử lâm sàng (Nếu có bệnh nhân liên quan thì KHÔNG cho xóa để
        // bảo toàn dữ liệu)
        boolean hasAppointments = !appointmentRepository.findByDoctorId(id).isEmpty();
        boolean hasRecords = !medicalRecordRepository.findByDoctorId(id).isEmpty();
        boolean hasPrescriptions = !prescriptionRepository.findByDoctorIdOrderByCreatedAtDesc(id).isEmpty();
        boolean hasOnlineConsultations = onlineConsultationRepository.existsByDoctorId(id);

        if (hasAppointments || hasRecords || hasPrescriptions || hasOnlineConsultations) {
            throw new IllegalArgumentException(
                    "Không thể xóa do có lịch hẹn, đơn thuốc, hoặc đơn tư vấn online liên quan");
        }

        // 2. Xóa các dữ liệu phụ (Lịch làm việc, Hình ảnh)
        doctorScheduleRepository.deleteAll(doctorScheduleRepository.findByDoctorId(id));

        if (doctor.getFeatureImage() != null) {
            fileStorageService.delete(doctor.getFeatureImage());
        }

        // 3. Thực hiện xóa
        doctorRepository.deleteById(id);
    }

    public DoctorResponse uploadFeatureImage(Long id, MultipartFile file) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        if (doctor.getFeatureImage() != null) {
            fileStorageService.delete(doctor.getFeatureImage());
        }
        String path = fileStorageService.store(file, IMAGE_SUBDIRECTORY);
        doctor.setFeatureImage(path);
        return toResponse(doctorRepository.save(doctor));
    }

    public DoctorResponse deleteFeatureImage(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        if (doctor.getFeatureImage() != null) {
            fileStorageService.delete(doctor.getFeatureImage());
            doctor.setFeatureImage(null);
            doctorRepository.save(doctor);
        }
        return toResponse(doctor);
    }

    private DoctorResponse toResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .userId(doctor.getUser().getId())
                .fullName(doctor.getUser().getFullName())
                .email(doctor.getUser().getEmail())
                .specializationName(doctor.getSpecialization() != null ? doctor.getSpecialization().getName() : null)
                .licenseNumber(doctor.getLicenseNumber())
                .experienceYears(doctor.getExperienceYears())
                .bio(doctor.getBio())
                .status(doctor.getUser().getStatus())
                .featureImageUrl(doctor.getFeatureImage() != null ? "/images/" + doctor.getFeatureImage() : null)
                .build();
    }

    private void generateEmbedding(Doctor doctor) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(doctor.getUser().getFullName()).append(" ");
            if (doctor.getUser().getGender() != null)
                sb.append(doctor.getUser().getGender()).append(" ");
            if (doctor.getUser().getAddress() != null)
                sb.append(doctor.getUser().getAddress()).append(" ");
            if (doctor.getSpecialization() != null)
                sb.append(doctor.getSpecialization().getName()).append(" ");
            if (doctor.getBio() != null)
                sb.append(doctor.getBio());
            List<Double> embedding = embeddingService.getEmbedding(sb.toString().trim());
            doctor.setEmbedding(embeddingService.embeddingToJson(embedding));
        } catch (Exception e) {
            // Don't fail the create/update if embedding fails
        }
    }
}
