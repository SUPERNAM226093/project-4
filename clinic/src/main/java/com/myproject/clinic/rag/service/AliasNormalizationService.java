package com.myproject.clinic.rag.service;

import com.myproject.clinic.entity.Specialization;
import com.myproject.clinic.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Chuẩn hóa tên chuyên khoa từ cách người dùng gọi dân dã
 * về đúng tên chuẩn trong Database.
 * Ví dụ: "khoa tim", "bệnh tim", "tim mạch" -> "Tim mạch" (ID=1)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AliasNormalizationService {

    private final SpecializationRepository specializationRepository;

    /**
     * Map các từ khóa viết tắt / dân dã sang tên chuẩn trong DB.
     * Nên mở rộng danh sách này khi phòng khám phát sinh thêm chuyên khoa.
     */
    private static final Map<String, String> ALIAS_MAP = Map.ofEntries(
            // Tim mạch
            Map.entry("tim", "Tim mạch"),
            Map.entry("tim mạch", "Tim mạch"),
            Map.entry("bệnh tim", "Tim mạch"),
            Map.entry("khoa tim", "Tim mạch"),
            Map.entry("cardiology", "Tim mạch"),
            Map.entry("cardiovascular", "Tim mạch"),
            // Nhi khoa
            Map.entry("nhi", "Nhi khoa"),
            Map.entry("trẻ em", "Nhi khoa"),
            Map.entry("khoa nhi", "Nhi khoa"),
            Map.entry("pediatric", "Nhi khoa"),
            Map.entry("kids", "Nhi khoa"),
            // Nội khoa
            Map.entry("nội", "Nội khoa"),
            Map.entry("khoa nội", "Nội khoa"),
            Map.entry("internal medicine", "Nội khoa"),
            // Da liễu
            Map.entry("da", "Da liễu"),
            Map.entry("da liễu", "Da liễu"),
            Map.entry("ngoài da", "Da liễu"),
            Map.entry("dermatology", "Da liễu"),
            // Thần kinh
            Map.entry("thần kinh", "Thần kinh"),
            Map.entry("khoa thần kinh", "Thần kinh"),
            Map.entry("đầu", "Thần kinh"),
            Map.entry("neurology", "Thần kinh"),
            // Mắt
            Map.entry("mắt", "Nhãn khoa"),
            Map.entry("nhãn khoa", "Nhãn khoa"),
            Map.entry("ophthalmology", "Nhãn khoa"),
            // Tai mũi họng
            Map.entry("tai mũi họng", "Tai Mũi Họng"),
            Map.entry("tmh", "Tai Mũi Họng"),
            Map.entry("tai", "Tai Mũi Họng"),
            Map.entry("họng", "Tai Mũi Họng"),
            // Xương khớp
            Map.entry("xương khớp", "Cơ xương khớp"),
            Map.entry("xương", "Cơ xương khớp"),
            Map.entry("khớp", "Cơ xương khớp"),
            Map.entry("orthopedic", "Cơ xương khớp"),
            // Sản phụ khoa
            Map.entry("sản", "Sản phụ khoa"),
            Map.entry("phụ khoa", "Sản phụ khoa"),
            Map.entry("obstetrics", "Sản phụ khoa"),
            // Tiêu hóa - Gan mật
            Map.entry("gan", "Tiêu hóa - Gan mật"),
            Map.entry("gan mật", "Tiêu hóa - Gan mật"),
            Map.entry("tiêu hóa", "Tiêu hóa - Gan mật"),
            Map.entry("dạ dày", "Tiêu hóa - Gan mật"),
            Map.entry("gastroenterology", "Tiêu hóa - Gan mật"),
            // Hô hấp
            Map.entry("phổi", "Hô hấp"),
            Map.entry("hô hấp", "Hô hấp"),
            Map.entry("pulmonology", "Hô hấp"),
            // Răng hàm mặt
            Map.entry("răng", "Răng hàm mặt"),
            Map.entry("răng hàm mặt", "Răng hàm mặt"),
            Map.entry("nha khoa", "Răng hàm mặt"),
            Map.entry("dentist", "Răng hàm mặt")
    );

    /**
     * Tìm ID chuyên khoa từ tên dân dã mà người dùng nhập.
     * Ưu tiên: Alias Map -> Tìm kiếm gần đúng trong DB (Contains).
     *
     * @param rawName tên chuyên khoa người dùng nhập (vd: "khoa tim")
     * @return ID chuyên khoa nếu tìm thấy, null nếu không khớp
     */
    public Long resolveSpecializationId(String rawName) {
        if (rawName == null || rawName.isBlank()) return null;

        String normalized = rawName.toLowerCase().trim();

        // Bước 1: Tìm trong Alias Map
        String canonicalName = ALIAS_MAP.get(normalized);
        if (canonicalName != null) {
            Optional<Specialization> found = findByName(canonicalName);
            if (found.isPresent()) {
                log.debug("[AliasNorm] '{}' -> '{}' (ID={})", rawName, canonicalName, found.get().getId());
                return found.get().getId();
            }
        }

        // Bước 2: Tìm kiếm gần đúng trực tiếp trong DB (contains, case-insensitive)
        List<Specialization> allSpecs = specializationRepository.findAll();
        for (Specialization spec : allSpecs) {
            if (spec.getName().toLowerCase().contains(normalized)
                    || normalized.contains(spec.getName().toLowerCase())) {
                log.debug("[AliasNorm] Fuzzy match '{}' -> '{}' (ID={})", rawName, spec.getName(), spec.getId());
                return spec.getId();
            }
        }

        log.warn("[AliasNorm] Không tìm thấy chuyên khoa cho: '{}'", rawName);
        return null;
    }

    /**
     * Tìm kiếm một chuyên khoa theo tên chính xác (không phân biệt hoa thường).
     * Hàm này duyệt qua tất cả chuyên khoa trong DB để tìm ra kết quả khớp đầu tiên.
     * 
     * @param name Tên chuyên khoa cần tìm (ví dụ: "Tim mạch")
     * @return Optional chứa đối tượng Specialization nếu tìm thấy, hoặc empty nếu không có
     */
    private Optional<Specialization> findByName(String name) {
        return specializationRepository.findAll().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
