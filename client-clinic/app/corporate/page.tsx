"use client";

import { useRouter } from "next/navigation";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PageHeader from "../components/PageHeader";

// PLACEHOLDER DATA: Chưa có backend API cho sức khỏe doanh nghiệp.
// Thay bằng call API khi backend sẵn sàng.
const PACKAGES = [
    {
        id: 1,
        name: "Gói Cơ bản",
        price: 890000,
        perEmployee: true,
        badge: "Phổ biến",
        badgeColor: "bg-emerald-100 text-emerald-700",
        features: [
            "Khám sức khỏe định kỳ 1 lần/năm",
            "Xét nghiệm máu cơ bản",
            "Đo huyết áp, BMI",
            "Tư vấn bác sĩ đa khoa",
            "Báo cáo sức khỏe cá nhân",
        ],
        highlight: false,
    },
    {
        id: 2,
        name: "Gói Tiêu chuẩn",
        price: 1590000,
        perEmployee: true,
        badge: "Được chọn nhiều nhất",
        badgeColor: "bg-[#7C6EE6] text-white",
        features: [
            "Tất cả tính năng Cơ bản",
            "Siêu âm bụng tổng quát",
            "Điện tâm đồ (ECG)",
            "Xét nghiệm chức năng gan, thận",
            "Khám chuyên khoa (chọn 1)",
            "Dashboard quản lý HR",
        ],
        highlight: true,
    },
    {
        id: 3,
        name: "Gói Toàn diện",
        price: 2890000,
        perEmployee: true,
        badge: "Premium",
        badgeColor: "bg-amber-100 text-amber-700",
        features: [
            "Tất cả tính năng Tiêu chuẩn",
            "MRI / CT scan (nếu cần)",
            "Khám chuyên khoa không giới hạn",
            "Tele-health không giới hạn",
            "Bảo hiểm sức khỏe nhóm",
            "Báo cáo phân tích nhóm",
            "Hỗ trợ 24/7",
        ],
        highlight: false,
    },
];

const BENEFITS = [
    { icon: "📊", title: "Dashboard HR thông minh", desc: "Theo dõi sức khỏe toàn bộ nhân viên qua 1 giao diện" },
    { icon: "📅", title: "Đặt lịch linh hoạt", desc: "Khám tại phòng khám hoặc khám tại công ty" },
    { icon: "🔒", title: "Bảo mật dữ liệu", desc: "Dữ liệu sức khỏe được mã hóa, riêng tư tuyệt đối" },
    { icon: "📋", title: "Báo cáo chuyên sâu", desc: "Báo cáo sức khỏe nhóm, xu hướng bệnh tật hàng năm" },
];

function formatPrice(n: number) {
    return new Intl.NumberFormat("vi-VN").format(n) + "đ";
}

export default function CorporatePage() {
    const router = useRouter();

    return (
        <>
            <Navbar />
            <div className="min-h-screen bg-gradient-to-b from-[#f0f8ff] to-white pt-36">
                <div className="max-w-5xl mx-auto px-4 py-8">
                    <PageHeader
                        title="Sức khỏe doanh nghiệp"
                        subtitle="Giải pháp chăm sóc sức khỏe toàn diện cho đội ngũ nhân viên của bạn"
                        backHref="/"
                        backLabel="Trang chủ"
                        icon={
                            <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                        }
                    />

                    {/* Data notice */}
                    <div className="mb-6 flex items-center gap-2 bg-amber-50 border border-amber-200 rounded-xl px-4 py-2.5">
                        <span>📦</span>
                        <p className="text-xs text-amber-700">Dữ liệu mẫu — chưa kết nối API. Tích hợp backend khi sẵn sàng.</p>
                    </div>

                    {/* Benefits */}
                    <h2 className="text-lg font-bold text-[#392E7B] mb-4">Tại sao chọn chúng tôi?</h2>
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-10">
                        {BENEFITS.map((b, i) => (
                            <div key={i} className="bg-white rounded-2xl border border-gray-100 p-4 text-center shadow-sm hover:shadow-md hover:-translate-y-0.5 transition-all">
                                <div className="text-3xl mb-2">{b.icon}</div>
                                <p className="font-bold text-[#392E7B] text-xs mb-1">{b.title}</p>
                                <p className="text-[11px] text-gray-400 leading-tight">{b.desc}</p>
                            </div>
                        ))}
                    </div>

                    {/* Pricing */}
                    <h2 className="text-lg font-bold text-[#392E7B] mb-4">Bảng giá gói doanh nghiệp</h2>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-5 mb-10">
                        {PACKAGES.map((pkg) => (
                            <div
                                key={pkg.id}
                                className={`bg-white rounded-2xl border shadow-sm flex flex-col transition-all duration-200 hover:shadow-xl hover:-translate-y-1 ${
                                    pkg.highlight ? "border-[#7C6EE6] ring-2 ring-[#7C6EE6]/20" : "border-gray-100"
                                }`}
                            >
                                <div className={`px-6 pt-6 pb-4 ${pkg.highlight ? "bg-gradient-to-br from-[#F3F0FF] to-white rounded-t-2xl" : ""}`}>
                                    <span className={`text-[10px] font-bold px-2.5 py-1 rounded-full ${pkg.badgeColor}`}>
                                        {pkg.badge}
                                    </span>
                                    <h3 className="text-xl font-bold text-[#392E7B] mt-3">{pkg.name}</h3>
                                    <div className="mt-2">
                                        <span className="text-2xl font-bold text-[#f26522]">{formatPrice(pkg.price)}</span>
                                        {pkg.perEmployee && <span className="text-xs text-gray-400"> / nhân viên / năm</span>}
                                    </div>
                                </div>
                                <ul className="px-6 pb-4 flex-1 space-y-2">
                                    {pkg.features.map((f, i) => (
                                        <li key={i} className="flex items-start gap-2 text-sm text-gray-600">
                                            <svg className="w-4 h-4 text-[#7C6EE6] flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                                                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                                            </svg>
                                            {f}
                                        </li>
                                    ))}
                                </ul>
                                <div className="px-6 pb-6">
                                    <button
                                        onClick={() => router.push("/doctor")}
                                        className={`w-full py-3 rounded-xl text-sm font-bold transition-all ${
                                            pkg.highlight
                                                ? "bg-[#7C6EE6] text-white hover:bg-[#6D5DD3] shadow-lg shadow-[#7C6EE6]/30"
                                                : "border border-[#7C6EE6] text-[#7C6EE6] hover:bg-[#F3F0FF]"
                                        }`}
                                    >
                                        Liên hệ tư vấn
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* CTA */}
                    <div className="bg-gradient-to-r from-[#7C6EE6] to-[#6D5DD3] rounded-3xl p-8 text-white text-center">
                        <h3 className="text-2xl font-bold mb-2">Doanh nghiệp của bạn có hơn 100 nhân viên?</h3>
                        <p className="text-white/80 text-sm mb-5">Liên hệ để nhận báo giá riêng và ưu đãi đặc biệt cho doanh nghiệp lớn.</p>
                        <button
                            onClick={() => router.push("/doctor")}
                            className="bg-white text-[#7C6EE6] font-bold px-8 py-3 rounded-xl hover:bg-gray-100 transition-colors text-sm"
                        >
                            Gặp chuyên gia tư vấn →
                        </button>
                    </div>
                </div>
            </div>
            <Footer />
        </>
    );
}

