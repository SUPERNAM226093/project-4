"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import {
    fetchAppointmentsByPatient,
    fetchHealthPackageBookingsByPatient,
    fetchOnlineConsultationsByPatient,
    cancelHealthPackageBooking,
    cancelOnlineConsultation,
    AppointmentResponse,
    HealthPackageBookingResponse,
    OnlineConsultationResponse,
    AuthResponse,
} from "../lib/api";
import RoomBookingHistory from "../components/RoomBookingHistory";

import { toast } from "react-toastify";

function getLoggedInUser(): AuthResponse | null {
    if (typeof window === "undefined") return null;
    try {
        const saved = localStorage.getItem("clinic_user");
        if (!saved) return null;
        return JSON.parse(saved) as AuthResponse;
    } catch {
        return null;
    }
}

function formatDate(dateStr: string) {
    const d = new Date(dateStr);
    return d.toLocaleDateString("vi-VN", { day: "2-digit", month: "2-digit", year: "numeric" });
}

function formatTime(timeStr: string) {
    return timeStr?.substring(0, 5) || "";
}

function formatDateTime(dateStr: string) {
    const d = new Date(dateStr);
    return d.toLocaleDateString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
}

export default function AppointmentHistoryPage() {
    
    const router = useRouter();
    const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
    const [pkgBookings, setPkgBookings] = useState<HealthPackageBookingResponse[]>([]);
    const [onlineConsultations, setOnlineConsultations] = useState<OnlineConsultationResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [user, setUser] = useState<AuthResponse | null>(null);
    const [filterStatus, setFilterStatus] = useState<string>("ALL");

    const STATUS_MAP: Record<string, { label: string; color: string; bg: string; icon: string }> = {
        PENDING: { label: "Chờ xác nhận", color: "text-amber-700", bg: "bg-amber-50 border-amber-200", icon: "⏳" },
        CONFIRMED: { label: "Đã xác nhận", color: "text-[#0065FF]", bg: "bg-[#E6EFFF] border-[#B3D4FF]", icon: "✓" },
        COMPLETED: { label: "Hoàn thành", color: "text-green-700", bg: "bg-green-50 border-green-200", icon: "✔" },
        CANCELLED: { label: "Đã hủy", color: "text-red-700", bg: "bg-red-50 border-red-200", icon: "✗" },
    };

    const handleCancelAppointment = async (id: number) => {
        const reason = window.prompt("Nhập lý do hủy lịch (không bắt buộc):");
        if (reason === null) return; // User cancelled prompt
        
        if (!user) return;
        try {
            await import("../lib/api").then(api => api.cancelAppointment(id, user.userId, reason));
            toast.success("Hủy lịch khám thành công!");
            // Update local state
            setAppointments(prev => prev.map(a => 
                a.id === id ? { ...a, status: "CANCELLED", note: (a.note ? a.note + " | " : "") + "Lý do hủy: " + reason } : a
            ));
        } catch (err: any) {
            toast.error(err.message || "Lỗi khi hủy lịch khám");
        }
    };

    useEffect(() => {
        const loggedUser = getLoggedInUser();
        if (!loggedUser) {
            setError("Vui lòng đăng nhập để xem lịch sử khám.");
            setLoading(false);
            return;
        }
        setUser(loggedUser);
        Promise.all([
            fetchAppointmentsByPatient(loggedUser.userId),
            fetchHealthPackageBookingsByPatient(loggedUser.userId),
            fetchOnlineConsultationsByPatient(loggedUser.userId),
        ])
            .then(([apts, pkgs, consults]) => {
                const sorted = [...apts].sort(
                    (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
                );
                setAppointments(sorted);
                setPkgBookings(pkgs);
                setOnlineConsultations(consults);
                setLoading(false);
            })
            .catch(() => {
                setError("Không thể tải lịch sử khám. Vui lòng thử lại.");
                setLoading(false);
            });
    }, []);

    const filteredAppointments =
        filterStatus === "ALL"
            ? appointments
            : appointments.filter((a) => a.status === filterStatus);

    const statusCounts = appointments.reduce<Record<string, number>>((acc, a) => {
        acc[a.status] = (acc[a.status] || 0) + 1;
        return acc;
    }, {});

    return (
        <>
            <Navbar />
            <div className="min-h-screen bg-[var(--background)] pt-36">
                <div className="max-w-5xl mx-auto px-4 py-8">
                    {/* Header */}
                    <div className="mb-8">
                        <button
                            onClick={() => router.back()}
                            className="flex items-center gap-2 text-sm text-gray-500 hover:text-[#2e5bff] mb-4 transition-colors"
                        >
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                            </svg>
                            {"Quay lại"}
                        </button>

                        <div className="bg-gradient-to-r from-[#2e5bff] to-[#1d4ed8] rounded-2xl p-6 text-white relative overflow-hidden">
                            <div className="absolute -top-12 -right-12 w-40 h-40 bg-white/10 rounded-full" />
                            <div className="absolute -bottom-8 -left-8 w-28 h-28 bg-white/5 rounded-full" />
                            <div className="relative z-10">
                                <div className="flex items-center gap-3 mb-2">
                                    <div className="w-10 h-10 rounded-xl bg-white/20 flex items-center justify-center">
                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                        </svg>
                                    </div>
                                    <h1 className="text-2xl font-bold">{"Lịch sử khám bệnh"}</h1>
                                </div>
                                {user && (
                                    <p className="text-white/80 text-sm ml-[52px]">
                                        {"Xin chào,"} <span className="font-semibold text-white">{user.fullName || user.email}</span>
                                    </p>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Loading */}
                    {loading && (
                        <div className="space-y-4">
                            {[1, 2, 3, 4].map((i) => (
                                <div key={i} className="bg-white rounded-2xl p-6 shadow-sm animate-pulse">
                                    <div className="flex items-center gap-4">
                                        <div className="w-12 h-12 bg-gray-200 rounded-xl" />
                                        <div className="space-y-2 flex-1">
                                            <div className="h-4 w-40 bg-gray-200 rounded" />
                                            <div className="h-3 w-64 bg-gray-200 rounded" />
                                        </div>
                                        <div className="h-6 w-24 bg-gray-200 rounded-full" />
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}

                    {/* Error */}
                    {error && !loading && (
                        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center">
                            <div className="w-20 h-20 mx-auto mb-4 rounded-full bg-red-50 flex items-center justify-center">
                                <svg className="w-10 h-10 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z" />
                                </svg>
                            </div>
                            <p className="text-red-600 font-medium mb-2">{error}</p>
                            <p className="text-sm text-gray-400">{"Vui lòng đăng nhập và thử lại"}</p>
                        </div>
                    )}

                    {/* Content */}
                    {!loading && !error && (
                        <>
                            {/* Stats */}
                            {appointments.length > 0 && (
                                <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-6">
                                    <div className="bg-white rounded-xl p-4 border border-gray-100 shadow-sm">
                                        <p className="text-2xl font-bold text-[#000000]">{appointments.length}</p>
                                        <p className="text-xs text-gray-400">{"Tổng lịch hẹn"}</p>
                                    </div>
                                    <div className="bg-white rounded-xl p-4 border border-gray-100 shadow-sm">
                                        <p className="text-2xl font-bold text-green-600">{statusCounts["COMPLETED"] || 0}</p>
                                        <p className="text-xs text-gray-400">{"Hoàn thành"}</p>
                                    </div>
                                    <div className="bg-white rounded-xl p-4 border border-gray-100 shadow-sm">
                                        <p className="text-2xl font-bold text-amber-600">{statusCounts["PENDING"] || 0}</p>
                                        <p className="text-xs text-gray-400">{"Chờ xác nhận"}</p>
                                    </div>
                                    <div className="bg-white rounded-xl p-4 border border-gray-100 shadow-sm">
                                        <p className="text-2xl font-bold text-red-600">{statusCounts["CANCELLED"] || 0}</p>
                                        <p className="text-xs text-gray-400">{"Đã hủy"}</p>
                                    </div>
                                </div>
                            )}

                            {/* Filter */}
                            {appointments.length > 0 && (
                                <div className="flex flex-wrap gap-2 mb-6">
                                    {[
                                        { key: "ALL", label: "Tất cả" },
                                        { key: "PENDING", label: "Chờ xác nhận" },
                                        { key: "CONFIRMED", label: "Đã xác nhận" },
                                        { key: "COMPLETED", label: "Hoàn thành" },
                                        { key: "CANCELLED", label: "Đã hủy" },
                                    ].map((f) => (
                                        <button
                                            key={f.key}
                                            onClick={() => setFilterStatus(f.key)}
                                            className={`px-4 py-2 rounded-full text-xs font-semibold transition-all ${filterStatus === f.key
                                                ? "bg-[#2e5bff] text-white shadow-md shadow-[#2e5bff]/20"
                                                : "bg-white text-gray-600 border border-gray-200 hover:border-[#2e5bff]/40 hover:text-[#2e5bff]"
                                                }`}
                                        >
                                            {f.label}
                                            {f.key !== "ALL" && statusCounts[f.key] ? ` (${statusCounts[f.key]})` : ""}
                                        </button>
                                    ))}
                                </div>
                            )}

                            {/* Empty */}
                            {appointments.length === 0 && (
                                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center">
                                    <div className="w-20 h-20 mx-auto mb-4 rounded-full bg-[#f0f7ff] flex items-center justify-center">
                                        <svg className="w-10 h-10 text-[#2e5bff]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                        </svg>
                                    </div>
                                    <p className="text-[#000000] font-semibold mb-1">{"Chưa có lịch hẹn nào"}</p>
                                    <p className="text-sm text-gray-400">{"Đặt lịch khám để bắt đầu sử dụng dịch vụ"}</p>
                                </div>
                            )}

                            {/* Filtered Empty */}
                            {appointments.length > 0 && filteredAppointments.length === 0 && (
                                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 text-center">
                                    <p className="text-gray-400 text-sm">{"Không có lịch hẹn nào với trạng thái này"}</p>
                                </div>
                            )}

                            {/* Appointment List */}
                            {filteredAppointments.length > 0 && (
                                <div className="space-y-3">
                                    {filteredAppointments.map((apt) => {
                                        const status = STATUS_MAP[apt.status] || STATUS_MAP.PENDING;
                                        return (
                                            <div
                                                key={apt.id}
                                                className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow"
                                            >
                                                <div className="flex flex-col md:flex-row md:items-center gap-4">
                                                    {/* Date box */}
                                                    <div className="flex-shrink-0 w-16 h-16 rounded-xl bg-gradient-to-br from-[#2e5bff] to-[#1d4ed8] text-white flex flex-col items-center justify-center">
                                                        <span className="text-lg font-bold leading-none">
                                                            {new Date(apt.appointmentDate).getDate()}
                                                        </span>
                                                        <span className="text-[10px] uppercase tracking-wider opacity-80">
                                                            {"Tháng"} {new Date(apt.appointmentDate).getMonth() + 1}
                                                        </span>
                                                    </div>

                                                    {/* Info */}
                                                    <div className="flex-1 min-w-0">
                                                        <div className="flex items-center gap-2 mb-1.5">
                                                            <p className="font-semibold text-[#000000] text-sm">
                                                                {"Khám với BS."} {apt.doctorName}
                                                            </p>
                                                        </div>
                                                        <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-gray-500">
                                                            <span className="flex items-center gap-1">
                                                                <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                                                </svg>
                                                                {formatDate(apt.appointmentDate)} • {formatTime(apt.appointmentTime)}
                                                            </span>
                                                            {apt.serviceName && (
                                                                <span className="flex items-center gap-1">
                                                                    <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                                                                    </svg>
                                                                    {apt.serviceName}
                                                                </span>
                                                            )}
                                                            {apt.healthPackageName && (
                                                                <span className="flex items-center gap-1">
                                                                    📦 {apt.healthPackageName}
                                                                </span>
                                                            )}
                                                        </div>
                                                        {apt.note && (
                                                            <p className="text-xs text-gray-400 mt-1.5 line-clamp-1">
                                                                💬 {apt.note}
                                                            </p>
                                                        )}
                                                    </div>

                                                    {/* Status + Meta */}
                                                    <div className="flex flex-col items-end gap-1.5">
                                                        <span className={`inline-flex items-center gap-1 px-3 py-1.5 rounded-full text-xs font-semibold border ${status.bg} ${status.color}`}>
                                                            <span>{status.icon}</span>
                                                            {status.label}
                                                        </span>
                                                        <span className="text-[10px] text-gray-400">
                                                            {"Tạo:"} {formatDateTime(apt.createdAt)}
                                                        </span>
                                                        {apt.status !== "CANCELLED" && apt.status !== "COMPLETED" && (
                                                            <button
                                                                onClick={() => handleCancelAppointment(apt.id)}
                                                                className="mt-2 text-xs text-red-500 border border-red-200 hover:bg-red-50 px-3 py-1 rounded-full transition-all"
                                                            >
                                                                Hủy lịch
                                                            </button>
                                                        )}
                                                    </div>
                                                </div>
                                            </div>
                                        );
                                    })}
                                </div>
                            )}

                            {user && <RoomBookingHistory userId={user.userId} />}

                            {/* Health Package Bookings Section */}
                            {pkgBookings.length > 0 && (
                                <div className="mt-8">
                                    <h2 className="text-lg font-bold text-[#000000] mb-4 flex items-center gap-2">
                                        <span className="text-xl">📦</span>
                                        Lịch sử đặt gói khám sức khỏe
                                    </h2>
                                    <div className="space-y-3">
                                        {pkgBookings.map((b) => {
                                            const status = STATUS_MAP[b.status] || STATUS_MAP.PENDING;
                                            return (
                                                <div key={b.id} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
                                                    <div className="flex flex-col md:flex-row md:items-center gap-4">
                                                        <div className="flex-shrink-0 w-16 h-16 rounded-xl bg-gradient-to-br from-[#0065FF] to-[#e05a1a] text-white flex flex-col items-center justify-center">
                                                            <span className="text-lg font-bold leading-none">
                                                                {new Date(b.bookingDate).getDate()}
                                                            </span>
                                                            <span className="text-[10px] uppercase tracking-wider opacity-80">
                                                                Tháng {new Date(b.bookingDate).getMonth() + 1}
                                                            </span>
                                                        </div>
                                                        <div className="flex-1 min-w-0">
                                                            <p className="font-semibold text-[#000000] text-sm mb-1">{b.healthPackageName}</p>
                                                            <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-gray-500">
                                                                <span className="flex items-center gap-1">
                                                                    <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                                                    </svg>
                                                                    {new Date(b.bookingDate).toLocaleDateString("vi-VN")} • {b.bookingTime?.substring(0, 5)}
                                                                </span>
                                                                {b.packagePrice && (
                                                                    <span className="text-[#2e5bff] font-semibold">
                                                                        {new Intl.NumberFormat("vi-VN").format(b.packagePrice)}đ
                                                                    </span>
                                                                )}
                                                            </div>
                                                            {b.note && <p className="text-xs text-gray-400 mt-1 line-clamp-1">💬 {b.note}</p>}
                                                        </div>
                                                        <div className="flex flex-col items-end gap-1.5">
                                                            <span className={`inline-flex items-center gap-1 px-3 py-1.5 rounded-full text-xs font-semibold border ${status.bg} ${status.color}`}>
                                                                <span>{status.icon}</span>{status.label}
                                                            </span>
                                                            <span className="text-[10px] text-gray-400">
                                                                {new Date(b.createdAt).toLocaleDateString("vi-VN", { day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit" })}
                                                            </span>
                                                            {b.status !== "CANCELLED" && b.status !== "COMPLETED" && (
                                                                <button
                                                                    onClick={async () => {
                                                                        const reason = window.prompt("Nhập lý do hủy:");
                                                                        if (reason === null || !user) return;
                                                                        try {
                                                                            await cancelHealthPackageBooking(b.id, user.userId, reason);
                                                                            toast.success("Hủy lịch gói khám thành công!");
                                                                            setPkgBookings(prev => prev.map(p => p.id === b.id ? { ...p, status: "CANCELLED" } : p));
                                                                        } catch { toast.error("Lỗi khi hủy lịch"); }
                                                                    }}
                                                                    className="mt-2 text-xs text-red-500 border border-red-200 hover:bg-red-50 px-3 py-1 rounded-full transition-all"
                                                                >
                                                                    Hủy lịch
                                                                </button>
                                                            )}
                                                        </div>
                                                    </div>
                                                </div>
                                            );
                                        })}
                                    </div>
                                </div>
                            )}

                            {/* Online Consultations Section */}
                            {onlineConsultations.length > 0 && (
                                <div className="mt-8">
                                    <h2 className="text-lg font-bold text-[#0d2d6b] mb-4 flex items-center gap-2">
                                        <span className="text-xl">🎥</span>
                                        Lịch sử tư vấn Video Online
                                    </h2>
                                    <div className="space-y-3">
                                        {onlineConsultations.map((c) => {
                                            const statusMap: Record<string, { label: string; color: string; bg: string; icon: string }> = {
                                                PENDING: { label: "Chờ xác nhận", color: "text-amber-700", bg: "bg-amber-50 border-amber-200", icon: "⏳" },
                                                PAID:    { label: "Đã thanh toán", color: "text-blue-700", bg: "bg-blue-50 border-blue-200", icon: "✅" },
                                                CANCELLED: { label: "Đã hủy", color: "text-red-700", bg: "bg-red-50 border-red-200", icon: "✗" },
                                            };
                                            const st = statusMap[c.paymentStatus] || statusMap.PENDING;
                                            return (
                                                <div key={c.id} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
                                                    <div className="flex flex-col md:flex-row md:items-center gap-4">
                                                        <div className="flex-shrink-0 w-16 h-16 rounded-xl bg-gradient-to-br from-[#1a8fe3] to-[#0d6cbf] text-white flex flex-col items-center justify-center">
                                                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 10l4.553-2.069A1 1 0 0121 8.82v6.362a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" />
                                                            </svg>
                                                            <span className="text-[9px] mt-1 opacity-80">Online</span>
                                                        </div>
                                                        <div className="flex-1 min-w-0">
                                                            <p className="font-semibold text-[#0d2d6b] text-sm mb-1">{c.doctorName}</p>
                                                            <div className="flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-gray-500">
                                                                {c.consultationDate && (
                                                                    <span className="font-bold text-blue-600">
                                                                        🗓️ {new Date(c.consultationDate).toLocaleDateString("vi-VN")} • {c.consultationTime}
                                                                    </span>
                                                                )}
                                                                {c.specializationName && <span>🏥 {c.specializationName}</span>}
                                                                {c.serviceName && <span>🩺 {c.serviceName}</span>}
                                                                <span>📞 {c.phoneNumber}</span>
                                                                <span className="text-[#2e5bff] font-semibold">
                                                                    {new Intl.NumberFormat("vi-VN").format(c.amount)}đ
                                                                </span>
                                                            </div>
                                                            <p className="text-[10px] text-gray-400 mt-1">
                                                                Đặt lúc: {new Date(c.createdAt).toLocaleDateString("vi-VN", { day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit" })}
                                                            </p>
                                                        </div>
                                                        <div className="flex flex-col items-end gap-2">
                                                            <span className={`inline-flex items-center gap-1 px-3 py-1.5 rounded-full text-xs font-semibold border ${st.bg} ${st.color}`}>
                                                                <span>{st.icon}</span>{st.label}
                                                            </span>
                                                            {/* Meeting Link Button */}
                                                            {c.paymentStatus === "PAID" && c.meetingLink && (
                                                                <a
                                                                    href={c.meetingLink}
                                                                    target="_blank"
                                                                    rel="noopener noreferrer"
                                                                    className="inline-flex items-center gap-1 px-3 py-1.5 rounded-full text-xs font-semibold bg-[#2e5bff] text-white hover:bg-[#1d4ed8] transition-colors shadow-md"
                                                                >
                                                                    🎥 Vào phòng họp
                                                                </a>
                                                            )}
                                                            {/* Cancel button for pending */}
                                                            {c.paymentStatus === "PENDING" && user && (
                                                                <button
                                                                    onClick={async () => {
                                                                        if (!confirm("Hủy đơn tư vấn này?")) return;
                                                                        try {
                                                                            await cancelOnlineConsultation(c.id, user.userId);
                                                                            toast.success("Đã hủy đơn tư vấn!");
                                                                            setOnlineConsultations(prev => prev.map(x => x.id === c.id ? { ...x, paymentStatus: "CANCELLED" as const } : x));
                                                                        } catch { toast.error("Lỗi khi hủy đơn"); }
                                                                    }}
                                                                    className="text-xs text-red-400 border border-red-100 hover:bg-red-50 px-3 py-1 rounded-full transition-all"
                                                                >
                                                                    Hủy đơn
                                                                </button>
                                                            )}
                                                            {/* View payment page */}
                                                            {c.paymentStatus === "PENDING" && (
                                                                <button
                                                                    onClick={() => router.push(`/video-call/payment/${c.id}`)}
                                                                    className="text-xs text-[#1a8fe3] border border-blue-100 hover:bg-blue-50 px-3 py-1 rounded-full transition-all"
                                                                >
                                                                    Xem QR thanh toán
                                                                </button>
                                                            )}
                                                        </div>
                                                    </div>
                                                </div>
                                            );
                                        })}
                                    </div>
                                </div>
                            )}
                        </>
                    )}
                </div>
            </div>
            <Footer />
        </>
    );
}

