package com.myproject.clinic.repository;

import com.myproject.clinic.entity.ClinicService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Giao diện Repository cung cấp các phương thức truy xuất dữ liệu từ Database cho ClinicService.
 */
@Repository
public interface ClinicServiceRepository extends JpaRepository<ClinicService, Long> {
}
