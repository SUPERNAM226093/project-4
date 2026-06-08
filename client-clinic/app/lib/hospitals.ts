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
        name: "Phòng khám Đa khoa Nam Clinic - Cơ sở 1",
        slug: "nam-clinic-co-so-1",
        shortDescription: "Cơ sở chính tại trung tâm TP.HCM với trang thiết bị hiện đại.",
        description:
            "Phòng khám Đa khoa Nam Clinic - Cơ sở 1 là trụ sở chính của hệ thống, tọa lạc tại trung tâm TP.HCM. Được trang bị hệ thống máy móc tiên tiến và đội ngũ y bác sĩ đầu ngành, cơ sở 1 chuyên cung cấp các dịch vụ khám chữa bệnh nội và ngoại trú chất lượng cao cho bệnh nhân khu vực miền Nam.",
        address: "Quận 1, TP. Hồ Chí Minh",
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
        name: "Phòng khám Đa khoa Nam Clinic - Cơ sở 2",
        slug: "nam-clinic-co-so-2",
        shortDescription: "Trung tâm y tế chất lượng cao phục vụ khu vực Đồng bằng sông Cửu Long.",
        description:
            "Tọa lạc tại trung tâm TP. Cần Thơ, Cơ sở 2 mang đến dịch vụ y tế chuẩn quốc tế cho người dân miền Tây. Với không gian rộng rãi và dịch vụ chăm sóc tận tình, chúng tôi cam kết mang lại trải nghiệm khám chữa bệnh tốt nhất.",
        address: "Quận Ninh Kiều, TP. Cần Thơ",
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
        name: "Phòng khám Đa khoa Nam Clinic - Cơ sở 3",
        slug: "nam-clinic-co-so-3",
        shortDescription: "Cơ sở y tế uy tín đáp ứng nhu cầu chăm sóc sức khỏe tại miền Bắc.",
        description:
            "Phòng khám Đa khoa Nam Clinic - Cơ sở 3 tọa lạc tại Hà Nội, cung cấp các dịch vụ khám chữa bệnh toàn diện. Chúng tôi tự hào sở hữu đội ngũ chuyên gia giàu kinh nghiệm và hệ thống xét nghiệm tự động hóa 100%.",
        address: "Quận Đống Đa, Hà Nội",
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
        name: "Phòng khám Đa khoa Nam Clinic - Cơ sở 4",
        slug: "nam-clinic-co-so-4",
        shortDescription: "Đơn vị chăm sóc sức khỏe hàng đầu tại khu vực miền Trung.",
        description:
            "Nằm tại trung tâm TP. Đà Nẵng, Cơ sở 4 của Nam Clinic cung cấp môi trường y tế an toàn, thân thiện với dịch vụ chăm sóc khách hàng chuyên nghiệp. Nơi đây tập trung nhiều thiết bị chẩn đoán hình ảnh tiên tiến nhất.",
        address: "Quận Hải Châu, Đà Nẵng",
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
