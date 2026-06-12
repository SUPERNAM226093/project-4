package com.myproject.clinic.healthpackage.service;

import com.myproject.clinic.entity.HealthPackage;
import com.myproject.clinic.entity.HealthPackageBooking;
import com.myproject.clinic.entity.User;
import com.myproject.clinic.exception.ResourceNotFoundException;
import com.myproject.clinic.healthpackage.dto.HealthPackageBookingRequest;
import com.myproject.clinic.healthpackage.dto.HealthPackageBookingResponse;
import com.myproject.clinic.repository.HealthPackageBookingRepository;
import com.myproject.clinic.repository.HealthPackageRepository;
import com.myproject.clinic.repository.UserRepository;
import com.myproject.clinic.validation.BookingValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthPackageBookingService {

    private final HealthPackageBookingRepository bookingRepository;
    private final HealthPackageRepository healthPackageRepository;
    private final UserRepository userRepository;
    private final BookingValidationService bookingValidationService;
    private final com.myproject.clinic.repository.AppointmentRepository appointmentRepository;

    public HealthPackageBookingResponse create(HealthPackageBookingRequest request) {
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getPatientId()));

        HealthPackage healthPackage = healthPackageRepository.findById(request.getHealthPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("HealthPackage", request.getHealthPackageId()));

        // Chống spam: tối đa 3 gói khám đang chờ duyệt
        bookingValidationService.validateMaxActiveHealthPackageBookings(patient.getId());

        bookingValidationService.validatePatientAvailability(
                patient.getId(), 
                request.getBookingDate(), 
                request.getBookingTime()
        );

        bookingValidationService.validateHealthPackageAvailability(
                healthPackage.getId(),
                request.getBookingDate(),
                request.getBookingTime(),
                null, // No ID for new booking
                false // This is not an appointment flow
        );

        HealthPackageBooking booking = HealthPackageBooking.builder()
                .patient(patient)
                .healthPackage(healthPackage)
                .bookingDate(request.getBookingDate())
                .bookingTime(request.getBookingTime())
                .status("PENDING")
                .note(request.getNote())
                .build();

        return toResponse(bookingRepository.save(booking));
    }

    public List<HealthPackageBookingResponse> findByPatientId(Long patientId) {
        return bookingRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream().map(this::toResponse).toList();
    }

    public List<HealthPackageBookingResponse> findAll() {
        return bookingRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    public List<String> getBookedSlots(Long packageId, java.time.LocalDate date) {
        List<String> ignoredStatuses = List.of("CANCELLED", "REJECTED", "COMPLETED");

        java.util.List<String> bookedInAppointments = appointmentRepository.findByHealthPackageId(packageId).stream()
                .filter(a -> a.getAppointmentDate().equals(date) && !ignoredStatuses.contains(a.getStatus()))
                .map(a -> a.getAppointmentTime().toString().substring(0, 5))
                .toList();

        java.util.List<String> bookedInPackages = bookingRepository.findByHealthPackageIdOrderByBookingDateAscBookingTimeAsc(packageId).stream()
                .filter(b -> b.getBookingDate().equals(date) && !ignoredStatuses.contains(b.getStatus()))
                .map(b -> b.getBookingTime().toString().substring(0, 5))
                .toList();

        java.util.Set<String> allBooked = new java.util.HashSet<>();
        allBooked.addAll(bookedInAppointments);
        allBooked.addAll(bookedInPackages);
        return new java.util.ArrayList<>(allBooked);
    }

    public HealthPackageBookingResponse updateStatus(Long id, String status) {
        HealthPackageBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HealthPackageBooking", id));
        booking.setStatus(status);
        return toResponse(bookingRepository.save(booking));
    }

    public HealthPackageBookingResponse update(Long id, HealthPackageBookingRequest request) {
        HealthPackageBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HealthPackageBooking", id));

        // Patient/package/date/time are fixed after the patient creates the booking.
        // Admin update is limited to status and note only.
        booking.setStatus(request.getStatus());
        booking.setNote(request.getNote());

        return toResponse(bookingRepository.save(booking));
    }

    public void delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("HealthPackageBooking", id);
        }
        bookingRepository.deleteById(id);
    }

    public void cancel(Long id, Long patientId, String reason) {
        HealthPackageBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HealthPackageBooking", id));

        if (!booking.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        if ("CANCELLED".equals(booking.getStatus()) || "COMPLETED".equals(booking.getStatus())) {
            throw new RuntimeException("Cannot cancel booking with status: " + booking.getStatus());
        }

        booking.setStatus("CANCELLED");
        String currentNote = booking.getNote() != null ? booking.getNote() : "";
        booking.setNote(currentNote + (currentNote.isEmpty() ? "" : " | ") + "Lý do hủy: " + reason);
        bookingRepository.save(booking);
    }

    private HealthPackageBookingResponse toResponse(HealthPackageBooking b) {
        return HealthPackageBookingResponse.builder()
                .id(b.getId())
                .patientId(b.getPatient().getId())
                .patientName(b.getPatient().getFullName())
                .healthPackageId(b.getHealthPackage().getId())
                .healthPackageName(b.getHealthPackage().getName())
                .packagePrice(b.getHealthPackage().getPrice())
                .bookingDate(b.getBookingDate())
                .bookingTime(b.getBookingTime())
                .status(b.getStatus())
                .note(b.getNote())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
