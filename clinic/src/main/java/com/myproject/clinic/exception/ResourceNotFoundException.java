package com.myproject.clinic.exception;

/**
 * Ngoại lệ tùy chỉnh (Custom Exception) dùng khi không tìm thấy tài nguyên yêu cầu trong Cơ sở dữ liệu.
 * Kế thừa từ RuntimeException để Spring Boot có thể tự động rollback Transaction khi ngoại lệ xảy ra.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Khởi tạo ngoại lệ với một thông báo lỗi (message) tùy ý.
     * @param message Thông báo lỗi cụ thể
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Khởi tạo ngoại lệ với tên tài nguyên và ID không tìm thấy để sinh ra thông báo lỗi chuẩn.
     * Ví dụ: "Doctor not found with id: 5"
     * @param resourceName Tên tài nguyên (ví dụ: Doctor, User, Appointment, ...)
     * @param id ID của tài nguyên không tồn tại
     */
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }
}
