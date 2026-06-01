"use client";

import { useState } from "react";
import Link from "next/link";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PageHeader from "../components/PageHeader";

import { HOSPITALS } from "../lib/hospitals";

const areas = ["Tất cả", "TP.HCM", "Cần Thơ", "Khác"];

export default function AllHospitalsPage() {
    const [search, setSearch] = useState("");
    const [activeArea, setActiveArea] = useState("Tất cả");

    // Lấy vùng miền từ tên hospital (giả định dựa trên dữ liệu mock)
    const hospitalsWithArea = HOSPITALS.map(h => ({
        ...h,
        area: h.address.includes("Cần Thơ") ? "Cần Thơ" : h.address.includes("Hồ Chí Minh") ? "TP.HCM" : "Khác"
    }));

    const filteredHospitals = hospitalsWithArea.filter(h => {
        const matchesSearch = h.name.toLowerCase().includes(search.toLowerCase());
        const matchesArea = activeArea === "Tất cả" || h.area === activeArea;
        return matchesSearch && matchesArea;
    });

    return (
        <main className="min-h-screen bg-white">
            <Navbar />
            <div className="pt-32 max-w-7xl mx-auto px-4">
                <PageHeader 
                    title="Cơ sở y tế" 
                    backHref="/"
                />

                <section className="max-w-7xl mx-auto px-4 py-12">
                    {/* Filters & Search */}
                    <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 mb-10 flex flex-col md:flex-row gap-6 items-center justify-between">
                        <div className="flex flex-wrap gap-2">
                            {areas.map(area => (
                                <button
                                    key={area}
                                    onClick={() => setActiveArea(area)}
                                    className={`px-5 py-2 rounded-full text-sm font-semibold transition-all ${
                                        activeArea === area 
                                        ? "bg-black text-white shadow-lg" 
                                        : "bg-gray-100 text-gray-500 hover:bg-gray-200"
                                    }`}
                                >
                                    {area}
                                </button>
                            ))}
                        </div>
                        <div className="relative w-full md:w-80">
                            <input 
                                type="text"
                                placeholder="Tìm kiếm bệnh viện..."
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                                className="w-full pl-10 pr-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-black/10 transition-all shadow-inner"
                            />
                            <svg className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                            </svg>
                        </div>
                    </div>

                    {/* Hospital Grid */}
                    <h2 className="text-xl font-black text-black mb-8 uppercase tracking-wider text-center">
                        Được tin tưởng hợp tác và đồng hành
                    </h2>

                    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
                        {filteredHospitals.map((h) => (
                            <Link 
                                key={h.id} 
                                href={`/hospitals/${h.slug}`}
                                className="flex flex-col items-center group cursor-pointer"
                            >
                                <div 
                                    className="w-24 h-24 md:w-28 md:h-28 rounded-[2rem] flex items-center justify-center text-black text-3xl font-black shadow-xl transition-all duration-300 group-hover:scale-110 group-hover:rotate-3 relative bg-white border border-gray-100"
                                >
                                    {h.image ? (
                                        <img src={h.image} alt={h.name} className="w-full h-full object-cover rounded-[2rem]" />
                                    ) : (
                                        h.name.charAt(0)
                                    )}
                                    <div className="absolute -bottom-1 -right-1 bg-white p-1 rounded-full shadow-md">
                                        <div className="bg-black rounded-full p-0.5">
                                            <svg className="w-3 h-3 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
                                            </svg>
                                        </div>
                                    </div>
                                </div>
                                <div className="mt-4 text-center">
                                    <h3 className="text-[13px] md:text-sm font-bold text-black group-hover:text-black group-hover:underline transition-colors leading-tight mb-1 max-w-[160px] mx-auto">
                                        {h.name}
                                        {h.verified && (
                                            <svg className="w-3.5 h-3.5 inline ml-1 text-black" fill="currentColor" viewBox="0 0 20 20">
                                                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                                            </svg>
                                        )}
                                    </h3>
                                    <span className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">
                                        {h.area}
                                    </span>
                                </div>
                            </Link>
                        ))}
                    </div>

                    {filteredHospitals.length === 0 && (
                        <div className="text-center py-20 bg-white rounded-3xl border border-dashed border-gray-200">
                            <p className="text-gray-400">Không tìm thấy bệnh viện nào phù hợp.</p>
                        </div>
                    )}
                </section>
            </div>
            <Footer />
        </main>
    );
}

