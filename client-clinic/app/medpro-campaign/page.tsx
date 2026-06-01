"use client";

import { useRouter } from "next/navigation";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PageHeader from "../components/PageHeader";

const HIGHLIGHTS = [
    { icon: "🏥", title: "Đặt khám ưu tiên", desc: "Ưu tiên lượt khám, không xếp hàng tại hơn 300 bệnh viện đối tác." },
    { icon: "📱", title: "Quản lý sức khỏe số", desc: "Lưu trữ hồ sơ, xem kết quả xét nghiệm, đơn thuốc mọi lúc mọi nơi." },
];

const NEWS = [
    {
        date: "30/03/2026",
        tag: "Sự kiện",
        title: "MEDPRO đạt 1 triệu người dùng sau 12 tháng",
        desc: "Nền tảng y tế số MEDPRO vừa đạt cột mốc 1 triệu người dùng đăng ký, đánh dấu bước ngoặt trong hành trình số hóa y tế Việt Nam.",
    },
    {
        date: "20/03/2026",
        tag: "Triển khai",
        title: "Tích hợp đặt khám với Bệnh viện Chợ Rẫy",
        desc: "MEDPRO mở rộng mạng lưới đối tác với Bệnh viện Chợ Rẫy, giúp hàng triệu bệnh nhân dễ dàng đặt lịch khám trực tuyến.",
    },
];

export default function MedroCampaignPage() {
    const router = useRouter();

    return (
        <>
            <Navbar />
            <div className="min-h-screen bg-gradient-to-b from-[#f0fff4] to-white pt-36">
                <div className="max-w-4xl mx-auto px-4 py-8">
                    <PageHeader
                        title="MEDPRO – Nền tảng y tế số"
                        subtitle="Tham gia MEDPRO ngay — đặt khám, quản lý sức khỏe trong một ứng dụng"
                        backHref="/"
                        backLabel="Trang chủ"
                        icon={
                            <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                        }
                    />


                    {/* Highlights */}
                    <h2 className="text-lg font-bold text-[#392E7B] mb-4">MEDPRO cung cấp gì?</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-10">
                        {HIGHLIGHTS.map((h, i) => (
                            <div key={i} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 flex gap-4 hover:shadow-md hover:-translate-y-0.5 transition-all">
                                <div className="text-3xl flex-shrink-0">{h.icon}</div>
                                <div>
                                    <h3 className="font-bold text-[#392E7B] text-sm mb-1">{h.title}</h3>
                                    <p className="text-xs text-gray-500 leading-relaxed">{h.desc}</p>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* News */}
                    <h2 className="text-lg font-bold text-[#392E7B] mb-4">Tin tức & sự kiện</h2>
                    <div className="space-y-4 mb-10">
                        {NEWS.map((n, i) => (
                            <div key={i} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 hover:shadow-md hover:-translate-y-0.5 transition-all cursor-pointer">
                                <div className="flex items-center gap-2 mb-2">
                                    <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-green-100 text-green-700">{n.tag}</span>
                                    <span className="text-xs text-gray-400">{n.date}</span>
                                </div>
                                <h3 className="font-bold text-[#392E7B] text-sm mb-1">{n.title}</h3>
                                <p className="text-xs text-gray-500 leading-relaxed">{n.desc}</p>
                            </div>
                        ))}
                    </div>

                    {/* CTA */}
                    <div className="bg-gradient-to-r from-[#2e7d32] to-[#4caf50] rounded-3xl p-8 text-white text-center">
                        <h3 className="text-2xl font-bold mb-2">Tham gia MEDPRO ngay!</h3>
                        <p className="text-white/80 text-sm mb-5">Hơn 1 triệu người dùng đang tin tưởng MEDPRO cho sức khỏe của mình.</p>
                        <div className="flex flex-col sm:flex-row gap-3 justify-center">
                            <button
                                onClick={() => router.push("/specialization")}
                                className="border-2 border-white text-white font-bold px-8 py-3 rounded-xl text-sm hover:bg-white/10 transition-colors"
                            >
                                Đặt khám ngay
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <Footer />
        </>
    );
}

