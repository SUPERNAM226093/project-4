"use client";

import { useEffect, useState, Suspense } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import Navbar from "../../../components/Navbar";
import Footer from "../../../components/Footer";
import { verifyVnPayCallback } from "../../../lib/api";

function CallbackContent() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const [status, setStatus] = useState<"loading" | "success" | "error">("loading");
    const [errorMsg, setErrorMsg] = useState("");

    useEffect(() => {
        const queryStr = window.location.search;
        if (!queryStr) {
            setStatus("error");
            setErrorMsg("Không tìm thấy thông tin giao dịch thanh toán.");
            return;
        }

        verifyVnPayCallback(queryStr)
            .then((res) => {
                if (res.success) {
                    setStatus("success");
                } else {
                    setStatus("error");
                    setErrorMsg("Xác thực giao dịch từ VNPay thất bại hoặc giao dịch bị hủy.");
                }
            })
            .catch((e) => {
                setStatus("error");
                setErrorMsg(e.message || "Đã xảy ra lỗi khi xác thực giao dịch.");
            });
    }, [searchParams]);

    if (status === "loading") {
        return (
            <div className="flex flex-col items-center justify-center space-y-4 py-12">
                <div className="w-16 h-16 border-4 border-emerald-200 border-t-emerald-500 rounded-full animate-spin" />
                <h2 className="text-xl font-bold text-gray-800">Đang kiểm tra kết quả thanh toán...</h2>
                <p className="text-sm text-gray-500">Vui lòng không đóng hoặc tải lại trang này.</p>
            </div>
        );
    }

    if (status === "success") {
        return (
            <div className="bg-white rounded-3xl shadow-sm border border-green-100 p-8 text-center max-w-md mx-auto space-y-6">
                <div className="w-20 h-20 bg-green-50 text-green-500 rounded-full flex items-center justify-center mx-auto shadow-inner border border-green-100 animate-bounce">
                    <svg className="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
                    </svg>
                </div>
                <div className="space-y-2">
                    <h2 className="text-2xl font-bold text-[var(--green-dark)]">Thanh toán thành công!</h2>
                    <p className="text-sm text-gray-500 leading-relaxed">
                        Hóa đơn của bạn đã được thanh toán qua cổng VNPay. Lịch hẹn tư vấn trực tuyến đã được kích hoạt.
                    </p>
                </div>
                <button
                    onClick={() => router.push("/lich-su-kham")}
                    className="w-full py-3.5 rounded-xl font-semibold text-sm text-white bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 shadow-lg shadow-green-200 transition-all hover:scale-[1.01]"
                >
                    📅 Xem lịch sử khám ngay
                </button>
            </div>
        );
    }

    return (
        <div className="bg-white rounded-3xl shadow-sm border border-red-100 p-8 text-center max-w-md mx-auto space-y-6">
            <div className="w-20 h-20 bg-red-50 text-red-500 rounded-full flex items-center justify-center mx-auto shadow-inner border border-red-100">
                <svg className="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
            </div>
            <div className="space-y-2">
                <h2 className="text-2xl font-bold text-red-600">Thanh toán thất bại</h2>
                <p className="text-sm text-gray-500 leading-relaxed">{errorMsg}</p>
            </div>
            <div className="flex flex-col gap-2">
                <button
                    onClick={() => router.push("/lich-su-kham")}
                    className="w-full py-3.5 rounded-xl font-semibold text-sm text-white bg-gradient-to-r from-[var(--green-mid)] to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 shadow-lg shadow-emerald-200 transition-all hover:scale-[1.01]"
                >
                    Quay lại lịch sử khám
                </button>
                <button
                    onClick={() => router.push("/")}
                    className="w-full py-2.5 rounded-xl text-sm font-medium text-gray-400 hover:text-gray-600 transition-colors"
                >
                    Về trang chủ
                </button>
            </div>
        </div>
    );
}

export default function VnPayCallbackPage() {
    return (
        <>
            <Navbar />
            <div className="min-h-screen bg-gradient-to-br from-[#e8f9f4] via-white to-[#f0f8ff] pt-36 pb-16 flex items-center justify-center">
                <div className="w-full max-w-lg px-4">
                    <Suspense fallback={
                        <div className="flex flex-col items-center justify-center space-y-4 py-12">
                            <div className="w-16 h-16 border-4 border-emerald-200 border-t-emerald-500 rounded-full animate-spin" />
                            <h2 className="text-xl font-bold text-gray-800">Đang chuẩn bị...</h2>
                        </div>
                    }>
                        <CallbackContent />
                    </Suspense>
                </div>
            </div>
            <Footer />
        </>
    );
}
