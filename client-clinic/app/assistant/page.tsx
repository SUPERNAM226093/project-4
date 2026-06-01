"use client";

import { useRouter } from "next/navigation";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PageHeader from "../components/PageHeader";

const FEATURES = [
    { icon: "💬", title: "Tư vấn sức khỏe tức thì", desc: "Đặt câu hỏi bất cứ lúc nào, AI trả lời ngay lập tức" },
    { icon: "🩺", title: "Gợi ý chuyên khoa", desc: "Mô tả triệu chứng, AI gợi ý chuyên khoa phù hợp" },
    { icon: "💊", title: "Hướng dẫn dùng thuốc", desc: "Thông tin về liều lượng, tác dụng phụ, tương tác thuốc" },
    { icon: "📊", title: "Theo dõi sức khỏe", desc: "Lưu lịch sử hỏi đáp, nhắc nhở tái khám" },
];

const FAQS = [
    { q: "Trợ lý AI có thể thay thế bác sĩ không?", a: "Không. Trợ lý AI chỉ cung cấp thông tin tham khảo và tư vấn chuyên khoa ban đầu. Quyết định chẩn đoán và điều trị thuộc về bác sĩ." },
    { q: "Dữ liệu của tôi có được bảo mật không?", a: "Có. Mọi cuộc hội thoại đều được mã hóa end-to-end và không chia sẻ với bên thứ ba." },
    { q: "Trợ lý AI có hỗ trợ tiếng Việt không?", a: "Có, trợ lý được tối ưu cho tiếng Việt và hiểu được các triệu chứng mô tả theo cách nói thông thường." },
];

export default function AssistantPage() {
    const router = useRouter();

    return (
        <>
            <Navbar />
            <div className="min-h-screen bg-gradient-to-b from-[#f0f8ff] to-white pt-36">
                <div className="max-w-4xl mx-auto px-4 py-8">
                    <PageHeader
                        title="Trợ lý sức khỏe cá nhân"
                        subtitle="AI hỗ trợ bạn 24/7 — tư vấn triệu chứng, gợi ý chuyên khoa và tìm kiếm thông tin"
                        backHref="/"
                        backLabel="Trang chủ"
                        icon = {
                            <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                            </svg>
                        }
                    />

                    {/* Hero CTA */}
                    <div className="bg-gradient-to-br from-[#7C6EE6] to-[#6D5DD3] rounded-3xl p-8 mb-8 text-white relative overflow-hidden">
                        <div className="absolute -top-10 -right-10 w-40 h-40 bg-white/10 rounded-full" />
                        <div className="absolute -bottom-8 -left-8 w-32 h-32 bg-white/5 rounded-full" />
                        <div className="relative z-10">
                            <div className="flex items-center gap-2 mb-3">
                                <span className="text-2xl">🤖</span>
                                <span className="text-sm font-semibold bg-white/20 px-3 py-1 rounded-full">AI Chatbot đang hoạt động</span>
                            </div>
                            <h2 className="text-2xl font-bold mb-2">Bắt đầu hỏi ngay bây giờ</h2>
                            <p className="text-white/80 text-sm mb-5 max-w-md">
                                Mô tả triệu chứng của bạn, trợ lý AI sẽ phân tích và gợi ý bước tiếp theo phù hợp nhất.
                            </p>
                            {/* Chatbot launcher — the ChatWidget is already on the page globally, this opens it */}
                            <button
                                onClick={() => {
                                    // Trigger the ChatWidget button (already mounted in layout)
                                    const chatBtn = document.querySelector<HTMLButtonElement>("[data-chat-toggle]");
                                    if (chatBtn) chatBtn.click();
                                    else router.push("/");
                                }}
                                className="bg-white text-[#7C6EE6] font-bold px-6 py-3 rounded-xl text-sm hover:bg-gray-100 transition-colors inline-flex items-center gap-2"
                            >
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                                </svg>
                                Mở trợ lý AI
                            </button>
                        </div>
                    </div>

                    {/* Features */}
                    <h2 className="text-lg font-bold text-[#392E7B] mb-4">Trợ lý AI có thể làm gì?</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-10">
                        {FEATURES.map((f, i) => (
                            <div key={i} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 flex gap-4 hover:shadow-md hover:-translate-y-0.5 transition-all">
                                <div className="text-3xl flex-shrink-0">{f.icon}</div>
                                <div>
                                    <h3 className="font-bold text-[#392E7B] text-sm mb-1">{f.title}</h3>
                                    <p className="text-xs text-gray-500 leading-relaxed">{f.desc}</p>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* Sample prompts */}
                    <h2 className="text-lg font-bold text-[#392E7B] mb-4">Ví dụ câu hỏi</h2>
                    <div className="flex flex-wrap gap-2 mb-10">
                        {[
                            "Tôi bị đau đầu và sốt nhẹ, tôi cần khám gì?",
                            "Bác sĩ nào giỏi về tim mạch?",
                            "Tôi bị đau họng thì nên khám khoa nào?",
                            "Thuốc paracetamol dùng liều bao nhiêu?",
                            "Có gói khám sức khỏe nào không?",
                        ].map((prompt, i) => (
                            <button
                                key={i}
                                onClick={() => {
                                    const chatBtn = document.querySelector<HTMLButtonElement>("[data-chat-toggle]");
                                    if (chatBtn) chatBtn.click();
                                    else router.push("/");
                                }}
                                className="text-xs bg-[#F3F0FF] text-[#7C6EE6] border border-[#7C6EE6]/20 px-3 py-2 rounded-xl hover:bg-[#7C6EE6] hover:text-white transition-colors"
                            >
                                {prompt}
                            </button>
                        ))}
                    </div>

                    {/* FAQ */}
                    <h2 className="text-lg font-bold text-[#392E7B] mb-4">Câu hỏi thường gặp</h2>
                    <div className="space-y-3 mb-8">
                        {FAQS.map((faq, i) => (
                            <div key={i} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5">
                                <p className="font-semibold text-[#392E7B] text-sm mb-2">❓ {faq.q}</p>
                                <p className="text-xs text-gray-600 leading-relaxed">{faq.a}</p>
                            </div>
                        ))}
                    </div>

                    {/* Fallback CTA */}
                    <div className="text-center">
                        <p className="text-sm text-gray-500 mb-3">Muốn tư vấn trực tiếp với bác sĩ thật?</p>
                        <button
                            onClick={() => router.push("/doctor")}
                            className="bg-[#7C6EE6] text-white font-bold px-8 py-3 rounded-xl text-sm hover:bg-[#6D5DD3] shadow-lg shadow-[#7C6EE6]/30 transition-all"
                        >
                            Đặt lịch gặp bác sĩ →
                        </button>
                    </div>
                </div>
            </div>
            <Footer />
        </>
    );
}

