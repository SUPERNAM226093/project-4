"use client";

import { useRouter } from "next/navigation";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PageHeader from "../components/PageHeader";

const BENEFITS = [
    { icon: "📅", title: "Đặt lịch khám hộ", desc: "Không cần tự gọi điện, nhân viên hỗ trợ đặt lịch và xác nhận giúp bạn." },
    { icon: "🚗", title: "Hỗ trợ di chuyển", desc: "Đặt xe đưa đón từ nhà đến bệnh viện và về lại an toàn, tiện lợi." },
    { icon: "📝", title: "Xử lý giấy tờ", desc: "Hỗ trợ điền form, scan hồ sơ, sao chép kết quả xét nghiệm." },
    { icon: "👨‍⚕️", title: "Thông dịch chuyên sâu", desc: "Giải thích chẩn đoán và hướng dẫn điều trị cho bệnh nhân cao tuổi." },
    { icon: "💊", title: "Mua thuốc hộ", desc: "Mua đúng đơn thuốc, giao về tận nhà sau khi khám." },
    { icon: "📞", title: "Hỗ trợ 24/7", desc: "Đội ngũ trực tuyến sẵn sàng giải đáp và hỗ trợ bất cứ lúc nào." },
];

const STEPS = [
    { step: "01", title: "Liên hệ đặt dịch vụ", desc: "Gọi hotline 1900 2267 hoặc đặt online qua form" },
    { step: "02", title: "Tư vấn nhu cầu", desc: "Nhân viên gọi lại trong vòng 15 phút để xác nhận yêu cầu" },
    { step: "03", title: "Bố trí nhân viên hỗ trợ", desc: "Nhân viên đến đúng giờ theo lịch hẹn" },
    { step: "04", title: "Đồng hành toàn trình", desc: "Hỗ trợ từ khi rời nhà đến lúc về tới nơi" },
];

export default function ServiceRegistrationPage() {
    const router = useRouter();

    return (
        <>
            <Navbar />
            <div className="min-h-screen bg-white pt-36">
                <div className="max-w-4xl mx-auto px-4 py-8">
                    <PageHeader
                        title="Hỗ trợ bạn đi khám bệnh"
                        subtitle="Dịch vụ đồng hành cá nhân — đặt lịch, di chuyển, xử lý giấy tờ y tế"
                        backHref="/"
                        backLabel="Trang chủ"
                        icon={
                            <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
                            </svg>
                        }
                    />

                    {/* Hero call strip */}
                    <div className="bg-black rounded-2xl p-5 mb-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-white">
                        <div>
                            <p className="text-xs font-semibold opacity-80 mb-0.5">HOTLINE HỖ TRỢ 24/7</p>
                            <p className="text-2xl font-bold tracking-wide">1900 2267</p>
                        </div>
                        <a
                            href="tel:19002267"
                            className="bg-white text-black font-bold px-7 py-3 rounded-xl text-sm hover:bg-gray-100 transition-colors whitespace-nowrap"
                        >
                            📞 Gọi ngay
                        </a>
                    </div>

                    {/* Benefits */}
                    <h2 className="text-lg font-bold text-[#000000] mb-4">Chúng tôi hỗ trợ gì?</h2>
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
                    <h2 className="text-lg font-bold text-[#000000] mb-4">Quy trình đăng ký</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-10">
                        {STEPS.map((s, i) => (
                            <div key={i} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 flex gap-4">
                                <div className="w-10 h-10 rounded-xl bg-gray-100 flex items-center justify-center flex-shrink-0">
                                    <span className="text-black font-extrabold text-sm">{s.step}</span>
                                </div>
                                <div>
                                    <h3 className="font-bold text-[#000000] text-sm mb-1">{s.title}</h3>
                                    <p className="text-xs text-gray-500 leading-relaxed">{s.desc}</p>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* CTA */}
                    <div className="bg-black rounded-3xl p-8 text-white text-center">
                        <h3 className="text-2xl font-bold mb-2">Sẵn sàng để được hỗ trợ?</h3>
                        <p className="text-white/80 text-sm mb-5">Gần 1.000 nhân viên hỗ trợ trên toàn quốc — liên hệ ngay hôm nay.</p>
                        <div className="flex flex-col sm:flex-row gap-3 justify-center">
                            <a
                                href="tel:19002267"
                                className="bg-white text-black font-bold px-8 py-3 rounded-xl text-sm hover:bg-gray-100 transition-colors"
                            >
                                📞 Gọi 1900 2267
                            </a>
                            <button
                                onClick={() => router.push("/doctor")}
                                className="border-2 border-white text-white font-bold px-8 py-3 rounded-xl text-sm hover:bg-white/10 transition-colors"
                            >
                                Xem bác sĩ trước
                            </button>
                        </div>
                    </div>

                    {/* Note placeholder */}
                    <p className="text-center text-xs text-gray-400 mt-6">
                        📦 Form đăng ký online đang được phát triển — hiện sử dụng hotline để đặt dịch vụ.
                    </p>
                </div>
            </div>
            <Footer />
        </>
    );
}

