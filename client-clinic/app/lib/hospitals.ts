// app/lib/hospitals.ts
// Mock data — thay bằng fetch API khi backend /api/hospitals sẵn sàng

export interface Hospital {
    id: number;
    name: string;
    slug: string;
    shortDescription: string;
    description: string;
    address: string;
    hotline: string;
    workingHours: string;
    specialties: string[];
    image: string | null;       // URL ảnh logo
    banner: string | null;      // URL ảnh banner
    website: string | null;
    verified: boolean;
    color: string;              // tailwind gradient cho avatar fallback
}

export const HOSPITALS: Hospital[] = [
    {
        id: 1,
        name: "Phòng khám Med 1 - CS1 - Chùa Hương",
        slug: "med-1-cs1-chua-huong",
        shortDescription: "Cơ sở y tế đạt chuẩn tại khu vực Mỹ Đức, Hà Nội.",
        description:
            "Phòng khám Med 1 - Cơ sở 1 tọa lạc tại khu vực danh thắng Chùa Hương, mang đến dịch vụ chăm sóc sức khỏe chất lượng cao cho người dân và khách du lịch khu vực ngoại thành Hà Nội. Hệ thống trang thiết bị hiện đại, không gian khám chữa bệnh thân thiện.",
        address: "Chùa Hương, Mỹ Đức, Hà Nội",
        hotline: "1900 1111",
        workingHours: "Thứ 2 – Chủ Nhật: 7:00 – 20:00",
        specialties: ["Nội tổng quát", "Sản phụ khoa", "Tai mũi họng", "Cơ xương khớp"],
        image: null,
        banner: null,
        website: null,
        verified: true,
        color: "from-[#1a6b3c] to-[#2d9254]",
    },
    {
        id: 2,
        name: "Phòng khám Med 2 - CS2 - 226 Ngô Quyền",
        slug: "med-2-cs2-226-ngo-quyen",
        shortDescription: "Trung tâm y tế chất lượng cao tại Hà Đông.",
        description:
            "Phòng khám Med 2 đặt tại trung tâm Hà Đông, sở hữu cơ sở vật chất khang trang cùng đội ngũ y bác sĩ đầu ngành. Chúng tôi cam kết mang lại trải nghiệm y tế tận tâm và chuẩn xác nhất cho bệnh nhân.",
        address: "226 Ngô Quyền, Hà Đông, Hà Nội",
        hotline: "1900 2222",
        workingHours: "Thứ 2 – Chủ Nhật: 7:00 – 19:00",
        specialties: ["Nhi khoa", "Nội tiêu hóa", "Mắt", "Răng hàm mặt"],
        image: null,
        banner: null,
        website: null,
        verified: true,
        color: "from-[#4527a0] to-[#7e57c2]",
    },
    {
        id: 3,
        name: "Phòng khám Med 3 - CS3 - 82 Kim Mã",
        slug: "med-3-cs3-82-kim-ma",
        shortDescription: "Phòng khám đa khoa hiện đại ngay trung tâm quận Ba Đình.",
        description:
            "Nằm trên tuyến phố sầm uất Kim Mã, Med 3 là địa chỉ chăm sóc sức khỏe tin cậy của giới văn phòng và người dân nội thành. Hệ thống xét nghiệm tự động hóa 100% giúp rút ngắn tối đa thời gian chờ đợi.",
        address: "82 Kim Mã, Ba Đình, Hà Nội",
        hotline: "1900 3333",
        workingHours: "Thứ 2 – Thứ 7: 7:30 – 18:00",
        specialties: ["Tim mạch", "Thần kinh", "Da liễu", "Nội tiết niệu"],
        image: null,
        banner: null,
        website: null,
        verified: true,
        color: "from-[#e65100] to-[#ff9800]",
    },
    {
        id: 4,
        name: "Phòng khám Med 4 - CS4 - 10 Hoàng Quốc Việt",
        slug: "med-4-cs4-10-hoang-quoc-viet",
        shortDescription: "Đơn vị chăm sóc sức khỏe công nghệ cao tại Cầu Giấy.",
        description:
            "Phòng khám Med 4 mang đến môi trường y tế công nghệ cao, an toàn và thân thiện. Nơi đây tập trung nhiều thiết bị chẩn đoán hình ảnh tiên tiến nhất, phục vụ khu vực dân cư đông đúc của quận Cầu Giấy.",
        address: "10 Hoàng Quốc Việt, Cầu Giấy, Hà Nội",
        hotline: "1900 4444",
        workingHours: "Thứ 2 – Chủ Nhật: 7:00 – 19:00",
        specialties: ["Cấp cứu", "Hô hấp", "Truyền nhiễm", "Ngoại khoa"],
        image: null,
        banner: null,
        website: null,
        verified: true,
        color: "from-[#00695c] to-[#26a69a]",
    }
];

/** Tìm hospital theo slug — trả null nếu không tìm thấy */
export function getHospitalBySlug(slug: string): Hospital | null {
    return HOSPITALS.find((h) => h.slug === slug) ?? null;
}
