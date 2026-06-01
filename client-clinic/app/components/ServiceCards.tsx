"use client";
/**
 * FILE: ServiceCards.tsx
 * MÔ TẢ: Thành phần hiển thị các phím tắt dịch vụ nhanh (Đặt khám, Tư vấn video, Xét nghiệm...) ngay dưới banner.
 */

import { useRouter } from "next/navigation";

// 1. Dữ liệu cấu hình cho các thẻ dịch vụ (Icon, Tên tiếng Việt, Đường dẫn chuyển trang)
const serviceIcons = [
    {
        icon: (
            <svg viewBox="0 0 48 48" fill="none" className="w-10 h-10">
                <rect x="4" y="8" width="40" height="32" rx="4" stroke="currentColor" strokeWidth="2.5" />
                <path d="M18 20h12M24 14v12" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" />
            </svg>
        ),
        name: "Đặt khám tại cơ sở",
        href: "/hospitals",
    },
    {
        icon: (
            <svg viewBox="0 0 48 48" fill="none" className="w-10 h-10">
                <rect x="6" y="6" width="36" height="36" rx="6" stroke="currentColor" strokeWidth="2.5" />
                <path d="M16 18h16M16 24h12M16 30h8" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" />
            </svg>
        ),
        name: "Đặt khám chuyên khoa",
        href: "/specialization",
    },
    {
        icon: (
            <svg viewBox="0 0 48 48" fill="none" className="w-10 h-10">
                <rect x="10" y="6" width="28" height="36" rx="4" stroke="currentColor" strokeWidth="2.5" />
                <circle cx="24" cy="20" r="6" stroke="currentColor" strokeWidth="2.5" />
                <path d="M16 36c0-4.418 3.582-8 8-8s8 3.582 8 8" stroke="currentColor" strokeWidth="2.5" />
            </svg>
        ),
        name: "Tư vấn khám từ xa",
        href: "/video-call/booking", 
    },
    {
        icon: (
            <svg viewBox="0 0 48 48" fill="none" className="w-10 h-10">
                <rect x="8" y="10" width="32" height="28" rx="4" stroke="currentColor" strokeWidth="2.5" />
                <path d="M8 18h32" stroke="currentColor" strokeWidth="2.5" />
                <path d="M16 6v8M32 6v8" stroke="currentColor" strokeWidth="2.5" />
            </svg>
        ),
        name: "Gói khám sức khỏe",
        href: "/health-package",
    },

    {
        icon: (
            <svg viewBox="0 0 48 48" fill="none" className="w-10 h-10">
                <path d="M24 4L6 14v20l18 10 18-10V14L24 4z" stroke="currentColor" strokeWidth="2.5" />
                <path d="M24 14v20M14 24h20" stroke="currentColor" strokeWidth="2.5" />
            </svg>
        ),
        name: "Cơ sở y tế liên kết",
        href: "/hospitals",
    },
    {
        icon: (
            <svg viewBox="0 0 48 48" fill="none" className="w-10 h-10">
                <rect x="10" y="6" width="28" height="36" rx="4" stroke="currentColor" strokeWidth="2.5" />
                <path d="M18 16h12M18 24h12" stroke="currentColor" strokeWidth="2.5" />
                <path d="M34 10l4 4-4 4" stroke="currentColor" strokeWidth="2.5" />
            </svg>
        ),
        name: "Hướng dẫn & Câu hỏi",
        href: "/guide/faq",
    },
];

/**
 * COMPONENT: ServiceCards
 * MÔ TẢ: Render danh sách các thẻ dịch vụ dưới dạng hàng ngang và điều hướng trang.
 */
export default function ServiceCards() {
    const router = useRouter(); // Hook dùng để chuyển hướng trang

    return (
        <section className="py-12 bg-transparent">
            <div className="max-w-7xl mx-auto px-4">
                {/* --- 2. VÙNG HIỂN THỊ DANH SÁCH THẺ (Dạng hàng ngang, hỗ trợ cuộn nếu màn hình nhỏ) --- */}
                <div className="flex flex-nowrap justify-center gap-4 overflow-x-auto pb-4">
                    {serviceIcons.map((service, index) => (
                        /* THẺ DỊCH VỤ CÁ NHÂN */
                        <button
                            key={index}
                            onClick={() => service.href && router.push(service.href)}
                            className="group flex flex-col items-center gap-4 p-5 bg-white border border-slate-100 rounded-2xl w-[145px] h-[160px] transition-all duration-300 hover:-translate-y-1.5 hover:shadow-xl hover:shadow-blue-500/10 hover:border-blue-100"
                        >
                            {/* ICON CONTAINER */}
                            <div className="w-16 h-16 rounded-2xl bg-blue-50 flex items-center justify-center transition-all duration-300 group-hover:bg-[#2e5bff]">
                                <div className="transition-all duration-300 text-[#2e5bff] group-hover:text-white">
                                    {/* Clone SVG and apply current color */}
                                    {service.icon}
                                </div>
                            </div>
                            
                            {/* SERVICE LABEL */}
                            <span className="text-[13px] text-center text-slate-700 font-semibold leading-tight px-1 transition-colors group-hover:text-[#2e5bff]">
                                {service.name}
                            </span>
                        </button>
                    ))}
                </div>
            </div>
        </section>
    );
}
