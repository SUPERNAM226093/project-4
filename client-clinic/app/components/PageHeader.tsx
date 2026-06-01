"use client";

/**
 * FILE: PageHeader.tsx
 * MÔ TẢ: Thành phần hiển thị tiêu đề trang kèm nút quay lại và các thông số thống kê (nếu có).
 * Thường được dùng ở đầu các trang danh mục hoặc hồ sơ chi tiết.
 */
import { useRouter } from "next/navigation";

interface PageHeaderProps {
    title: string; // Tiêu đề chính của trang
    subtitle?: string; // Phụ đề mô tả ngắn gọn
    backLabel?: string; // Nhãn của nút quay lại (mặc định là "Quay lại")
    backHref?: string; // Đường dẫn cụ thể để quay lại (nếu không có sẽ dùng history.back)
    icon?: React.ReactNode; // Icon trang trí bên cạnh tiêu đề
    stats?: { label: string; value: string | number }[]; // Các chỉ số thống kê hiển thị ở dưới
}

export default function PageHeader({
    title,
    subtitle,
    backLabel = "Quay lại",
    backHref,
    icon,
    stats,
}: PageHeaderProps) {
    const router = useRouter();

    /**
     * HÀM: handleBack
     * MÔ TẢ: Xử lý sự kiện khi người dùng nhấn nút quay lại.
     */
    const handleBack = () => {
        if (backHref) router.push(backHref);
        else router.back();
    };

    return (
        <div className="mb-8">
            {/* NÚT QUAY LẠI (Back Button) */}
            <button
                onClick={handleBack}
                className="flex items-center gap-2 text-sm text-gray-500 hover:text-black mb-6 transition-colors"
            >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                </svg>
                {backLabel}
            </button>

            {/* THẺ TIÊU ĐỀ (Hero Card) - Thiết kế dạng thẻ bo góc với nền đen sang trọng */}
            <div className="bg-white rounded-3xl shadow-sm border border-gray-100 overflow-hidden">
                <div className="bg-gradient-to-r from-[#1a8fe3] to-[#0d6cbf] px-8 py-10 relative overflow-hidden">
                    {/* Các hình tròn trang trí mờ ảo ở nền */}
                    <div className="absolute -top-12 -right-12 w-40 h-40 bg-white/10 rounded-full" />
                    <div className="absolute -bottom-10 -left-10 w-32 h-32 bg-white/5 rounded-full" />
                    
                    <div className="relative z-10 flex items-center gap-5">
                        {/* Hiển thị Icon nếu có */}
                        {icon && (
                            <div className="w-16 h-16 rounded-2xl bg-white/20 backdrop-blur-sm flex items-center justify-center border border-white/20 flex-shrink-0">
                                {icon}
                            </div>
                        )}
                        <div>
                            <h1 className="text-2xl md:text-3xl font-bold text-white mb-1">{title}</h1>
                            {subtitle && (
                                <p className="text-white/80 text-sm leading-relaxed max-w-xl">{subtitle}</p>
                            )}
                        </div>
                    </div>
                </div>

                {/* PHẦN THỐNG KÊ (Stats Bar) - Hiển thị các chỉ số bên dưới tiêu đề */}
                {stats && stats.length > 0 && (
                    <div className="px-8 py-4 flex flex-wrap gap-8 border-t border-gray-100 bg-white">
                        {stats.map((s, i) => (
                            <div key={i} className="flex items-center gap-2 text-sm">
                                <span className="font-bold text-[#0d2d6b]">{s.value}</span>
                                <span className="text-gray-500">{s.label}</span>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}
