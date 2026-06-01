"use client";

import { useParams, useRouter } from "next/navigation";
import { getHospitalBySlug } from "../../lib/hospitals";
import Navbar from "../../components/Navbar";
import Footer from "../../components/Footer";

export default function HospitalDetailPage() {
    const params = useParams();
    const router = useRouter();
    const slug = typeof params.slug === "string" ? params.slug : "";
    const hospital = getHospitalBySlug(slug);

    if (!hospital) {
        return (
            <>
                <Navbar />
                <div className="min-h-screen bg-gradient-to-b from-[#f0f8ff] to-white flex items-center justify-center pt-36">
                    <div className="bg-white rounded-3xl shadow-lg p-12 text-center max-w-md">
                        <svg className="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5" />
                        </svg>
                        <h2 className="text-xl font-bold text-[#392E7B] mb-2">Không tìm thấy bệnh viện</h2>
                        <p className="text-sm text-gray-400 mb-6">Bệnh viện bạn đang tìm kiếm không tồn tại hoặc đã bị xóa.</p>
                        <button
                            onClick={() => router.push("/")}
                            className="bg-[#7C6EE6] text-white font-semibold px-6 py-2.5 rounded-xl text-sm hover:bg-[#6D5DD3] transition-colors"
                        >
                            ← Về trang chủ
                        </button>
                    </div>
                </div>
                <Footer />
            </>
        );
    }

    return (
        <>
            <Navbar />
            <div className="min-h-screen bg-gradient-to-b from-[#f0f8ff] to-white pt-28">

                {/* ── HERO BANNER ── */}
                <div className={`bg-gradient-to-r ${hospital.color} relative overflow-hidden`}>
                    <div className="absolute inset-0 bg-black/10" />
                    <div className="absolute -top-16 -right-16 w-64 h-64 bg-white/10 rounded-full" />
                    <div className="absolute -bottom-12 -left-12 w-48 h-48 bg-white/5 rounded-full" />

                    <div className="relative z-10 max-w-6xl mx-auto px-4 py-16">

                        <div className="flex items-center gap-6">
                            {/* Logo / Avatar */}
                            <div className="w-24 h-24 rounded-2xl bg-white/20 backdrop-blur-sm border border-white/30 flex items-center justify-center text-white text-4xl font-bold flex-shrink-0 shadow-lg">
                                {hospital.image ? (
                                    <img src={hospital.image} alt={hospital.name} className="w-full h-full object-cover rounded-2xl" />
                                ) : (
                                    hospital.name.charAt(0)
                                )}
                            </div>

                            <div>
                                {hospital.verified && (
                                    <span className="inline-flex items-center gap-1 text-[11px] font-semibold bg-white/20 text-white px-2.5 py-0.5 rounded-full mb-2">
                                        <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                                            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                                        </svg>
                                        Đối tác đã xác thực
                                    </span>
                                )}
                                <h1 className="text-2xl md:text-3xl font-bold text-white leading-tight">{hospital.name}</h1>
                                <p className="text-white/80 text-sm mt-1 max-w-xl">{hospital.shortDescription}</p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* ── CONTENT ── */}
                <div className="max-w-6xl mx-auto px-4 py-10">
                    <div className="space-y-8">
                        {/* main info */}
                        <div className="space-y-8">

                            {/* Giới thiệu */}
                            <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6">
                                <h2 className="text-base font-bold text-[#392E7B] mb-3 flex items-center gap-2">
                                    <svg className="w-5 h-5 text-[#7C6EE6]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                    </svg>
                                    Giới thiệu
                                </h2>
                                <p className="text-sm text-gray-600 leading-relaxed">{hospital.description}</p>
                            </div>

                            {/* Chuyên khoa */}
                            <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6">
                                <h2 className="text-base font-bold text-[#392E7B] mb-4 flex items-center gap-2">
                                    <svg className="w-5 h-5 text-[#7C6EE6]" fill="currentColor" viewBox="0 0 20 20">
                                        <path fillRule="evenodd" d="M7 2a1 1 0 00-.707 1.707L7 4.414v3.758a1 1 0 01-.293.707l-4 4C.817 14.769 2.156 18 4.828 18h10.343c2.673 0 4.012-3.231 2.122-5.121l-4-4A1 1 0 0113 8.172V4.414l.707-.707A1 1 0 0013 2H7zm2 6.172V4h2v4.172a3 3 0 00.879 2.12l1.027 1.028a4 4 0 00-2.171.102l-.47.156a4 4 0 01-2.53 0l-.563-.187 1.116-1.116A3 3 0 009 8.172z" clipRule="evenodd" />
                                    </svg>
                                    Chuyên khoa nổi bật
                                </h2>
                                <div className="flex flex-wrap gap-2">
                                    {hospital.specialties.map((s, i) => (
                                        <span
                                            key={i}
                                            className="px-3 py-1.5 rounded-full bg-[#F3F0FF] text-[#7C6EE6] text-xs font-semibold border border-[#7C6EE6]/10"
                                        >
                                            {s}
                                        </span>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <Footer />
        </>
    );
}
