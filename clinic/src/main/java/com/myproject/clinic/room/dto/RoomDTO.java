package com.myproject.clinic.room.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Lớp class RoomDTO trong hệ thống.
 */
@Data
public class RoomDTO {
    private Long id;
    private String roomCode;
    private String name;
    private String floor;
    private String bedType;
    private Integer maxCapacity;
    private BigDecimal pricePerNight;
    private BigDecimal cleaningFee;
    private BigDecimal serviceFee;
    private Integer totalBeds;
    private Integer availableBeds;
    private String description;
    private String status;
    private List<String> amenities;
    private List<String> images;
    private Boolean isActive;
    private Boolean isAvailable;
}
