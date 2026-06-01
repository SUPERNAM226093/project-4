package com.myproject.clinic.repository;

import com.myproject.clinic.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Giao diện Repository cung cấp các phương thức truy xuất dữ liệu từ Database cho MedicalRecord.
 */
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);

    List<MedicalRecord> findByAppointmentPatientId(Long patientId);

    List<MedicalRecord> findByDoctorId(Long doctorId);
}
