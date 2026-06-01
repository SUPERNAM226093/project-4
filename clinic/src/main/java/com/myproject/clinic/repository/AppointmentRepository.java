package com.myproject.clinic.repository;

import com.myproject.clinic.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Giao diện Repository cung cấp các phương thức truy xuất dữ liệu từ Database
 * cho Appointment.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
        List<Appointment> findByPatientId(Long patientId);

        List<Appointment> findByDoctorId(Long doctorId);

        List<Appointment> findByScheduleId(Long scheduleId);

        List<Appointment> findByHealthPackageId(Long healthPackageId);

        boolean existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
                        Long patientId, java.time.LocalDate date, java.time.LocalTime time,
                        java.util.Collection<String> statuses);

        boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
                        Long doctorId, java.time.LocalDate date, java.time.LocalTime time,
                        java.util.Collection<String> statuses);

        boolean existsByHealthPackageIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
                        Long healthPackageId, java.time.LocalDate date, java.time.LocalTime time,
                        java.util.Collection<String> statuses);

        boolean existsByHealthPackageIdAndAppointmentDateAndAppointmentTimeAndStatusNotInAndIdNot(
                        Long healthPackageId, java.time.LocalDate date, java.time.LocalTime time,
                        java.util.Collection<String> statuses, Long id);

        long countByStatusAndCreatedAtBetween(String status, java.time.LocalDateTime start,
                        java.time.LocalDateTime end);

        long countByStatusIgnoreCaseAndCreatedAtBetween(String status, java.time.LocalDateTime start,
                        java.time.LocalDateTime end);

        java.util.List<Appointment> findByStatusAndCreatedAtBetween(String status, java.time.LocalDateTime start,
                        java.time.LocalDateTime end);

        java.util.List<Appointment> findByStatusIgnoreCaseAndCreatedAtBetween(String status,
                        java.time.LocalDateTime start, java.time.LocalDateTime end);

        long countByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

        java.util.List<Appointment> findByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.transaction.annotation.Transactional
        @org.springframework.data.jpa.repository.Query("UPDATE Appointment a SET a.createdAt = :now WHERE a.createdAt IS NULL")
        void fixNullDates(java.time.LocalDateTime now);

        @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId "
                        +
                        "AND (a.appointmentDate > :today OR (a.appointmentDate = :today AND a.appointmentTime > :nowTime)) "
                        +
                        "AND a.status NOT IN :doneStatuses")
        boolean existsFutureActiveAppointmentByDoctorId(
                        @org.springframework.data.repository.query.Param("doctorId") Long doctorId,
                        @org.springframework.data.repository.query.Param("today") java.time.LocalDate today,
                        @org.springframework.data.repository.query.Param("nowTime") java.time.LocalTime nowTime,
                        @org.springframework.data.repository.query.Param("doneStatuses") java.util.Collection<String> doneStatuses);

        @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.healthPackage.id = :healthPackageId "
                        +
                        "AND (a.appointmentDate > :today OR (a.appointmentDate = :today AND a.appointmentTime > :nowTime)) "
                        +
                        "AND a.status NOT IN :doneStatuses")
        boolean existsFutureActiveAppointmentByHealthPackageId(
                        @org.springframework.data.repository.query.Param("healthPackageId") Long healthPackageId,
                        @org.springframework.data.repository.query.Param("today") java.time.LocalDate today,
                        @org.springframework.data.repository.query.Param("nowTime") java.time.LocalTime nowTime,
                        @org.springframework.data.repository.query.Param("doneStatuses") java.util.Collection<String> doneStatuses);

        List<Appointment> findByAppointmentDate(java.time.LocalDate date);
}
