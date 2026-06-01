package com.myproject.clinic.room.service;

import com.myproject.clinic.entity.Room;
import com.myproject.clinic.entity.RoomBooking;
import com.myproject.clinic.entity.User;
import com.myproject.clinic.repository.RoomBookingRepository;
import com.myproject.clinic.repository.RoomRepository;
import com.myproject.clinic.repository.UserRepository;
import com.myproject.clinic.room.dto.RoomBookingRequest;
import com.myproject.clinic.validation.BookingValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoomBookingServiceTest {

    @Mock
    private RoomBookingRepository bookingRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingValidationService bookingValidationService;

    @InjectMocks
    private RoomBookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBooking_Success() {
        RoomBookingRequest request = new RoomBookingRequest();
        request.setRoomId(1L);
        request.setCheckInDate(LocalDateTime.now().plusDays(1));
        request.setCheckOutDate(LocalDateTime.now().plusDays(2));
        request.setPatientName("John Doe");

        User user = new User();
        user.setId(1L);

        Room room = new Room();
        room.setId(1L);
        room.setPricePerNight(BigDecimal.valueOf(500000));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.findByBookedById(1L)).thenReturn(new ArrayList<>());
        when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(RoomBooking.class))).thenAnswer(i -> i.getArguments()[0]);

        com.myproject.clinic.room.dto.RoomBookingDTO result = bookingService.createBooking(1L, request);

        assertNotNull(result);
        assertEquals("John Doe", result.getPatientName());
        assertEquals(1, result.getTotalNights());
        assertEquals(0, BigDecimal.valueOf(500000).compareTo(result.getEstimatedFee()));
        verify(bookingValidationService).validateMaxActiveRoomBookings(1L);
        verify(bookingValidationService).validateRoomAvailability(1L, request.getCheckInDate(), request.getCheckOutDate());
    }

    @Test
    void testCreateBooking_Overlap() {
        RoomBookingRequest request = new RoomBookingRequest();
        request.setRoomId(1L);
        request.setCheckInDate(LocalDateTime.now().plusDays(1));
        request.setCheckOutDate(LocalDateTime.now().plusDays(2));

        User user = new User();
        user.setId(1L);
        Room room = new Room();
        room.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        
        doThrow(new RuntimeException("Room is not available"))
                .when(bookingValidationService).validateRoomAvailability(any(), any(), any());

        assertThrows(RuntimeException.class, () -> bookingService.createBooking(1L, request));
    }
}
