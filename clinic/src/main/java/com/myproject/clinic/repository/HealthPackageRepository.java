package com.myproject.clinic.repository;

import com.myproject.clinic.entity.HealthPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Giao diện Repository cung cấp các phương thức truy xuất dữ liệu từ Database cho HealthPackage.
 */
@Repository
public interface HealthPackageRepository extends JpaRepository<HealthPackage, Long> {
    java.util.List<HealthPackage> findByStatus(String status);
}
