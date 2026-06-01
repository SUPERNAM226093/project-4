"use client";

import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PageHeader from "../components/PageHeader";

const contactTypes = [
    {
        title: "Dành cho Bệnh viện",
        desc: "Hợp tác cung cấp giải pháp đặt lịch khám bệnh trực tuyến cho cơ sở y tế.",
        icon: "🏥",
        email: "hospital@medpro.com"
    },
    {
        title: "Dành cho Bác sĩ",
        desc: "Tham gia mạng lưới bác sĩ tư vấn trực tuyến và nâng cao uy tín chuyên môn.",
        icon: "🩺",
        email: "doctor@medpro.com"
    },
    {
        title: "Dành cho Doanh nghiệp",
        desc: "Giải pháp khám sức khỏe định kỳ toàn diện cho đội ngũ nhân viên của bạn.",
        icon: "🏢",
        email: "corporate@medpro.com"
    }
];

export default function ContactPage() {
    return (
        <main className="min-h-screen bg-white">
            <Navbar />
            <div className="pt-32 pb-20 max-w-7xl mx-auto px-4">
                <PageHeader 
                    title="Liên hệ hợp tác" 
                    backHref="/"
                />

                <section className="max-w-4xl mx-auto px-4 py-20">
                    <div className="flex flex-col gap-16">
                        {/* Contact Info Header */}
                        <div className="flex flex-col gap-4 text-center">
                            <h2 className="text-4xl font-black text-[#392E7B]">Đối tác tin cậy</h2>
                            <p className="text-gray-500 leading-relaxed text-lg max-w-2xl mx-auto">
                                Chúng tôi luôn sẵn sàng lắng nghe mọi ý kiến đóng góp và mong muốn được hợp tác cùng các đối tác trong ngành y tế để mang lại giá trị tốt nhất cho người dân.
                            </p>
                        </div>

                        {/* Contact Info Cards */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                            {contactTypes.map((type, idx) => (
                                <div key={idx} className="flex flex-col gap-6 items-center p-8 rounded-3xl bg-gray-50 hover:bg-white hover:shadow-xl hover:shadow-gray-200 transition-all group border border-transparent hover:border-gray-100">
                                    <div className="w-20 h-20 bg-[#7C6EE6]/10 rounded-2xl flex items-center justify-center text-4xl group-hover:scale-110 transition-transform">
                                        {type.icon}
                                    </div>
                                    <div className="flex flex-col gap-2 text-center">
                                        <h3 className="text-xl font-bold text-[#392E7B]">{type.title}</h3>
                                        <p className="text-sm text-gray-500 mb-2">{type.desc}</p>
                                        <a href={`mailto:${type.email}`} className="text-[#7C6EE6] font-bold text-sm hover:underline underline-offset-4">
                                            {type.email}
                                        </a>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </section>
            </div>
            <Footer />
        </main>
    );
}

