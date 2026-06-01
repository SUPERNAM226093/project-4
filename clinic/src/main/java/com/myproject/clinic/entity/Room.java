package com.myproject.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Thực thể quản lý thông tin các Phòng lưu bệnh/phòng điều trị nội trú trong cơ sở y tế.
 */
public class Room {

    /** Mã định danh duy nhất (Primary Key), tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mã ký hiệu của phòng (ví dụ: VIP-01, P102). */
    @Column(nullable = false, unique = true)
    private String roomCode; // P101, VIP-02

    /** Tên gọi hiển thị. */
    @Column(nullable = false)
    private String name; // Standard Room, VIP Room

    /** Tầng của phòng. */
    private String floor;

    /** Loại giường (Single, Double, King...). */
    @Column(nullable = false)
    private String bedType; // Single, Double, King

    /** Sức chứa tối đa (số người tối đa ở trong phòng). */
    @Column(nullable = false)
    private Integer maxCapacity;

    /** Giá thuê phòng cho mỗi đêm. */
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal pricePerNight;

    /** Phí vệ sinh phòng. */
    @Column(precision = 19, scale = 2)
    private BigDecimal cleaningFee;

    /** Phí dịch vụ phòng. */
    @Column(precision = 19, scale = 2)
    private BigDecimal serviceFee;

    /** Tổng số giường có trong phòng. */
    private Integer totalBeds;
    /** Số giường còn trống khả dụng. */
    private Integer availableBeds;

    /** Mô tả chi tiết. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Trạng thái hiện tại của thực thể. */
    private String status; // AVAILABLE, UNAVAILABLE, MAINTENANCE

    /** Danh sách các tiện nghi trong phòng (Wifi, Tủ lạnh, Điều hòa...). */
    @ElementCollection
    @CollectionTable(name = "room_amenities", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "amenity")
    private List<String> amenities;

    /** Danh sách link ảnh của phòng. */
    @ElementCollection
    @CollectionTable(name = "room_images", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "image_url")
    private List<String> images;

    /** Trạng thái kích hoạt (hoạt động/ngưng hoạt động). */
    @Builder.Default
    private Boolean isActive = true;
}
