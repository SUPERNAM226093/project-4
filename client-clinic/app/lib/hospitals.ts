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
        name: "Bệnh viện Lao và Bệnh phổi Cần Thơ",
        slug: "benh-vien-lao-va-benh-phoi-can-tho",
        shortDescription: "Trung tâm điều trị lao và bệnh phổi hàng đầu khu vực Đồng bằng sông Cửu Long.",
        description:
            "Bệnh viện Lao và Bệnh phổi Cần Thơ là cơ sở y tế chuyên khoa hạng I trực thuộc Sở Y tế TP. Cần Thơ, với hơn 60 năm hoạt động trong lĩnh vực phòng chống và điều trị các bệnh về đường hô hấp, đặc biệt là lao phổi. Bệnh viện được trang bị hệ thống máy móc hiện đại, đội ngũ bác sĩ chuyên khoa giàu kinh nghiệm, mỗi năm tiếp nhận điều trị hàng nghìn lượt bệnh nhân từ khắp vùng ĐBSCL.",
        address: "Số 1 Nguyễn Trãi, Phường Lê Bình, Quận Cái Răng, TP. Cần Thơ",
        hotline: "0292 3841 795",
        workingHours: "Thứ 2 – Thứ 6: 7:00 – 16:30 | Cấp cứu: 24/7",
        specialties: ["Lao phổi", "Hô hấp – Phế quản", "Nội soi phế quản", "Xét nghiệm vi sinh", "Chẩn đoán hình ảnh"],
        image: null,
        banner: null,
        website: null,
        verified: true,
        color: "from-[#1a6b3c] to-[#2d9254]",
    },
    {
        id: 2,
        name: "Bệnh viện Mắt – Răng Hàm Mặt Cần Thơ",
        slug: "benh-vien-mat-rang-ham-mat-can-tho",
        shortDescription: "Chuyên khoa mắt và răng hàm mặt uy tín tại TP. Cần Thơ.",
        description:
            "Bệnh viện Mắt – Răng Hàm Mặt Cần Thơ là bệnh viện chuyên khoa hạng I trực thuộc Sở Y tế TP. Cần Thơ. Đây là địa chỉ tin cậy trong khám, chẩn đoán và điều trị các bệnh lý mắt, thực hiện phẫu thuật khúc xạ, đục thủy tinh thể, cùng các dịch vụ nha khoa toàn diện từ nhổ răng, trám răng đến phẫu thuật hàm mặt. Bệnh viện phục vụ hàng chục nghìn lượt bệnh nhân mỗi năm.",
        address: "Đường Nguyễn Việt Hồng, Phường An Bình, Quận Ninh Kiều, TP. Cần Thơ",
        hotline: "0292 3820 049",
        workingHours: "Thứ 2 – Thứ 7: 7:00 – 17:00 | Cấp cứu mắt: 24/7",
        specialties: ["Nhãn khoa", "Phẫu thuật khúc xạ (LASIK)", "Đục thủy tinh thể", "Nha khoa tổng quát", "Phẫu thuật răng hàm mặt"],
        image: null,
        banner: null,
        website: null,
        verified: true,
        color: "from-[#4527a0] to-[#7e57c2]",
    },
    {
        id: 3,
        name: "Bệnh viện Đại học Y Dược TP.HCM",
        slug: "benh-vien-dai-hoc-y-duoc-tphcm",
        shortDescription: "Bệnh viện đại học hàng đầu Việt Nam với đội ngũ Giáo sư – Tiến sĩ chuyên sâu.",
        description:
            "Bệnh viện Đại học Y Dược TP.HCM (UMC) là bệnh viện đa khoa hạng I trực thuộc Trường Đại học Y Dược TP.HCM, là trung tâm khám chữa bệnh và nghiên cứu y học hàng đầu Việt Nam. Với đội ngũ hơn 3.000 cán bộ nhân viên, hơn 600 giường bệnh nội trú và hàng chục phòng khám chuyên khoa, mỗi ngày bệnh viện tiếp nhận hàng nghìn lượt bệnh nhân từ khắp cả nước. UMC được trang bị các thiết bị y tế hiện đại nhất, áp dụng kỹ thuật cao trong nhiều lĩnh vực như ghép tạng, phẫu thuật robot, can thiệp tim mạch.",
        address: "215 Hồng Bàng, Phường 11, Quận 5, TP. Hồ Chí Minh",
        hotline: "028 3855 4269",
        workingHours: "Thứ 2 – Thứ 6: 6:30 – 17:00 | Cấp cứu: 24/7",
        specialties: ["Tim mạch", "Ung bướu", "Thần kinh", "Ghép tạng", "Phẫu thuật robot", "Sản phụ khoa", "Nhi khoa"],
        image: null,
        banner: null,
        website: "https://umc.edu.vn",
        verified: true,
        color: "from-[#e65100] to-[#ff9800]",
    },
    {
        id: 4,
        name: "Bệnh viện Chợ Rẫy",
        slug: "benh-vien-cho-ray",
        shortDescription: "Bệnh viện đa khoa lớn nhất miền Nam, tuyến cuối trong hệ thống y tế Việt Nam.",
        description:
            "Bệnh viện Chợ Rẫy là bệnh viện đa khoa hạng đặc biệt trực thuộc Bộ Y tế, là bệnh viện lớn nhất khu vực phía Nam với hơn 125 năm lịch sử. Với quy mô hơn 2.000 giường bệnh, hàng chục chuyên khoa mũi nhọn, mỗi ngày bệnh viện tiếp nhận điều trị hơn 6.000 lượt bệnh nhân nội và ngoại trú. Bệnh viện là trung tâm cấp cứu – hồi sức tích cực, y học nhiệt đới, và đào tạo y khoa nổi tiếng trong khu vực.",
        address: "201B Nguyễn Chí Thanh, Phường 12, Quận 5, TP. Hồ Chí Minh",
        hotline: "028 3855 4137",
        workingHours: "24/7 (Cấp cứu và nội trú) | OPD: Thứ 2 – Thứ 6: 6:00 – 17:00",
        specialties: ["Cấp cứu – Hồi sức", "Y học nhiệt đới", "Tim mạch", "Thần kinh", "Chấn thương chỉnh hình", "Tiêu hóa", "Da liễu"],
        image: null,
        banner: null,
        website: "https://choray.vn",
        verified: true,
        color: "from-[#00695c] to-[#26a69a]",
    },
    {
        id: 5,
        name: "Bệnh viện Nhi Đồng 1",
        slug: "benh-vien-nhi-dong-1",
        shortDescription: "Bệnh viện nhi khoa lớn nhất Việt Nam — chuyên điều trị bệnh lý trẻ em.",
        description:
            "Bệnh viện Nhi Đồng 1 là bệnh viện chuyên khoa nhi hạng đặc biệt trực thuộc Sở Y tế TP.HCM, là bệnh viện nhi lớn nhất Việt Nam. Với hơn 1.800 giường bệnh và hơn 100 chuyên ngành, bệnh viện phục vụ hàng triệu lượt bệnh nhi mỗi năm đến từ khắp các tỉnh thành khu vực phía Nam. Đội ngũ y bác sĩ chuyên khoa nhi giàu kinh nghiệm, trang thiết bị hiện đại trong nhi khoa – sơ sinh – phẫu thuật nhi.",
        address: "341 Sư Vạn Hạnh, Phường 10, Quận 10, TP. Hồ Chí Minh",
        hotline: "028 3927 1119",
        workingHours: "24/7 (Cấp cứu) | OPD: Thứ 2 – Thứ 7: 6:30 – 16:30",
        specialties: ["Nhi tổng quát", "Nhi sơ sinh", "Nhi tim mạch", "Nhi thần kinh", "Nhi tiêu hóa", "Phẫu thuật nhi"],
        image: null,
        banner: null,
        website: "https://nhidong1.org.vn",
        verified: true,
        color: "from-[#0277bd] to-[#29b6f6]",
    },
    {
        id: 6,
        name: "Bệnh viện Đa khoa Cần Thơ",
        slug: "benh-vien-da-khoa-can-tho",
        shortDescription: "Bệnh viện công lớn nhất TP. Cần Thơ với đầy đủ chuyên khoa.",
        description:
            "Bệnh viện Đa khoa Cần Thơ là bệnh viện hạng I tuyến tỉnh, là cơ sở y tế lớn nhất tại TP. Cần Thơ. Với hơn 1.000 giường bệnh và đội ngũ gần 1.500 cán bộ y tế, bệnh viện cung cấp dịch vụ khám và điều trị toàn diện cho người dân khu vực ĐBSCL. Bệnh viện đang từng bước hiện đại hóa thiết bị và nâng cao chất lượng dịch vụ y tế.",
        address: "4 Châu Văn Liêm, Phường An Lạc, Quận Ninh Kiều, TP. Cần Thơ",
        hotline: "0292 3823 700",
        workingHours: "24/7 (Cấp cứu) | OPD: Thứ 2 – Thứ 6: 7:00 – 16:30",
        specialties: ["Nội tổng quát", "Ngoại tổng quát", "Sản phụ khoa", "Nhi khoa", "Tim mạch", "Da liễu"],
        image: null,
        banner: null,
        website: null,
        verified: true,
        color: "from-[#c62828] to-[#ef5350]",
    },
    {
        id: 7,
        name: "Bệnh viện Vinmec Cần Thơ",
        slug: "benh-vien-vinmec-can-tho",
        shortDescription: "Bệnh viện quốc tế tiêu chuẩn JCI tại Cần Thơ.",
        description:
            "Bệnh viện Quốc tế Vinmec Cần Thơ là một trong những bệnh viện tư nhân hiện đại nhất khu vực ĐBSCL, đạt chuẩn JCI quốc tế. Bệnh viện triển khai toàn diện các dịch vụ khám chữa bệnh từ đa khoa đến chuyên khoa sâu trong môi trường tiện nghi, sạch sẽ và thân thiện. Vinmec Cần Thơ áp dụng công nghệ y tế tiên tiến, quy trình khám bệnh không chờ đợi và bệnh án điện tử.",
        address: "Khu dân cư Hưng Phú 1, Đường số 7, Quận Cái Răng, TP. Cần Thơ",
        hotline: "1900 23 23 89",
        workingHours: "24/7",
        specialties: ["Đa khoa quốc tế", "Tim mạch", "Ung bướu", "Sản phụ khoa", "Nhi khoa", "Thẩm mỹ y tế"],
        image: null,
        banner: null,
        website: "https://vinmec.com",
        verified: true,
        color: "from-[#1565c0] to-[#42a5f5]",
    },
    {
        id: 8,
        name: "Bệnh viện Phụ sản TP.HCM",
        slug: "benh-vien-phu-san-tphcm",
        shortDescription: "Trung tâm sản phụ khoa hàng đầu cả nước.",
        description:
            "Bệnh viện Phụ Sản TP.HCM là bệnh viện chuyên khoa hạng I về sản phụ khoa và nhi sơ sinh thuộc Sở Y tế TP.HCM. Đây là địa chỉ tin cậy hàng đầu về chăm sóc sức khỏe bà mẹ và trẻ em, thai kỳ nguy cơ cao, thụ tinh ống nghiệm (IVF) và các bệnh lý phụ khoa. Mỗi năm bệnh viện đỡ đẻ hơn 35.000 ca và thực hiện hàng nghìn ca IVF.",
        address: "284 Cống Quỳnh, Phường Phạm Ngũ Lão, Quận 1, TP. Hồ Chí Minh",
        hotline: "028 3925 0164",
        workingHours: "24/7",
        specialties: ["Sản khoa", "Phụ khoa", "Thụ tinh nhân tạo (IVF)", "Nhi sơ sinh", "Siêu âm thai"],
        image: null,
        banner: null,
        website: "https://benhvienphusan.com.vn",
        verified: true,
        color: "from-[#2e7d32] to-[#66bb6a]",
    },
];

/** Tìm hospital theo slug — trả null nếu không tìm thấy */
export function getHospitalBySlug(slug: string): Hospital | null {
    return HOSPITALS.find((h) => h.slug === slug) ?? null;
}
