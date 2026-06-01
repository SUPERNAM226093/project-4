package com.myproject.clinic.serviceregistration.service;

import com.myproject.clinic.entity.ClinicService;
import com.myproject.clinic.entity.ServiceRegistration;
import com.myproject.clinic.entity.User;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.ClinicServiceRepository;
import com.myproject.clinic.repository.ServiceRegistrationRepository;
import com.myproject.clinic.repository.UserRepository;
import com.myproject.clinic.serviceregistration.dto.ServiceRegistrationRequest;
import com.myproject.clinic.serviceregistration.dto.ServiceRegistrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// JPA -> Hibernate -> JDBC -> Mysql
@Service
@RequiredArgsConstructor
public class ServiceRegistrationService {

    private final ServiceRegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final ClinicServiceRepository clinicServiceRepository;

    /**
     * Lấy danh sách tất cả các bản ghi.
     */
    public List<ServiceRegistrationResponse> findAll() {
        return registrationRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Tìm kiếm và lấy thông tin chi tiết của bản ghi theo mã định danh ID.
     */
    public ServiceRegistrationResponse findById(Long id) {
        return toResponse(registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRegistration", id)));
    }

    /**
     * Tạo mới và lưu bản ghi vào hệ thống.
     */
    public ServiceRegistrationResponse create(ServiceRegistrationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));
        ClinicService service = clinicServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", request.getServiceId()));

        ServiceRegistration registration = ServiceRegistration.builder()
                .user(user)
                .service(service)
                .status(request.getStatus() != null ? request.getStatus() : "PENDING")
                .build();

        return toResponse(registrationRepository.save(registration));
    }

    /**
     * Cập nhật thông tin chi tiết cho bản ghi.
     */
    public ServiceRegistrationResponse update(Long id, ServiceRegistrationRequest request) {
        ServiceRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRegistration", id));

        if (request.getStatus() != null)
            registration.setStatus(request.getStatus());

        return toResponse(registrationRepository.save(registration));
    }

    /**
     * Xóa bản ghi khỏi hệ thống.
     */
    public void delete(Long id) {
        if (!registrationRepository.existsById(id)) {
            throw new ResourceNotFoundException("ServiceRegistration", id);
        }
        registrationRepository.deleteById(id);
    }

    /**
     * Chuyển đổi đối tượng thực thể (Entity) sang định dạng phản hồi (Response DTO).
     */
    private ServiceRegistrationResponse toResponse(ServiceRegistration r) {
        return ServiceRegistrationResponse.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .userName(r.getUser().getFullName())
                .serviceId(r.getService().getId())
                .serviceName(r.getService().getName())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
