package com.myproject.clinic.repository;

import com.myproject.clinic.entity.HealthPackageSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Giao diện Repository cung cấp các phương thức truy xuất dữ liệu từ Database cho HealthPackageSchedule.
 */
@Repository
public interface HealthPackageScheduleRepository extends JpaRepository<HealthPackageSchedule, Long> {
    List<HealthPackageSchedule> findByHealthPackageId(Long healthPackageId);
}
