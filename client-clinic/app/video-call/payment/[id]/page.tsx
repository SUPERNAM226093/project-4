"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Navbar from "../../../components/Navbar";
import Footer from "../../../components/Footer";
import {
    fetchOnlineConsultationById,
    cancelOnlineConsultation,
    buildVietQRUrl,
    OnlineConsultationResponse,
    AuthResponse,
} from "../../../lib/api";

// ── Config (từ biến môi trường) ────────────────────────────────────────────────
const BANK_ID = process.env.NEXT_PUBLIC_BANK_ID || "Vietinbank";
const ACCOUNT_NO = process.env.NEXT_PUBLIC_BANK_ACCOUNT || "101885711760";
const ACCOUNT_NAME = process.env.NEXT_PUBLIC_BANK_OWNER || "PHONG KHAM MEDPRO";
const BANK_DISPLAY = process.env.NEXT_PUBLIC_BANK_DISPLAY || "Vietinbank";

function getLoggedInUser(): AuthResponse | null {
    if (typeof window === "undefined") return null;
    try { return JSON.parse(localStorage.getItem("clinic_user") || ""); } catch { return null; }
}

function formatCountdown(expiredAt: string): string {
    const diff = Math.max(0, new Date(expiredAt).getTime() - Date.now());
    const h = Math.floor(diff / 3_600_000);
    const m = Math.floor((diff % 3_600_000) / 60_000);
    const s = Math.floor((diff % 60_000) / 1_000);
    return `${String(h).padStart(2, "0")}:${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
}

export default function PaymentPage() {
    const params = useParams();
    const router = useRouter();
    const consultationId = Number(params.id);

    // --- CÁC TRẠNG THÁI (STATE) ---
    const [consultation, setConsultation] = useState<OnlineConsultationResponse | null>(null); // Thông tin đơn tư vấn
    const [loading, setLoading] = useState(true); // Trạng thái đang tải dữ liệu
    const [error, setError] = useState<string | null>(null); // Lỗi khi tải dữ liệu
    const [countdown, setCountdown] = useState(""); // Chuỗi đếm ngược thời gian thanh toán
    const [notified, setNotified] = useState(false); // Đã nhấn nút "Tôi đã chuyển khoản" chưa
    const [cancelling, setCancelling] = useState(false); // Đang xử lý hủy đơn
    const [qrError, setQrError] = useState(false); // Lỗi khi tải mã QR VietQR

    /**
     * HOOK: Lấy thông tin chi tiết đơn tư vấn khi vào trang
     */
    useEffect(() => {
        const user = getLoggedInUser();
        fetchOnlineConsultationById(consultationId, user?.userId)
            .then(data => {
                setConsultation(data);
                setLoading(false);
            })
            .catch(e => {
                setError(e.message);
                setLoading(false);
            });
    }, [consultationId]);

    // Countdown timer
    /**
     * HOOK: Chạy bộ đếm ngược (Countdown) dựa trên thời gian hết hạn (expiredAt)
     */
    useEffect(() => {
        if (!consultation?.expiredAt) return;
        const timer = setInterval(() => {
            setCountdown(formatCountdown(consultation.expiredAt));
        }, 1000);
        return () => clearInterval(timer);
    }, [consultation?.expiredAt]);

    /**
     * HÀM: Xử lý hủy đơn tư vấn
     */
    const handleCancel = async () => {
        if (!consultation) return;
        const user = getLoggedInUser();
        if (!user) return;
        if (!confirm("Bạn chắc chắn muốn hủy đơn này?")) return;
        setCancelling(true);
        try {
            await cancelOnlineConsultation(consultation.id, user.userId);
            router.push("/lich-su-kham");
        } catch (e: any) {
            alert(e.message);
            setCancelling(false);
        }
    };

    // Sinh URL mã QR thanh toán qua VietQR
    const qrUrl = consultation ? buildVietQRUrl({
        bankId: BANK_ID,
        accountNo: ACCOUNT_NO,
        accountName: ACCOUNT_NAME,
        amount: consultation.amount,
        orderId: consultation.id,
    }) : null;

    if (loading) return (
        <>
            <Navbar />
            <div className="min-h-screen flex items-center justify-center pt-36">
                <div className="text-center space-y-3">
                    <div className="w-12 h-12 border-4 border-blue-200 border-t-blue-500 rounded-full animate-spin mx-auto" />
                    <p className="text-sm text-gray-500">Đang tải thông tin thanh toán...</p>
                </div>
            </div>
            <Footer />
        </>
    );

    if (error || !consultation) return (
        <>
            <Navbar />
            <div className="min-h-screen flex items-center justify-center pt-36">
                <div className="bg-white rounded-3xl shadow p-10 text-center max-w-sm mx-auto">
                    <div className="w-14 h-14 bg-red-100 text-red-500 rounded-full flex items-center justify-center mx-auto mb-4">
                        <svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    </div>
                    <p className="font-semibold text-gray-800 mb-1">Không thể tải thông tin</p>
                    <p className="text-sm text-gray-500 mb-4">{error}</p>
                    <button onClick={() => router.push("/")} className="text-blue-500 text-sm hover:underline">← Về trang chủ</button>
                </div>
            </div>
            <Footer />
        </>
    );

    const isPaid = consultation.paymentStatus === "PAID";
    const isCancelled = consultation.paymentStatus === "CANCELLED";

    return (
        <>
            <Navbar />
            <div className="min-h-screen bg-gradient-to-br from-[#eaf4ff] via-white to-[#f0f8ff] pt-36 pb-16">
                <div className="max-w-2xl mx-auto px-4 space-y-4">

                    {/* Step indicator - Containerized */}
                    <div className="bg-white rounded-3xl shadow-sm border border-gray-100 overflow-hidden">
                        <div className="flex items-center justify-center gap-2 py-6 bg-gray-50/50 border-b border-gray-50">
                            {["Thông tin", "Thanh toán", "Tư vấn"].map((step, i) => (
                                <div key={i} className="flex items-center gap-2">
                                    <div className={`flex items-center justify-center w-8 h-8 rounded-full text-sm font-bold
                                        ${i === 1 ? "bg-[#1a8fe3] text-white shadow-md shadow-blue-200"
                                            : i < 1 ? "bg-green-400 text-white" : "bg-gray-100 text-gray-400"}`}>
                                        {i < 1 ? "✓" : i + 1}
                                    </div>
                                    <span className={`text-xs font-medium ${i === 1 ? "text-[#1a8fe3]" : i < 1 ? "text-green-500" : "text-gray-400"}`}>{step}</span>
                                    {i < 2 && <div className="w-8 h-px bg-gray-200" />}
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Status banners */}
                    {isPaid && (
                        <div className="bg-green-50 border border-green-200 rounded-2xl px-5 py-4 flex items-center gap-3">
                            <span className="text-2xl">✅</span>
                            <div>
                                <p className="font-semibold text-green-700 text-sm">Đã xác nhận thanh toán</p>
                                {consultation.meetingLink
                                    ? <p className="text-xs text-green-600">Phòng họp đã sẵn sàng. Bấm "Vào phòng họp" để bắt đầu.</p>
                                    : <p className="text-xs text-green-600">Nhân viên đang chuẩn bị phòng họp, vui lòng đợi.</p>}
                            </div>
                        </div>
                    )}
                    {isCancelled && (
                        <div className="bg-red-50 border border-red-200 rounded-2xl px-5 py-4 flex items-center gap-3">
                            <span className="text-2xl">❌</span>
                            <p className="font-semibold text-red-600 text-sm">Đơn tư vấn này đã bị hủy</p>
                        </div>
                    )}

                    {/* Order summary */}
                    <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6">
                        <h2 className="text-sm font-bold text-[#0d2d6b] mb-4 flex items-center gap-2">
                            <svg className="w-4 h-4 text-[#1a8fe3]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                            </svg>
                            Thông tin đơn tư vấn #{consultation.id}
                        </h2>
                        <div className="grid grid-cols-2 gap-3 text-sm">
                            <div>
                                <p className="text-gray-400 text-xs mb-0.5">Bác sĩ</p>
                                <p className="font-semibold text-[#0d2d6b]">{consultation.doctorName}</p>
                            </div>
                            <div>
                                <p className="text-gray-400 text-xs mb-0.5">Chuyên khoa</p>
                                <p className="font-semibold text-[#0d2d6b]">{consultation.specializationName || "—"}</p>
                            </div>
                            <div>
                                <p className="text-gray-400 text-xs mb-0.5">Dịch vụ</p>
                                <p className="font-semibold text-[#0d2d6b]">{consultation.serviceName || "—"}</p>
                            </div>
                            <div>
                                <p className="text-gray-400 text-xs mb-0.5">SĐT liên hệ</p>
                                <p className="font-semibold text-[#0d2d6b]">{consultation.phoneNumber}</p>
                            </div>
                        </div>
                        <div className="mt-4 flex items-center justify-between bg-blue-50 rounded-xl px-4 py-3">
                            <span className="text-sm text-gray-600">Số tiền cần thanh toán</span>
                            <span className="text-xl font-bold text-[#f26522]">
                                {new Intl.NumberFormat("vi-VN").format(consultation.amount)}đ
                            </span>
                        </div>
                    </div>

                    {/* QR Code */}
                    {!isPaid && !isCancelled && (
                        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-sm font-bold text-[#0d2d6b] flex items-center gap-2">
                                    📱 Quét mã QR để thanh toán
                                </h2>
                                {countdown && (
                                    <div className="flex items-center gap-1.5 text-xs text-amber-600 bg-amber-50 border border-amber-100 rounded-lg px-3 py-1.5">
                                        <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                        </svg>
                                        Hết hạn sau {countdown}
                                    </div>
                                )}
                            </div>

                            <div className="flex flex-col items-center">
                                {!qrError && qrUrl ? (
                                    <div className="p-3 bg-white border-2 border-blue-100 rounded-2xl shadow-inner">
                                        <img
                                            src={qrUrl}
                                            alt="VietQR Payment Code"
                                            className="w-56 h-56 object-contain"
                                            onError={() => setQrError(true)}
                                        />
                                    </div>
                                ) : (
                                    <div className="w-56 h-56 bg-gray-50 rounded-2xl border-2 border-dashed border-gray-200 flex flex-col items-center justify-center text-gray-400 gap-2">
                                        <svg className="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                                        </svg>
                                        <p className="text-xs text-center px-4">Không tải được mã QR. Vui lòng dùng thông tin bên dưới.</p>
                                    </div>
                                )}

                                {/* Bank Info */}
                                <div className="mt-5 w-full bg-gray-50 rounded-2xl p-4 space-y-2 text-sm">
                                    <div className="flex justify-between">
                                        <span className="text-gray-400">Ngân hàng</span>
                                        <span className="font-semibold text-[#0d2d6b]">{BANK_DISPLAY}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-400">Số tài khoản</span>
                                        <span className="font-mono font-bold text-[#0d2d6b]">{ACCOUNT_NO}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-400">Chủ tài khoản</span>
                                        <span className="font-semibold text-[#0d2d6b]">{ACCOUNT_NAME}</span>
                                    </div>
                                    <div className="pt-2 border-t border-gray-200">
                                        <div className="flex justify-between items-start gap-2">
                                            <span className="text-gray-400 shrink-0">Nội dung CK</span>
                                            <span className="font-bold text-[#1a8fe3] text-right">
                                                THANH TOAN ONLINE {consultation.id}
                                            </span>
                                        </div>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-400">Số tiền</span>
                                        <span className="font-bold text-[#f26522]">
                                            {new Intl.NumberFormat("vi-VN").format(consultation.amount)}đ
                                        </span>
                                    </div>
                                </div>
                            </div>

                            {/* Notify button */}
                            {!notified ? (
                                <button
                                    onClick={() => setNotified(true)}
                                    className="mt-5 w-full py-3.5 rounded-xl font-semibold text-sm text-white bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 shadow-lg shadow-green-200 transition-all"
                                >
                                    ✅ Tôi đã chuyển khoản
                                </button>
                            ) : (
                                <div className="mt-5 bg-green-50 border border-green-200 rounded-xl px-5 py-4 text-center">
                                    <p className="font-semibold text-green-700 text-sm">⏳ Đang chờ nhân viên xác nhận</p>
                                    <p className="text-xs text-green-600 mt-1">Nhân viên sẽ xác nhận trong vài phút. Bạn có thể xem trạng thái trong lịch sử khám.</p>
                                </div>
                            )}

                            {/* Cancel */}
                            <button
                                onClick={handleCancel}
                                disabled={cancelling}
                                className="mt-3 w-full py-2.5 rounded-xl text-sm text-red-400 border border-red-100 hover:bg-red-50 transition-colors disabled:opacity-50"
                            >
                                {cancelling ? "Đang hủy..." : "Hủy đơn này"}
                            </button>
                        </div>
                    )}

                    {/* Meeting link for paid orders */}
                    {isPaid && consultation.meetingLink && (
                        <div className="bg-white rounded-3xl shadow-sm border border-green-100 p-6 text-center">
                            <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
                                <svg className="w-8 h-8 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 10l4.553-2.069A1 1 0 0121 8.82v6.362a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" />
                                </svg>
                            </div>
                            <h3 className="font-bold text-[#0d2d6b] mb-1">Phòng họp đã sẵn sàng!</h3>
                            <p className="text-xs text-gray-500 mb-4">Bấm nút bên dưới để bắt đầu tư vấn với bác sĩ</p>
                            <a
                                href={consultation.meetingLink}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="inline-block w-full py-3.5 rounded-xl font-semibold text-sm text-white bg-gradient-to-r from-[#1a8fe3] to-[#0d6cbf] shadow-lg shadow-blue-200 transition-all hover:shadow-xl"
                            >
                                🎥 Vào phòng họp ngay
                            </a>
                        </div>
                    )}

                    <button onClick={() => router.push("/lich-su-kham")} className="w-full text-center text-sm text-gray-400 hover:text-gray-600 py-2">
                        Xem lịch sử khám →
                    </button>
                </div>
            </div>
            <Footer />
        </>
    );
}
