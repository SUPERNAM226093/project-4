package com.myproject.clinic.repository;

import com.myproject.clinic.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Giao diện Repository cung cấp các phương thức truy xuất dữ liệu từ Database cho Prescription.
 */
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByMedicalRecordId(Long medicalRecordId);
    java.util.List<Prescription> findByMedicalRecordAppointmentPatientIdOrderByCreatedAtDesc(Long patientId);
    java.util.List<Prescription> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);
    long countByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query("UPDATE Prescription p SET p.createdAt = :now WHERE p.createdAt IS NULL")
    void fixNullDates(java.time.LocalDateTime now);
}
