"use client";
/**
 * FILE: Footer.tsx
 * MÔ TẢ: Thành phần chân trang hiển thị thông tin liên hệ, liên kết nhanh và bản quyền của hệ thống.
 */
import Image from "next/image";


const footerLinks = {
    "Dịch vụ y tế": [
        "Đặt khám tại cơ sở",
        "Đặt khám theo chuyên khoa",
        "Tư vấn khám từ xa (Video)",
        "Gói khám sức khỏe toàn diện",
        "Dịch vụ y tế doanh nghiệp",
    ],
    "Cơ sở y tế": [
        "Bệnh viện công",
        "Bệnh viện tư",
        "Phòng khám đa khoa",
        "Phòng mạch bác sĩ",
        "Trung tâm xét nghiệm",
        "Y tế tại nhà",
    ],
    "Hướng dẫn": [
        "Cài đặt ứng dụng",
        "Quy trình đặt lịch",
        "Chính sách hoàn tiền",
        "Câu hỏi thường gặp (FAQ)",
    ],
};



export default function Footer() {


    return (
        <footer className="bg-[var(--green-light)] text-[#0a3d2e] border-t border-white/20">
            {/* --- 1. PHẦN CHÍNH CỦA CHÂN TRANG (MAIN FOOTER) --- */}
            <div className="max-w-6xl mx-auto px-4 py-12">
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-8">
                    {/* CỘT 1: THƯƠNG HIỆU VÀ THÔNG TIN LIÊN HỆ CHÍNH */}
                    <div className="lg:col-span-1">
                        <div className="mb-4">
                            {/* Logo MedPro dạng nguyên bản (tông tối/xanh) nổi bật trên nền xanh da trời */}
                            <Image
                                src="/logo-medpro.png"
                                alt="MedPro"
                                width={120}
                                height={40}
                                className="h-10 w-auto object-contain"
                            />
                        </div>
                        {/* Đoạn mô tả ngắn về sứ mệnh của phòng khám */}
                        <p className="text-sm text-[#0a3d2e]/90 leading-relaxed mb-4">
                            {"Kết nối người dân với cơ sở và dịch vụ y tế hàng đầu Việt Nam"}
                        </p>

                        {/* Thông tin Hotline và Email hỗ trợ */}
                        <div className="mt-6 space-y-2">
                            <div className="flex items-center gap-2 text-sm">
                                <svg className="w-4 h-4 text-[#0a3d2e]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                                </svg>
                                <span className="text-[#0a3d2e] font-bold">1900 2115</span>
                            </div>
                            <div className="flex items-center gap-2 text-sm text-[#0a3d2e]/85">
                                <svg className="w-4 h-4 text-[#0a3d2e]/70" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                                </svg>
                                cskh@medpro.vn
                            </div>
                        </div>
                    </div>

                    {/* CÁC CỘT LIÊN KẾT NHANH (Dịch vụ, Cơ sở y tế, Hướng dẫn) */}
                    {Object.entries(footerLinks).map(([title, links]) => (
                        <div key={title}>
                            <h3 className="text-sm font-bold mb-4 text-[#0a3d2e] uppercase tracking-wider">{title}</h3>
                            <ul className="space-y-2.5">
                                {links.map((linkName) => (
                                    <li key={linkName}>
                                        <a
                                            href="#"
                                            className="text-sm text-[#0a3d2e]/80 hover:text-white transition-colors"
                                        >
                                            {linkName}
                                        </a>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    ))}
                </div>
            </div>

            {/* Dòng dưới cùng hiển thị bản quyền và chính sách */}
            <div className="border-t border-[#0a3d2e]/15">
                <div className="max-w-6xl mx-auto px-4 py-6 flex flex-col md:flex-row items-center justify-between gap-2">
                    <p className="text-[10px] text-[#0a3d2e]/70 uppercase tracking-widest font-medium">
                        {"© 2024 MedPro. Tất cả quyền được bảo lưu."}
                    </p>
                    <div className="flex gap-4 text-xs text-[#0a3d2e]/80">
                        <a href="#" className="hover:text-white transition-colors">
                            {"Chính sách bảo mật"}
                        </a>
                        <a href="#" className="hover:text-white transition-colors">
                            {"Điều khoản sử dụng"}
                        </a>
                        <a href="#" className="hover:text-white transition-colors">
                            {"Quy chế hoạt động"}
                        </a>
                    </div>
                </div>
            </div>
        </footer>
    );
}

