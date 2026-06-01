package com.myproject.clinic.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa các tham số đã được trích xuất và chuẩn hóa từ câu hỏi của người dùng.
 * Được dùng bởi DataExtractionService để làm input cho các Handler.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtractionResult {

    /**
     * Tên chuyên khoa người dùng nhắc đến (sau khi đã qua AliasNormalizationService).
     * null = người dùng không đề cập cụ thể -> tìm tất cả chuyên khoa.
     */
    private String specialization;

    /**
     * ID chuyên khoa sau khi resolve từ tên. null nếu không tìm thấy.
     */
    private Long specializationId;

    /**
     * Tên bác sĩ người dùng hỏi đích danh (vd: "bác sĩ Tuấn").
     * null nếu người dùng không đề cập tên cụ thể.
     */
    private String doctorName;

    /**
     * Ngày truy vấn đã được quy đổi sang định dạng ISO (yyyy-MM-dd).
     * Mặc định là ngày hôm nay nếu người dùng không nói rõ.
     */
    private String date;

    /**
     * Loại hình khám: "ONLINE", "OFFLINE", hoặc "ALL".
     * Mặc định là "ALL".
     */
    @Builder.Default
    private String type = "ALL";

    /**
     * Khung giờ: "MORNING" (sáng <12h), "AFTERNOON" (chiều >=12h), "ALL".
     * Mặc định là "ALL".
     */
    @Builder.Default
    private String timeRange = "ALL";

    /**
     * Danh sách intent phát hiện được trong câu hỏi (hỗ trợ multi-intent).
     * Ví dụ: ["STATISTICS", "SEARCH"]
     */
    private java.util.List<String> intents;
}
