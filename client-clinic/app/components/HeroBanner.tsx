"use client";
/**
 * FILE: HeroBanner.tsx
 * MÔ TẢ: Thành phần Banner chính của trang chủ, bao gồm tiêu đề chào mừng và thanh tìm kiếm bác sĩ thông minh.
 */
import { useState, useEffect, useRef } from "react";
import Image from "next/image";

import { useRouter } from "next/navigation";
import { fetchDoctors, DoctorResponse, getImageUrl } from "../lib/api";

export default function HeroBanner() {
    const router = useRouter();
    const [searchQuery, setSearchQuery] = useState("");
    const [doctors, setDoctors] = useState<DoctorResponse[]>([]);
    const [suggestions, setSuggestions] = useState<DoctorResponse[]>([]);
    const [showSuggestions, setShowSuggestions] = useState(false);
    const searchRef = useRef<HTMLDivElement>(null);

    /**
     * HÀM 1: useEffect này chạy 1 lần khi load trang để lấy danh sách bác sĩ từ Backend.
     * Mục đích: Chuẩn bị dữ liệu cho tính năng tìm kiếm nhanh.
     */
    useEffect(() => {
        const loadDoctors = async () => {
            try {
                const data = await fetchDoctors();
                setDoctors(data);
            } catch (error) {
                console.error("Failed to load doctors:", error);
            }
        };
        loadDoctors();
    }, []);

    /**
     * HÀM 2: Logic xử lý gợi ý khi người dùng nhập vào ô tìm kiếm.
     * Tự động lọc danh sách bác sĩ theo Tên hoặc Chuyên khoa.
     */
    useEffect(() => {
        if (searchQuery.trim().length >= 1) {
            const filtered = doctors.filter(d =>
                d.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
                d.specializationName.toLowerCase().includes(searchQuery.toLowerCase())
            );
            setSuggestions(filtered.slice(0, 5)); // Chỉ lấy 5 kết quả đầu tiên để hiển thị gợi ý
            setShowSuggestions(true);
        } else {
            setSuggestions([]);
            setShowSuggestions(false);
        }
    }, [searchQuery, doctors]);

    /**
     * HÀM 3: Xử lý đóng bảng gợi ý khi người dùng Click chuột ra ngoài khu vực tìm kiếm.
     */
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (searchRef.current && !searchRef.current.contains(event.target as Node)) {
                setShowSuggestions(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    /**
     * HÀM 4: Xử lý khi người dùng chọn một bác sĩ từ bảng gợi ý.
     * Sẽ chuyển hướng (navigate) sang trang hồ sơ chi tiết của bác sĩ đó.
     */
    const handleSelectDoctor = (id: number) => {
        router.push(`/doctor/${id}`);
        setShowSuggestions(false);
        setSearchQuery("");
    };

    // Lấy danh sách các lợi ích (Benefits) từ file ngôn ngữ i18n
    const benefits = ["Đặt khám nhanh - Lấy số thứ tự trực tuyến - Tư vấn sức khỏe từ xa", "Đặt khám theo giờ - Đặt càng sớm để có thể có số thứ tự thấp nhất", "Được hoàn tiền khi huỷ khám - Có cơ hội nhận ưu đãi hoàn tiền"];

    return (
        <section className="relative pt-32 pb-16 overflow-hidden">
            {/* --- PHẦN 1: NỀN VÀ CÁC HỌA TIẾT TRANG TRÍ --- */}
            <div className="absolute inset-0 bg-[#CBE7FC]" />
            <div className="absolute inset-0 opacity-15" style={{ backgroundImage: 'radial-gradient(rgba(255,255,255,0.4) 1.5px, transparent 1.5px)', backgroundSize: '40px 40px' }} />

            {/* Các khối hiệu ứng mờ tạo chiều sâu */}
            <div className="absolute top-20 left-10 w-32 h-32 bg-white/20 rounded-full blur-3xl" />
            <div className="absolute top-40 right-20 w-48 h-48 bg-white/20 rounded-full blur-3xl" />

            {/* Các họa tiết hình dấu cộng (+) phong cách y tế hiện đại */}
            <div className="absolute top-24 right-[10%] text-white/20 text-6xl font-thin select-none">+</div>
            <div className="absolute top-48 left-[5%] text-white/20 text-4xl font-thin select-none">+</div>
            <div className="absolute bottom-32 right-[20%] text-white/20 text-5xl font-thin select-none">+</div>

            <div className="relative max-w-7xl mx-auto px-4">
                <div className="flex flex-col lg:flex-row items-center gap-8">
                    {/* Hình ảnh bác sĩ - Màn hình lớn */}
                    <div className="hidden lg:block flex-shrink-0 w-64">
                        <Image src="/doctor-female-v2.png" alt="Bác sĩ" width={256} height={400} className="w-full h-auto object-contain drop-shadow-2xl brightness-105" />
                    </div>

                    {/* --- PHẦN 2: NỘI DUNG CHÍNH (TIÊU ĐỀ & Ô TÌM KIẾM) --- */}
                    <div className="flex-1 text-center max-w-2xl mx-auto">
                        <h1 className="text-3xl md:text-4xl lg:text-[44px] font-extrabold text-[#102A56] leading-tight mb-8 drop-shadow-sm">
                            {"Kết nối Người Dân với"} {"Cơ sở & Dịch vụ Y tế hàng đầu"}
                        </h1>

                        {/* Thanh tìm kiếm cao cấp */}
                        <div className="relative max-w-xl mx-auto mb-8" ref={searchRef}>
                            <div className="relative flex items-center bg-white rounded-full shadow-[0_10px_40px_-10px_rgba(37,99,235,0.15)] border border-[#D6EAFE] p-1.5 transition-all hover:shadow-[0_15px_50px_-10px_rgba(37,99,235,0.25)]">
                                <div className="flex items-center flex-1 px-4">
                                    <svg className="w-5 h-5 text-[#2563EB]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                                    </svg>
                                    <input
                                        type="text"
                                        placeholder={"Tìm kiếm bác sĩ"}
                                        value={searchQuery}
                                        onChange={(e) => setSearchQuery(e.target.value)}
                                        onFocus={() => searchQuery && setShowSuggestions(true)}
                                        className="w-full px-4 py-3.5 text-[15px] text-[#102A56] placeholder-[#5F789A] bg-transparent outline-none font-medium"
                                    />
                                </div>
                                <button className="px-8 py-3.5 rounded-full text-sm font-bold text-white bg-gradient-to-r from-[#2563EB] to-[#1E40AF] shadow-lg shadow-blue-200 hover:scale-[1.02] active:scale-95 transition-all">
                                    Tìm kiếm
                                </button>
                            </div>

                            {/* DROPDOWN GỢI Ý */}
                            {showSuggestions && suggestions.length > 0 && (
                                <div className="absolute top-full left-0 right-0 mt-3 bg-white rounded-2xl shadow-2xl border border-[#D6EAFE] overflow-hidden z-[60] animate-in fade-in slide-in-from-top-2 duration-200">
                                    <div className="py-2">
                                        <p className="px-5 py-3 text-[10px] font-bold text-[#5F789A] uppercase tracking-[0.2em] border-b border-[#F2FAFF]">
                                            Bác sĩ tìm thấy ({suggestions.length})
                                        </p>
                                        {suggestions.map((doctor) => (
                                            <button
                                                key={doctor.id}
                                                onClick={() => handleSelectDoctor(doctor.id)}
                                                className="w-full px-5 py-4 flex items-center gap-4 hover:bg-[#F8FCFF] transition-all text-left group"
                                            >
                                                <div className="w-12 h-12 rounded-full overflow-hidden bg-[#F2FAFF] border border-[#D6EAFE] flex-shrink-0 shadow-sm">
                                                    {doctor.featureImageUrl ? (
                                                        <img src={getImageUrl(doctor.featureImageUrl)} alt={doctor.fullName} className="w-full h-full object-cover" />
                                                    ) : (
                                                        <div className="w-full h-full flex items-center justify-center text-[#2563EB] font-bold text-lg">
                                                            {doctor.fullName.charAt(0)}
                                                        </div>
                                                    )}
                                                </div>
                                                <div className="flex-1 min-w-0">
                                                    <p className="text-sm font-bold text-[#102A56] group-hover:text-[#2563EB] transition-colors truncate">
                                                        {doctor.fullName}
                                                    </p>
                                                    <p className="text-[11px] font-medium text-[#5F789A] truncate uppercase tracking-wide">
                                                        {doctor.specializationName}
                                                    </p>
                                                </div>
                                                <div className="w-8 h-8 rounded-full bg-[#F2FAFF] flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all">
                                                    <svg className="w-4 h-4 text-[#2563EB]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                                                    </svg>
                                                </div>
                                            </button>
                                        ))}
                                    </div>
                                    <div className="bg-[#F8FCFF] px-4 py-2.5 text-center border-t border-[#D6EAFE]">
                                        <p className="text-[11px] text-[#5F789A] font-medium italic">Nhấn để xem hồ sơ và đặt lịch khám ngay</p>
                                    </div>
                                </div>
                            )}

                            {showSuggestions && searchQuery.trim().length > 0 && suggestions.length === 0 && (
                                <div className="absolute top-full left-0 right-0 mt-3 bg-white rounded-2xl shadow-2xl border border-[#D6EAFE] p-10 text-center z-[60]">
                                    <div className="w-16 h-16 bg-[#F2FAFF] rounded-full flex items-center justify-center mx-auto mb-4">
                                        <svg className="w-8 h-8 text-[#D6EAFE]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                                        </svg>
                                    </div>
                                    <p className="text-sm text-[#102A56] font-bold">Không tìm thấy bác sĩ nào</p>
                                    <p className="text-xs text-[#5F789A] mt-1">Vui lòng thử tìm kiếm theo tên hoặc chuyên khoa khác</p>
                                </div>
                            )}
                        </div>

                        {/* DANH SÁCH LỢI ÍCH */}
                        <div className="flex flex-wrap justify-center gap-x-8 gap-y-4 max-w-3xl mx-auto">
                            {benefits.map((benefit, index) => (
                                <div key={index} className="flex items-center gap-2 text-left">
                                    <div className="w-5 h-5 rounded-full bg-[#EAF4FF] flex items-center justify-center flex-shrink-0">
                                        <svg className="w-3 h-3 text-[#2563EB]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
                                        </svg>
                                    </div>
                                    <p className="text-sm font-bold text-[#102A56]">
                                        {benefit}
                                    </p>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Hình ảnh bác sĩ nam bên phải (Chỉ hiện trên màn hình lớn) */}
                    <div className="hidden lg:block flex-shrink-0 w-64">
                        <Image
                            src="/doctor-male-v2.png"
                            alt="Bác sĩ"
                            width={256}
                            height={400}
                            className="w-full h-auto object-contain drop-shadow-2xl"
                        />
                    </div>
                </div>
            </div>
        </section>
    );
}

