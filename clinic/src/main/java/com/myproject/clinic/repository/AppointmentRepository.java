package com.myproject.clinic.repository;

import com.myproject.clinic.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * </p>
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"patient", "doctor", "service", "schedule", "healthPackage"})
        List<Appointment> findByPatientId(Long patientId);

        /**
         * Lấy toàn bộ danh sách lịch hẹn của một Bác sĩ để theo dõi lịch trình khám
         * bệnh.
         * Lệnh SQL ngầm định: SELECT * FROM appointments WHERE doctor_id = ?
         */
        @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"patient", "doctor", "service", "schedule", "healthPackage"})
        List<Appointment> findByDoctorId(Long doctorId);

        /** Lấy toàn bộ danh sách lịch hẹn thuộc về một Khung giờ cụ thể */
        @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"patient", "doctor", "service", "schedule", "healthPackage"})
        List<Appointment> findByScheduleId(Long scheduleId);

        /** Lấy toàn bộ danh sách lịch hẹn thuộc về một Gói khám sức khỏe cụ thể */
        @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"patient", "doctor", "service", "schedule", "healthPackage"})
        List<Appointment> findByHealthPackageId(Long healthPackageId);

        /** Lấy danh sách lịch hẹn trong một ngày cụ thể */
        @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"patient", "doctor", "service", "schedule", "healthPackage"})
        List<Appointment> findByAppointmentDate(java.time.LocalDate date);

        // =========================================================================
        // 2. NHÓM HÀM KIỂM TRA TỒN TẠI (DÙNG ĐỂ VALIDATE CHỐNG TRÙNG LỊCH)
        // Bắt đầu bằng chữ "exists", sẽ trả về true/false thay vì trả về cả bản ghi
        // (Tối ưu tốc độ DB).
        // =========================================================================

        /**
         * Kiểm tra xem Bệnh nhân đã có lịch hẹn nào bị trùng ngày + giờ hay chưa.
         * "StatusNotIn": Loại trừ các trạng thái đã Xong/Hủy (vì nếu hủy rồi thì vẫn
         * được phép đặt lại vào giờ đó).
         */
        boolean existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
                        Long patientId, java.time.LocalDate date, java.time.LocalTime time,
                        java.util.Collection<String> statuses);

        /**
         * Kiểm tra xem Bác sĩ đã có lịch hẹn nào bị trùng ngày + giờ (đang chờ/xác
         * nhận) hay chưa.
         * Dùng để chặn 2 bệnh nhân đặt chung 1 bác sĩ vào cùng 1 giờ.
         */
        boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
                        Long doctorId, java.time.LocalDate date, java.time.LocalTime time,
                        java.util.Collection<String> statuses);

        /**
         * Kiểm tra xem Gói khám sức khỏe đã bị ai đặt vào ngày + giờ đó chưa (Dùng khi
         * TẠO MỚI lịch hẹn).
         */
        boolean existsByHealthPackageIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
                        Long healthPackageId, java.time.LocalDate date, java.time.LocalTime time,
                        java.util.Collection<String> statuses);

        /**
         * Tương tự hàm trên nhưng dùng khi CẬP NHẬT (Sửa lịch hẹn).
         * "AndIdNot": Phải loại trừ chính cái ID của lịch hẹn đang sửa ra (nếu không hệ
         * thống sẽ báo lỗi trùng với chính nó).
         */
        boolean existsByHealthPackageIdAndAppointmentDateAndAppointmentTimeAndStatusNotInAndIdNot(
                        Long healthPackageId, java.time.LocalDate date, java.time.LocalTime time,
                        java.util.Collection<String> statuses, Long id);

        // =========================================================================
        // 3. NHÓM HÀM THỐNG KÊ (DÙNG CHO ADMIN DASHBOARD / BIỂU ĐỒ)
        // Dùng để đếm hoặc lấy danh sách lịch hẹn trong một khoảng thời gian (Từ ngày A
        // đến ngày B)
        // =========================================================================

        /**
         * Đếm số lượng lịch hẹn theo trạng thái (có phân biệt hoa/thường) trong một
         * khoảng thời gian
         */
        long countByStatusAndCreatedAtBetween(String status, java.time.LocalDateTime start,
                        java.time.LocalDateTime end);

        /**
         * Đếm số lượng lịch hẹn theo trạng thái (KHÔNG phân biệt hoa/thường) trong một
         * khoảng thời gian
         */
        long countByStatusIgnoreCaseAndCreatedAtBetween(String status, java.time.LocalDateTime start,
                        java.time.LocalDateTime end);

        /** Lấy danh sách lịch hẹn theo trạng thái trong khoảng thời gian */
        @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"patient", "doctor", "service", "schedule", "healthPackage"})
        java.util.List<Appointment> findByStatusAndCreatedAtBetween(String status, java.time.LocalDateTime start,
                        java.time.LocalDateTime end);

        /**
         * Lấy danh sách lịch hẹn theo trạng thái (không phân biệt chữ hoa thường) trong
         * khoảng thời gian
         */
        @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"patient", "doctor", "service", "schedule", "healthPackage"})
        java.util.List<Appointment> findByStatusIgnoreCaseAndCreatedAtBetween(String status,
                        java.time.LocalDateTime start, java.time.LocalDateTime end);

        /**
         * Đếm TỔNG số lượng lịch hẹn trong một khoảng thời gian (không quan tâm trạng
         * thái)
         */
        long countByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

        /** Lấy danh sách tất cả lịch hẹn trong một khoảng thời gian */
        @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"patient", "doctor", "service", "schedule", "healthPackage"})
        java.util.List<Appointment> findByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

        /**
         * @Modifying:
         * @Transactional:
         */
        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.transaction.annotation.Transactional
        @org.springframework.data.jpa.repository.Query("UPDATE Appointment a SET a.createdAt = :now WHERE a.createdAt IS NULL")
        void fixNullDates(java.time.LocalDateTime now);

        /**
         * Chức năng: Kiểm tra xem Bác sĩ này CÓ CÒN BẤT KỲ LỊCH HẸN NÀO TRONG TƯƠNG LAI
         * đang chờ khám hay không.
         * Logic JPQL: Ngày hẹn lớn hơn hôm nay HOẶC (ngày bằng hôm nay VÀ giờ lớn hơn
         * hiện tại).
         * Ứng dụng: Dùng để chặn Admin không được XÓA bác sĩ nếu bác sĩ đó vẫn còn lịch
         * hẹn sắp tới.
         */
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

        /**
         * Tương tự hàm trên, nhưng dùng để chặn XÓA Gói khám sức khỏe nếu vẫn có người
         * đã đặt và chưa khám xong.
         */
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
}
