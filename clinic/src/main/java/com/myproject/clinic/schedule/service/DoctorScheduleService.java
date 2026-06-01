package com.myproject.clinic.schedule.service;

import com.myproject.clinic.entity.Appointment;
import com.myproject.clinic.entity.Doctor;
import com.myproject.clinic.entity.DoctorSchedule;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.repository.DoctorRepository;
import com.myproject.clinic.repository.DoctorScheduleRepository;
import com.myproject.clinic.schedule.dto.DoctorScheduleRequest;
import com.myproject.clinic.schedule.dto.DoctorScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final com.myproject.clinic.repository.AppointmentRepository appointmentRepository;
    private final com.myproject.clinic.repository.OnlineConsultationRepository onlineConsultationRepository;

    public List<DoctorScheduleResponse> findAll() {
        return scheduleRepository.findAll().stream().map(this::toResponse).toList();
    }

    public DoctorScheduleResponse findById(Long id) {
        return toResponse(scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorSchedule", id)));
    }

    public DoctorScheduleResponse create(DoctorScheduleRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", request.getDoctorId()));

        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .workDate(request.getWorkDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return toResponse(scheduleRepository.save(schedule));
    }

    public DoctorScheduleResponse update(Long id, DoctorScheduleRequest request) {
        DoctorSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorSchedule", id));

        if (request.getWorkDate() != null)
            schedule.setWorkDate(request.getWorkDate());
        if (request.getStartTime() != null)
            schedule.setStartTime(request.getStartTime());
        if (request.getEndTime() != null)
            schedule.setEndTime(request.getEndTime());

        return toResponse(scheduleRepository.save(schedule));
    }

    @org.springframework.transaction.annotation.Transactional
    public void delete(Long id) {
        DoctorSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorSchedule", id));
        
        // Gỡ bỏ liên kết trong các cuộc hẹn trước khi xóa lịch
        List<Appointment> appointments = appointmentRepository.findByScheduleId(id);
        if (!appointments.isEmpty()) {
            appointments.forEach(a -> a.setSchedule(null));
            appointmentRepository.saveAll(appointments);
        }
        
        scheduleRepository.delete(schedule);
    }

    public List<DoctorScheduleResponse> findByDoctorId(Long doctorId) {
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorId(doctorId);
        return schedules.stream().map(s -> toResponse(s, isAvailable(s))).toList();
    }

    public List<DoctorScheduleResponse> findByDoctorIdAndDate(Long doctorId, java.time.LocalDate date) {
        List<String> activeStatuses = List.of("PENDING", "CONFIRMED", "EXAMINING", "COMPLETED");
        List<String> activeOnlineStatuses = List.of("PENDING", "PAID", "CONFIRMED", "COMPLETED");
        
        // 2. Lấy tất cả các khung giờ đã có người đặt thành công (Offline)
        List<java.time.LocalTime> bookedTimes = appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .filter(a -> a.getAppointmentDate().equals(date) && activeStatuses.contains(a.getStatus()))
                .map(Appointment::getAppointmentTime)
                .toList();

        // 3. Lấy tất cả các khung giờ đã có người đặt thành công (Online)
        List<String> bookedOnlineTimes = onlineConsultationRepository.findByDoctorId(doctorId)
                .stream()
                .filter(c -> c.getConsultationDate() != null && c.getConsultationDate().equals(date) && activeOnlineStatuses.contains(c.getPaymentStatus()))
                .map(com.myproject.clinic.entity.OnlineConsultation::getConsultationTime)
                .toList();

        java.util.List<DoctorScheduleResponse> slots = new java.util.ArrayList<>();
        java.time.LocalTime currentTime = java.time.LocalTime.of(8, 0);
        java.time.LocalTime dayEndTime = java.time.LocalTime.of(17, 0);

        while (currentTime.isBefore(dayEndTime)) {
            java.time.LocalTime slotEndTime = currentTime.plusHours(1);
            
            // Không còn kiểm tra isInShift (Admin không cần quản lý lịch làm việc nữa)
            // Bác sĩ mặc định làm việc từ 8h - 17h
            boolean isInShift = true; 

            // Kiểm tra xem đã có người đặt chưa (Offline hoặc Online)
            String timeStr = currentTime.toString().substring(0, 5);
            boolean isBookedOffline = bookedTimes.contains(currentTime);
            boolean isBookedOnline = bookedOnlineTimes.contains(timeStr);
            boolean isBooked = isBookedOffline || isBookedOnline;
            
            slots.add(DoctorScheduleResponse.builder()
                    .id(null)
                    .doctorId(doctorId)
                    .workDate(date)
                    .startTime(currentTime)
                    .endTime(slotEndTime)
                    .available(isInShift && !isBooked)
                    .build());
            
            currentTime = slotEndTime;
        }

        return slots;
    }

    private boolean isAvailable(DoctorSchedule s) {
        return !appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNotIn(
                s.getDoctor().getId(), s.getWorkDate(), s.getStartTime(), List.of("CANCELLED", "REJECTED"));
    }

    private DoctorScheduleResponse toResponse(DoctorSchedule s) {
        return toResponse(s, isAvailable(s));
    }

    private DoctorScheduleResponse toResponse(DoctorSchedule s, boolean available) {
        return DoctorScheduleResponse.builder()
                .id(s.getId())
                .doctorId(s.getDoctor().getId())
                .doctorName(s.getDoctor().getUser().getFullName())
                .workDate(s.getWorkDate())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .available(available)
                .build();
    }
}
