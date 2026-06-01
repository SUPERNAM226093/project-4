"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";

import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import { fetchPrescriptionsByPatient, PrescriptionResponse, AuthResponse } from "../lib/api";
import { HiOutlineDocumentText, HiOutlineCalendar, HiOutlineUser, HiOutlineClipboardDocumentList, HiOutlineChevronDown, HiOutlineChevronUp, HiOutlineInformationCircle } from "react-icons/hi2";

export default function MyPrescriptionsPage() {
    
    const router = useRouter();
    const [prescriptions, setPrescriptions] = useState<PrescriptionResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState<AuthResponse | null>(null);
    const [expandedId, setExpandedId] = useState<number | null>(null);

    useEffect(() => {
        const savedUser = localStorage.getItem("clinic_user");
        if (savedUser) {
            const parsedUser = JSON.parse(savedUser);
            setUser(parsedUser);
            loadPrescriptions(parsedUser.userId);
        } else {
            setLoading(false);
        }
    }, []);

    const loadPrescriptions = async (userId: number) => {
        try {
            const data = await fetchPrescriptionsByPatient(userId);
            setPrescriptions(data);
            if (data.length > 0) {
                setExpandedId(data[0].id); // Expand first by default
            }
        } catch (error) {
            console.error("Failed to load prescriptions:", error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50">
                <Navbar />
                <div className="flex items-center justify-center h-[60vh]">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#7C6EE6]"></div>
                </div>
                <Footer />
            </div>
        );
    }

    if (!user) {
        return (
            <div className="min-h-screen bg-gray-50">
                <Navbar />
                <div className="max-w-4xl mx-auto px-4 py-20 text-center">
                    <HiOutlineInformationCircle className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                    <h2 className="text-2xl font-bold text-gray-800 mb-2">Yêu cầu đăng nhập</h2>
                    <p className="text-gray-600 mb-6">Vui lòng đăng nhập để xem đơn thuốc của bạn.</p>
                    <button 
                        onClick={() => router.push("/")}
                        className="bg-[#7C6EE6] text-white px-8 py-3 rounded-xl font-bold hover:bg-[#6D5DD3] transition-all"
                    >
                        Quay lại trang chủ
                    </button>
                </div>
                <Footer />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <Navbar />
            
            <main className="max-w-5xl mx-auto px-4 py-12">
                <div className="flex items-center gap-4 mb-10">
                    <div className="w-14 h-14 rounded-2xl bg-[#7C6EE6]/10 flex items-center justify-center text-[#7C6EE6]">
                        <HiOutlineDocumentText className="w-8 h-8" />
                    </div>
                    <div>
                        <h1 className="text-3xl font-bold text-[#392E7B]">Đơn thuốc của tôi</h1>
                        <p className="text-gray-500 mt-1">Danh sách các đơn thuốc bác sĩ đã kê cho bạn</p>
                    </div>
                </div>

                {prescriptions.length === 0 ? (
                    <div className="bg-white rounded-3xl p-16 text-center shadow-sm border border-gray-100">
                        <div className="w-24 h-24 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-6">
                            <HiOutlineClipboardDocumentList className="w-12 h-12 text-gray-300" />
                        </div>
                        <h2 className="text-xl font-bold text-gray-800 mb-2">Bạn chưa có đơn thuốc nào</h2>
                        <p className="text-gray-500">Các đơn thuốc sau khi khám sẽ được hiển thị tại đây.</p>
                    </div>
                ) : (
                    <div className="space-y-6">
                        {prescriptions.map((p) => (
                            <div 
                                key={p.id} 
                                className="bg-white rounded-3xl shadow-sm border border-gray-100 overflow-hidden transition-all hover:shadow-md"
                            >
                                {/* Header */}
                                <div 
                                    className="p-6 cursor-pointer flex items-center justify-between"
                                    onClick={() => setExpandedId(expandedId === p.id ? null : p.id)}
                                >
                                    <div className="flex flex-wrap items-center gap-x-8 gap-y-3">
                                        <div className="flex items-center gap-2">
                                            <HiOutlineCalendar className="w-5 h-5 text-[#7C6EE6]" />
                                            <div>
                                                <p className="text-[10px] text-gray-400 uppercase font-bold tracking-wider leading-none">Ngày kê đơn</p>
                                                <p className="font-semibold text-gray-700">{new Date(p.createdAt).toLocaleDateString('vi-VN')}</p>
                                            </div>
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <HiOutlineUser className="w-5 h-5 text-[#7C6EE6]" />
                                            <div>
                                                <p className="text-[10px] text-gray-400 uppercase font-bold tracking-wider leading-none">Bác sĩ kê đơn</p>
                                                <p className="font-semibold text-gray-700">BS. {p.doctorName}</p>
                                            </div>
                                        </div>
                                        <div className="bg-[#7C6EE6]/5 px-4 py-1.5 rounded-full">
                                            <span className="text-[#7C6EE6] text-sm font-bold">Mã đơn: #DT{p.id.toString().padStart(5, '0')}</span>
                                        </div>
                                    </div>
                                    <div className="text-gray-400">
                                        {expandedId === p.id ? <HiOutlineChevronUp className="w-6 h-6" /> : <HiOutlineChevronDown className="w-6 h-6" />}
                                    </div>
                                </div>

                                {/* Body */}
                                {expandedId === p.id && (
                                    <div className="px-6 pb-8 animate-in fade-in slide-in-from-top-2 duration-300">
                                        <div className="border-t border-gray-100 pt-6">
                                            <div className="overflow-x-auto">
                                                <table className="w-full">
                                                    <thead>
                                                        <tr className="bg-gray-50 text-gray-500 text-xs font-bold uppercase tracking-wider">
                                                            <th className="px-4 py-3 text-left rounded-l-xl">Tên thuốc</th>
                                                            <th className="px-4 py-3 text-left">Liều dùng</th>
                                                            <th className="px-4 py-3 text-left">Tần suất</th>
                                                            <th className="px-4 py-3 text-left">Thời gian</th>
                                                            <th className="px-4 py-3 text-left rounded-r-xl">Ghi chú</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody className="divide-y divide-gray-50">
                                                        {p.items.map((item) => (
                                                            <tr key={item.id} className="text-sm text-gray-700 hover:bg-gray-50/50 transition-colors">
                                                                <td className="px-4 py-4 font-bold text-[#392E7B]">{item.medicineName}</td>
                                                                <td className="px-4 py-4">{item.dosage}</td>
                                                                <td className="px-4 py-4">{item.frequency}</td>
                                                                <td className="px-4 py-4 font-medium">{item.duration}</td>
                                                                <td className="px-4 py-4 text-gray-500 italic text-xs">{item.note || '-'}</td>
                                                            </tr>
                                                        ))}
                                                    </tbody>
                                                </table>
                                            </div>

                                            <div className="mt-8 flex justify-end">
                                                <button 
                                                    onClick={() => window.print()}
                                                    className="flex items-center gap-2 text-[#7C6EE6] font-bold hover:bg-[#7C6EE6]/5 px-6 py-2.5 rounded-xl transition-all border border-[#7C6EE6]/20"
                                                >
                                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                                                    </svg>
                                                    In đơn thuốc
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </main>

            <Footer />
        </div>
    );
}

