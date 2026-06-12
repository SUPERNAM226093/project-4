"use client";

/**
 * FILE: Partners.tsx
 * MÔ TẢ: Thành phần hiển thị danh sách các bệnh viện/phòng khám đối tác liên kết.
 * Sử dụng thanh cuộn ngang tự động (Auto-scrolling carousel).
 */
import { useState, useEffect, useRef } from "react";

import { useRouter } from "next/navigation";
import { HOSPITALS } from "../lib/hospitals";

export default function Partners() {
    const router = useRouter();
    const [currentIndex, setCurrentIndex] = useState(0); // Vị trí hiện tại của carousel
    const containerRef = useRef<HTMLDivElement>(null);
    const itemsPerView = 5; // Số lượng đối tác hiển thị cùng lúc trên một màn hình

    const maxIndex = Math.max(0, HOSPITALS.length - itemsPerView);

    // Các hàm điều khiển cuộn
    const goNext = () => setCurrentIndex((prev) => Math.min(prev + 1, maxIndex));
    const goPrev = () => setCurrentIndex((prev) => Math.max(prev - 1, 0));

    /**
     * HÀM 1: useEffect Auto-scroll
     * MÔ TẢ: Tự động chuyển đổi đối tác sau mỗi 4 giây.
     */
    useEffect(() => {
        const interval = setInterval(() => {
            setCurrentIndex((prev) => (prev >= maxIndex ? 0 : prev + 1));
        }, 4000);
        return () => clearInterval(interval);
    }, [maxIndex]);

    return (
        <section className="py-16 bg-white/50">
            <div className="max-w-7xl mx-auto px-4">
                {/* Tiêu đề mục Đối tác - Modern Navy Style */}
                <div className="text-center mb-12">
                    <h2 className="text-2xl md:text-[28px] font-extrabold text-[#0a3d2e] tracking-tight">
                        {"CƠ SỞ Y TẾ"}
                    </h2>
                    <div className="w-16 h-1 bg-[#0d6b52] mx-auto mt-4 rounded-full opacity-20" />
                </div>

                <div className="relative group/carousel">
                    {/* NÚT ĐIỀU HƯỚNG TRÁI/PHẢI - Premium Style */}
                    <button
                        onClick={goPrev}
                        disabled={currentIndex === 0}
                        className="absolute left-0 top-1/2 -translate-y-1/2 -translate-x-6 z-10 w-12 h-12 rounded-full bg-white shadow-xl flex items-center justify-center text-[#0d6b52] hover:bg-[#0d6b52] hover:text-white transition-all opacity-0 group-hover/carousel:opacity-100 disabled:opacity-0"
                    >
                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M15 19l-7-7 7-7" />
                        </svg>
                    </button>

                    <button
                        onClick={goNext}
                        disabled={currentIndex >= maxIndex}
                        className="absolute right-0 top-1/2 -translate-y-1/2 translate-x-6 z-10 w-12 h-12 rounded-full bg-white shadow-xl flex items-center justify-center text-[#0d6b52] hover:bg-[#0d6b52] hover:text-white transition-all opacity-0 group-hover/carousel:opacity-100 disabled:opacity-0"
                    >
                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M9 5l7 7-7 7" />
                        </svg>
                    </button>

                    {/* VÙNG HIỂN THỊ DANH SÁCH ĐỐI TÁC */}
                    <div className="overflow-hidden" ref={containerRef}>
                        <div
                            className="flex transition-transform duration-700 cubic-bezier(0.4, 0, 0.2, 1)"
                            style={{ transform: `translateX(-${currentIndex * (100 / itemsPerView)}%)` }}
                        >
                            {HOSPITALS.map((hospital) => (
                                <div
                                    key={hospital.id}
                                    className="flex-shrink-0 px-4"
                                    style={{ width: `${100 / itemsPerView}%` }}
                                >
                                    <button
                                        onClick={() => router.push(`/hospitals/${hospital.slug}`)}
                                        className="flex flex-col items-center gap-4 group/card w-full cursor-pointer py-4"
                                    >
                                        <div
                                            className="w-24 h-24 rounded-[2rem] bg-white border border-[#b2e8d9]
                                                flex items-center justify-center text-[#0a3d2e] text-3xl font-bold
                                                shadow-sm group-hover/card:shadow-[0_15px_30px_-10px_rgba(13,107,82,0.2)] 
                                                transition-all duration-500 group-hover/card:border-[#0d6b52]
                                                group-hover/card:scale-105 group-hover/card:-translate-y-2 relative overflow-hidden"
                                        >
                                            {hospital.image ? (
                                                <img src={hospital.image} alt={hospital.name} className="w-14 h-14 object-contain transition-transform group-hover/card:scale-110" />
                                            ) : (
                                                hospital.name.charAt(0)
                                            )}
                                        </div>
                                        <div className="text-center px-2">
                                            <p className="text-xs text-[#4d8871] font-bold leading-relaxed group-hover/card:text-[#0d6b52] transition-colors line-clamp-2">
                                                {hospital.name}
                                                {hospital.verified && (
                                                    <span className="inline-block ml-1 text-[#0d6b52]">
                                                        <svg className="w-3.5 h-3.5 inline" fill="currentColor" viewBox="0 0 20 20">
                                                            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                                                        </svg>
                                                    </span>
                                                )}
                                            </p>
                                        </div>
                                    </button>
                                </div>
                            ))}

                            {/* Nút "Xem tất cả" */}
                            <div className="flex-shrink-0 px-4" style={{ width: `${100 / itemsPerView}%` }}>
                                <button
                                    onClick={() => router.push("/hospitals")}
                                    className="flex flex-col items-center gap-4 group/card w-full cursor-pointer py-4"
                                >
                                    <div className="w-24 h-24 rounded-[2rem] bg-[var(--green-ultra)] border-2 border-dashed border-[#b2e8d9] flex items-center justify-center group-hover/card:border-[#0d6b52] group-hover/card:bg-white transition-all duration-500 group-hover/card:-translate-y-2 group-hover/card:shadow-[0_15px_30px_-10px_rgba(13,107,82,0.2)]">
                                        <svg className="w-10 h-10 text-[#4d8871] group-hover/card:text-[#0d6b52] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7l5 5m0 0l-5 5m5-5H6" />
                                        </svg>
                                    </div>
                                    <p className="text-xs text-[#4d8871] font-bold group-hover/card:text-[#0a3d2e] transition-colors uppercase tracking-wider">
                                        {"Xem tất cả"}
                                    </p>
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Indicator Dots - Premium Blue Style */}
                    <div className="flex justify-center gap-2.5 mt-8">
                        {Array.from({ length: maxIndex + 1 }).map((_, i) => (
                            <button
                                key={i}
                                onClick={() => setCurrentIndex(i)}
                                className={`h-2 rounded-full transition-all duration-500 ${i === currentIndex ? "w-10 bg-[#0d6b52]" : "w-2 bg-[#b2e8d9] hover:bg-[#0d6b52]/40"
                                    }`}
                            />
                        ))}
                    </div>
                </div>
            </div>
        </section>
    );
}
