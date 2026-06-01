package com.myproject.clinic.room.service;

import com.myproject.clinic.config.FileStorageService;
import com.myproject.clinic.entity.Room;
import com.myproject.clinic.repository.RoomRepository;
import com.myproject.clinic.room.dto.RoomDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoom_Success() {
        RoomDTO dto = new RoomDTO();
        dto.setRoomCode("P101");
        dto.setName("VIP Room");
        dto.setPricePerNight(BigDecimal.valueOf(1000000));

        when(roomRepository.existsByRoomCode("P101")).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenAnswer(i -> i.getArguments()[0]);

        RoomDTO result = roomService.createRoom(dto);

        assertNotNull(result);
        assertEquals("P101", result.getRoomCode());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testCreateRoom_AlreadyExists() {
        RoomDTO dto = new RoomDTO();
        dto.setRoomCode("P101");

        when(roomRepository.existsByRoomCode("P101")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> roomService.createRoom(dto));
    }

    @Test
    void testGetRoomById_NotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roomService.getRoomById(1L));
    }
}
