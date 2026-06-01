"use client";

import { useState } from "react";
import Navbar from "../../components/Navbar";
import Footer from "../../components/Footer";
import PageHeader from "../../components/PageHeader";

const faqs = [
    {
        question: "Làm thế nào để đặt lịch khám trên MedPro?",
        answer: "Để đặt lịch khám, bạn chỉ cần truy cập mục 'Cơ sở y tế' hoặc 'Dịch vụ y tế', chọn bệnh viện/phòng khám mong muốn, chọn ngày giờ và bác sĩ phù hợp, sau đó xác nhận thông tin là hoàn tất."
    },
    {
        question: "Tôi có thể hủy lịch khám đã đặt không?",
        answer: "Có, bạn hoàn toàn có thể hủy lịch khám. Bạn vào mục 'Lịch sử khám' trong tài khoản cá nhân, chọn lịch hẹn muốn hủy và nhấn 'Hủy'. Lưu ý quy định thời gian hủy tối thiểu theo từng bệnh viện."
    },
    {
        question: "Quy trình hoàn phí diễn ra như thế nào?",
        answer: "Sau khi lịch khám được hủy thành công theo đúng quy định, hệ thống sẽ tự động thực hiện hoàn phí vào tài khoản gốc bạn đã dùng để thanh toán trong vòng 3-5 ngày làm việc."
    },
    {
        question: "Tôi có cần mang gì khi đi khám theo lịch đặt trước?",
        answer: "Khi đi khám, bạn chỉ cần xuất trình mã số phiếu khám (được gửi qua tin nhắn hoặc hiển thị trong đơn đặt khám trên ứng dụng) kèm thẻ BHYT và giấy tờ tùy thân tại quầy ưu tiên."
    }
];

export default function FAQPage() {
    const [activeIndex, setActiveIndex] = useState<number | null>(0);

    return (
        <main className="min-h-screen bg-white">
            <Navbar />
            <div className="pt-32 max-w-4xl mx-auto px-4">
                <PageHeader 
                    title="Câu hỏi thường gặp" 
                    backHref="/"
                />

                <section className="max-w-3xl mx-auto px-4 py-20">
                    <div className="space-y-4">
                        {faqs.map((faq, index) => (
                            <div 
                                key={index} 
                                className={`border rounded-2xl overflow-hidden transition-all duration-300 ${activeIndex === index ? "border-[#7C6EE6] bg-[#7C6EE6]/5 shadow-lg shadow-[#7C6EE6]/5" : "border-gray-200"}`}
                            >
                                <button 
                                    className="w-full flex items-center justify-between px-6 py-5 text-left"
                                    onClick={() => setActiveIndex(activeIndex === index ? null : index)}
                                >
                                    <span className={`font-bold text-lg ${activeIndex === index ? "text-[#392E7B]" : "text-gray-700"}`}>
                                        {faq.question}
                                    </span>
                                    <svg 
                                        className={`w-5 h-5 text-gray-400 transition-transform duration-300 ${activeIndex === index ? "rotate-180 text-[#7C6EE6]" : ""}`} 
                                        fill="none" 
                                        stroke="currentColor" 
                                        viewBox="0 0 24 24"
                                    >
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                    </svg>
                                </button>
                                <div 
                                    className={`px-6 transition-all duration-300 overflow-hidden ${activeIndex === index ? "max-h-[500px] pb-6 opacity-100" : "max-h-0 opacity-0"}`}
                                >
                                    <p className="text-gray-500 leading-relaxed text-sm lg:text-base">
                                        {faq.answer}
                                    </p>
                                </div>
                            </div>
                        ))}
                    </div>
                </section>
            </div>
            <Footer />
        </main>
    );
}

