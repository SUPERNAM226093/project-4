"use client";

import { useRouter } from "next/navigation";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PageHeader from "../components/PageHeader";

const BENEFITS = [
    { icon: "⚡", title: "Không cần xếp hàng", desc: "Phòng khám chờ riêng, ưu tiên thanh toán và lấy số thứ tự." },
    { icon: "🏥", title: "Hơn 300 cơ sở y tế", desc: "Bệnh viện công, tư, phòng khám trên toàn quốc đều hỗ trợ." },
    { icon: "📱", title: "Đặt khám trong 60 giây", desc: "Chọn bác sĩ, chọn giờ, xác nhận ngay trên ứng dụng/web." },
    { icon: "🔔", title: "Nhắc lịch tự động", desc: "SMS và push notification nhắc lịch trước 24h và 1h." },
    { icon: "🔄", title: "Dễ đổi / hủy lịch", desc: "Thay đổi lịch hẹn bất cứ lúc nào, miễn phí 100%." },
    { icon: "📋", title: "Hồ sơ khám lưu sẵn", desc: "Kết quả xét nghiệm, đơn thuốc, lịch sử khám được lưu trữ." },
];

const STEPS = [
    { step: "01", title: "Chọn chuyên khoa", desc: "Mô tả triệu chứng hoặc chọn trực tiếp chuyên khoa phù hợp" },
    { step: "02", title: "Chọn bác sĩ & giờ khám", desc: "Xem lịch trống theo thời gian thực, chọn giờ thuận tiện nhất" },
    { step: "03", title: "Xác nhận & thanh toán", desc: "Xác nhận thông tin, thanh toán online hoặc tại quầy" },
    { step: "04", title: "Đến khám đúng giờ", desc: "Đến đúng giờ, không phải xếp hàng — vào thẳng phòng khám" },
];

export default function PriorityBookingPage() {
    const router = useRouter();

    return (
        <>
            <Navbar />
            <div className="min-h-screen bg-gradient-to-b from-[#E6EFFF] to-white pt-36">
                <div className="max-w-4xl mx-auto px-4 py-8">
                    <PageHeader
                        title="Đặt khám trước — không xếp hàng"
                        subtitle="Bỏ qua hàng chờ, đến đúng giờ, khám ngay — hơn 300 cơ sở y tế trên toàn quốc"
                        backHref="/"
                        backLabel="Trang chủ"
                        icon={
                            <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                            </svg>
                        }
                    />

                    {/* Stat strip */}
                    <div className="grid grid-cols-3 gap-4 mb-8">
                        {[
                            { value: "300+", label: "Cơ sở y tế" },
                            { value: "60s", label: "Đặt khám xong" },
                            { value: "0đ", label: "Phí đặt lịch" },
                        ].map((s, i) => (
                            <div key={i} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 text-center">
                                <p className="text-2xl font-extrabold text-[#0065FF]">{s.value}</p>
                                <p className="text-xs text-gray-500 mt-0.5">{s.label}</p>
                            </div>
                        ))}
                    </div>

                    {/* Benefits */}
                    <h2 className="text-lg font-bold text-[#000000] mb-4">Vì sao nên đặt khám trước?</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-10">
                        {BENEFITS.map((b, i) => (
                            <div key={i} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 flex gap-4 hover:shadow-md hover:-translate-y-0.5 transition-all">
                                <div className="text-3xl flex-shrink-0">{b.icon}</div>
                                <div>
                                    <h3 className="font-bold text-[#000000] text-sm mb-1">{b.title}</h3>
                                    <p className="text-xs text-gray-500 leading-relaxed">{b.desc}</p>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* Steps */}
                    <h2 className="text-lg font-bold text-[#000000] mb-4">Quy trình đặt khám</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-10">
                        {STEPS.map((s, i) => (
                            <div key={i} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 flex gap-4">
                                <div className="w-10 h-10 rounded-xl bg-[#0065FF]/10 flex items-center justify-center flex-shrink-0">
                                    <span className="text-[#0065FF] font-extrabold text-sm">{s.step}</span>
                                </div>
                                <div>
                                    <h3 className="font-bold text-[#000000] text-sm mb-1">{s.title}</h3>
                                    <p className="text-xs text-gray-500 leading-relaxed">{s.desc}</p>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* CTA */}
                    <div className="bg-gradient-to-r from-[#000000] to-[#0065FF] rounded-3xl p-8 text-white text-center">
                        <h3 className="text-2xl font-bold mb-2">Đặt khám ngay – miễn phí!</h3>
                        <p className="text-white/80 text-sm mb-5">Chọn chuyên khoa và đặt lịch trong vòng 60 giây.</p>
                        <div className="flex flex-col sm:flex-row gap-3 justify-center">
                            <button
                                onClick={() => router.push("/specialization")}
                                className="bg-white text-[#0065FF] font-bold px-8 py-3 rounded-xl text-sm hover:bg-gray-100 transition-colors"
                            >
                                Chọn chuyên khoa →
                            </button>
                            <button
                                onClick={() => router.push("/doctor")}
                                className="border-2 border-white text-white font-bold px-8 py-3 rounded-xl text-sm hover:bg-white/10 transition-colors"
                            >
                                Tìm theo bác sĩ
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <Footer />
        </>
    );
}

