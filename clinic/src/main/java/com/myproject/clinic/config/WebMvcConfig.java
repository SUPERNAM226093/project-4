package com.myproject.clinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình Web MVC (WebMvcConfig).
 * Cấu hình các Resource Handlers để ánh xạ các đường dẫn URL tĩnh (static URLs) tới thư mục vật lý lưu trữ tệp tin trên Server.
 * Giúp người dùng hoặc Frontend có thể truy cập trực tiếp hình ảnh thông qua trình duyệt (Ví dụ: http://localhost:8080/images/doctor.jpg).
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ URL dạng "/images/**" tới thư mục vật lý "./public/" ở thư mục gốc của Backend
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:./public/");
                
        // Ánh xạ URL dạng "/rooms/**" tới thư mục vật lý "./public/rooms/" lưu trữ ảnh phòng bệnh
        registry.addResourceHandler("/rooms/**")
                .addResourceLocations("file:./public/rooms/");
    }
}
