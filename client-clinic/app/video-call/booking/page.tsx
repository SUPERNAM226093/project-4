"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Navbar from "../../components/Navbar";
import Footer from "../../components/Footer";
import {
    fetchDoctors,
    fetchSpecializations,
    fetchClinicServices,
    createOnlineConsultation,
    DoctorResponse,
    SpecializationResponse,
    AuthResponse,
    fetchOnlineConsultationsByPatient,
    fetchBookedOnlineConsultationSlots,
    ClinicServiceResponse,
} from "../../lib/api";
import { getTodayStr, isPastTime } from "../../lib/dateUtils";

function getLoggedInUser(): AuthResponse | null {
    if (typeof window === "undefined") return null;
    try {
        const saved = localStorage.getItem("clinic_user");
        return saved ? (JSON.parse(saved) as AuthResponse) : null;
    } catch { return null; }
}

const PHONE_REGEX = /^[0-9]{10}$/;

export default function VideoCallBookingPage() {
    const router = useRouter();
    const [user, setUser] = useState<AuthResponse | null>(null);
    const [specializations, setSpecializations] = useState<SpecializationResponse[]>([]); // Danh sách chuyên khoa
    const [doctors, setDoctors] = useState<DoctorResponse[]>([]); // Danh sách bác sĩ
    const [services, setServices] = useState<ClinicServiceResponse[]>([]); // Danh sách dịch vụ

    const [selectedSpec, setSelectedSpec] = useState<SpecializationResponse | null>(null); // Chuyên khoa đã chọn
    const [selectedDoctor, setSelectedDoctor] = useState<DoctorResponse | null>(null); // Bác sĩ đã chọn
    const [selectedService, setSelectedService] = useState<ClinicServiceResponse | null>(null); // Dịch vụ (Được lấy động từ API)
    const [phoneNumber, setPhoneNumber] = useState(""); // Số điện thoại liên hệ
    const [phoneError, setPhoneError] = useState(""); // Lỗi định dạng điện thoại

    const [consultationDate, setConsultationDate] = useState(getTodayStr()); // Ngày tư vấn mong muốn
    const [consultationTime, setConsultationTime] = useState(""); // Khung giờ tư vấn mong muốn
    const [bookedSlots, setBookedSlots] = useState<string[]>([]);

    const [showServiceModal, setShowServiceModal] = useState(false);
    const [serviceSearch, setServiceSearch] = useState("");

    const [submitting, setSubmitting] = useState(false); // Trạng thái đang gửi form
    const [error, setError] = useState<string | null>(null); // Lỗi từ server hoặc validation

    /**
     * HOOK: Chạy một lần khi mount component
     * MÔ TẢ: Lấy danh sách chuyên khoa, bác sĩ và lọc dịch vụ "Khám online"
     */
    useEffect(() => {
        const u = getLoggedInUser();
        setUser(u);
        
        fetchSpecializations().then(setSpecializations).catch(console.error);
        fetchDoctors().then(setDoctors).catch(console.error);
        fetchClinicServices().then(svcs => {
            const onlineSvc = svcs.filter(s => 
                s.name.toLowerCase().includes("khám onl") || 
                s.name.toLowerCase().includes("tư vấn onl")
            );
            if (onlineSvc.length > 0) {
                setServices(onlineSvc);
                setSelectedService(onlineSvc[0]);
            }
        }).catch(console.error);
    }, []);

    useEffect(() => {
        if (!selectedDoctor || !consultationDate) return;
        fetchBookedOnlineConsultationSlots(selectedDoctor.id, consultationDate)
            .then(slots => setBookedSlots(slots))
            .catch(() => setBookedSlots([]));
    }, [selectedDoctor, consultationDate]);

    // Lọc danh sách bác sĩ theo chuyên khoa đã chọn
    const filteredDoctors = selectedSpec
        ? doctors.filter(d => d.specializationName === selectedSpec.name)
        : doctors;

    // Lọc dịch vụ theo ô tìm kiếm (nếu có)
    const filteredServices = services.filter(s =>
        s.name.toLowerCase().includes(serviceSearch.toLowerCase())
    );

    /**
     * HÀM: Validate số điện thoại (10 chữ số)
     */
    const validatePhone = (val: string) => {
        if (!val.trim()) { setPhoneError("Vui lòng nhập số điện thoại"); return false; }
        if (!PHONE_REGEX.test(val)) { setPhoneError("Số điện thoại không hợp lệ (VD: 0912345678)"); return false; }
        setPhoneError("");
        return true;
    };

    // Điều kiện để nút "Tiếp tục" được kích hoạt
    const canProceed = selectedDoctor && selectedService && phoneNumber && !phoneError && consultationDate && consultationTime;

    /**
     * HÀM: Xử lý khi nhấn nút Đặt lịch
     * LUỒNG: Validate -> Gửi API -> Chuyển sang trang thanh toán
     */
    const handleSubmit = async () => {
        if (!validatePhone(phoneNumber)) return;
        
        if (!user) {
            setError("Vui lòng đăng nhập để đặt lịch tư vấn");
            return;
        }
        
        if (!selectedDoctor) {
            setError("Vui lòng chọn bác sĩ");
            return;
        }
        
        if (!selectedService) {
            setError("Vui lòng chọn dịch vụ");
            return;
        }

        setSubmitting(true);
        setError(null);
        try {

            const result = await createOnlineConsultation({
                patientId: user.userId,
                doctorId: selectedDoctor!.id,
                specializationId: selectedSpec?.id,
                serviceId: selectedService!.id,
                phoneNumber,
                amount: selectedService!.price,
                consultationDate: consultationDate,
                consultationTime: consultationTime,
            });
            router.push(`/video-call/payment/${result.id}`);
        } catch (e: any) {
            let msg = e.message || "Có lỗi xảy ra, vui lòng thử lại";
            
            // Aggressively strip technical prefixes if they exist (e.g. from an old backend)
            if (msg.includes(" - 409 CONFLICT")) {
                const parts = msg.split(" - 409 CONFLICT");
                if (parts.length > 1) {
                    msg = parts[1].replace(/"/g, "").trim();
                }
            } else if (msg.includes("[FIXED_V4]")) {
                msg = msg.replace(/\[FIXED_V4\].*?:\s*/, "").trim();
            }

            // Map common conflict messages to the exact requested text
            if (msg.toLowerCase().includes("lịch khám") || 
                msg.toLowerCase().includes("tư vấn online") || 
                msg.toLowerCase().includes("gói khám")) {
                msg = "Đã có lịch khám vào khung giờ này.";
            } else if (msg.toLowerCase().includes("bác sĩ đã có lịch bận")) {
                msg = "Bác sĩ đã có lịch bận vào khung giờ này.";
            } else if (msg.toLowerCase().includes("đã có người đặt")) {
                msg = "Đã có người đặt.";
            }
            
            setError(msg);
            setSubmitting(false);
        }
    };

    return (
        <>
            <Navbar />
            <div className="min-h-screen bg-gradient-to-br from-[#e8f9f4] via-white to-[#f0f8ff] pt-36 pb-16">
                <div className="max-w-2xl mx-auto px-4">

                    {/* Header */}
                    <div className="text-center mb-8">
                        <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-gradient-to-br from-[var(--green-mid)] to-[var(--green-dark)] text-white mb-4 shadow-lg shadow-emerald-200">
                            <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 10l4.553-2.069A1 1 0 0121 8.82v6.362a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" />
                            </svg>
                        </div>
                        <h1 className="text-2xl font-bold text-[var(--green-dark)]">Tư vấn Video với Bác sĩ</h1>
                        <p className="text-sm text-gray-500 mt-1">Khám online từ xa, nhanh chóng và tiện lợi</p>
                    </div>

                    {/* Form Card */}
                    <div className="bg-white rounded-3xl shadow-sm border border-gray-100 overflow-hidden">
                        
                        {/* Step indicator - Moved down */}
                        <div className="flex items-center justify-center gap-2 py-6 bg-gray-50/50 border-b border-gray-50">
                            {["Thông tin", "Thanh toán", "Tư vấn"].map((step, i) => (
                                <div key={i} className="flex items-center gap-2">
                                    <div className={`flex items-center justify-center w-8 h-8 rounded-full text-sm font-bold transition-all
                                        ${i === 0 ? "bg-[var(--green-mid)] text-white shadow-md shadow-emerald-200" : "bg-gray-100 text-gray-400"}`}>
                                        {i + 1}
                                    </div>
                                    <span className={`text-xs font-medium ${i === 0 ? "text-[var(--green-mid)]" : "text-gray-400"}`}>{step}</span>
                                    {i < 2 && <div className="w-8 h-px bg-gray-200" />}
                                </div>
                            ))}
                        </div>

                        {/* Specialization */}
                        <div className="p-6 border-b border-gray-50">
                            <label className="block text-sm font-semibold text-[var(--green-dark)] mb-2">
                                Chuyên khoa <span className="text-red-500">*</span>
                            </label>
                            <div className="relative">
                                <select
                                    value={selectedSpec?.id ?? ""}
                                    onChange={(e) => {
                                        const spec = specializations.find(s => s.id === Number(e.target.value)) || null;
                                        setSelectedSpec(spec);
                                        setSelectedDoctor(null);
                                    }}
                                    className="w-full appearance-none bg-gray-50 border border-gray-200 rounded-xl px-4 py-3 pr-10 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-300 focus:border-emerald-400 cursor-pointer"
                                >
                                    <option value="">-- Chọn chuyên khoa --</option>
                                    {specializations.map(s => (
                                        <option key={s.id} value={s.id}>{s.name}</option>
                                    ))}
                                </select>
                                <svg className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                </svg>
                            </div>
                        </div>

                        {/* Doctor */}
                        <div className="p-6 border-b border-gray-50">
                            <label className="block text-sm font-semibold text-[var(--green-dark)] mb-2">
                                Bác sĩ <span className="text-red-500">*</span>
                            </label>
                            <div className="relative">
                                <select
                                    value={selectedDoctor?.id ?? ""}
                                    onChange={(e) => {
                                        const doc = filteredDoctors.find(d => d.id === Number(e.target.value)) || null;
                                        setSelectedDoctor(doc);
                                    }}
                                    disabled={filteredDoctors.length === 0}
                                    className="w-full appearance-none bg-gray-50 border border-gray-200 rounded-xl px-4 py-3 pr-10 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-300 focus:border-emerald-400 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
                                >
                                    <option value="">-- Chọn bác sĩ --</option>
                                    {filteredDoctors.map(d => (
                                        <option key={d.id} value={d.id}>{d.fullName} — {d.specializationName}</option>
                                    ))}
                                </select>
                                <svg className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                </svg>
                            </div>
                            {selectedSpec && filteredDoctors.length === 0 && (
                                <p className="text-xs text-amber-600 mt-1">⚠ Chưa có bác sĩ thuộc chuyên khoa này</p>
                            )}
                        </div>

                        {/* Service - Hardcoded Display */}
                        <div className="p-6 border-b border-gray-50">
                            <label className="block text-sm font-semibold text-[var(--green-dark)] mb-2">
                                Dịch vụ tư vấn
                            </label>
                            {selectedService ? (
                                <>
                                    <div className="w-full flex items-center justify-between px-4 py-3 rounded-xl border border-emerald-100 bg-emerald-50/30 text-[var(--green-dark)] text-sm">
                                        <span className="font-medium">{selectedService.name}</span>
                                        <span className="text-[var(--green-mid)] font-bold">
                                            {selectedService.price.toLocaleString("vi-VN")}đ
                                        </span>
                                    </div>
                                    <p className="text-[10px] text-gray-400 mt-2 italic">✓ Dịch vụ đã được chọn mặc định cho hình thức tư vấn online</p>
                                </>
                            ) : (
                                <div className="w-full flex items-center justify-between px-4 py-3 rounded-xl border border-amber-100 bg-amber-50/30 text-amber-700 text-sm">
                                    <span className="font-medium">Đang tải dịch vụ tư vấn...</span>
                                </div>
                            )}
                        </div>

                        {/* Phone */}
                        <div className="p-6">
                            <label className="block text-sm font-semibold text-[var(--green-dark)] mb-2">
                                Số điện thoại liên hệ <span className="text-red-500">*</span>
                            </label>
                            <input
                                type="tel"
                                value={phoneNumber}
                                onChange={(e) => { setPhoneNumber(e.target.value); if (phoneError) validatePhone(e.target.value); }}
                                onBlur={() => validatePhone(phoneNumber)}
                                placeholder="VD: 0912 345 678"
                                className={`w-full px-4 py-3 rounded-xl border text-sm focus:outline-none focus:ring-2 transition-all
                                    ${phoneError
                                        ? "border-red-300 bg-red-50 focus:ring-red-200"
                                        : phoneNumber && !phoneError
                                            ? "border-green-300 bg-green-50 focus:ring-green-200"
                                            : "border-gray-200 bg-gray-50 focus:ring-emerald-200 focus:border-emerald-400"}`}
                            />
                            {phoneError && <p className="text-xs text-red-500 mt-1">⚠ {phoneError}</p>}
                            {phoneNumber && !phoneError && <p className="text-xs text-green-600 mt-1">✓ Số điện thoại hợp lệ</p>}
                        </div>

                        {/* Consultation Date & Time */}
                        <div className="p-6 border-t border-gray-50 bg-emerald-50/10">
                            <h3 className="text-sm font-bold text-[var(--green-dark)] mb-4 flex items-center gap-2">
                                <svg className="w-4 h-4 text-emerald-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                                Thời gian tư vấn mong muốn
                            </h3>
                            <div className="mb-4">
                                <label className="block text-xs font-semibold text-gray-500 mb-1.5 uppercase tracking-wider">Ngày khám</label>
                                <input 
                                    type="date"
                                    min={getTodayStr()}
                                    value={consultationDate}
                                    onChange={(e) => { setConsultationDate(e.target.value); setConsultationTime(""); }}
                                    className="w-full md:w-1/2 bg-white border border-gray-200 rounded-xl px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-300 focus:border-emerald-400"
                                />
                            </div>
                            <div>
                                <label className="block text-xs font-semibold text-gray-500 mb-1.5 uppercase tracking-wider">Giờ khám (18:00 - 20:00)</label>
                                {selectedDoctor ? (
                                    <div className="grid grid-cols-4 md:grid-cols-5 gap-3">
                                        {[
                                            "18:00", "18:15", "18:30", "18:45",
                                            "19:00", "19:15", "19:30", "19:45", "20:00"
                                        ].filter(t => !isPastTime(consultationDate, t)).map(t => {
                                            const isBooked = bookedSlots.includes(t);
                                            const isSelected = consultationTime === t;
                                            return (
                                                <button
                                                    key={t}
                                                    onClick={() => setConsultationTime(isSelected ? "" : t)}
                                                    disabled={isBooked}
                                                    className={`py-3 rounded-xl text-sm font-semibold transition-all duration-200 border text-center ${
                                                        isBooked
                                                            ? "bg-gray-100 text-gray-400 border-gray-100 cursor-not-allowed opacity-50"
                                                            : isSelected
                                                                ? "bg-[var(--green-mid)] text-white border-[var(--green-mid)] shadow-md shadow-[var(--green-mid)]/30 scale-105"
                                                                : "bg-white text-gray-700 hover:bg-emerald-50 hover:text-[var(--green-mid)] border-gray-200 hover:border-[var(--green-mid)]/30"
                                                    }`}
                                                >
                                                    {t}
                                                </button>
                                            );
                                        })}
                                    </div>
                                ) : (
                                    <div className="px-4 py-3 bg-gray-50 border border-gray-100 rounded-xl text-sm text-gray-500 italic">
                                        Vui lòng chọn bác sĩ để xem khung giờ trống.
                                    </div>
                                )}
                                {selectedDoctor && consultationDate && ["18:00","18:15","18:30","18:45","19:00","19:15","19:30","19:45","20:00"].filter(t => !isPastTime(consultationDate, t)).length === 0 && (
                                    <p className="text-xs text-red-500 mt-2">⚠ Hết khung giờ hôm nay, vui lòng chọn ngày khác.</p>
                                )}
                            </div>
                            <p className="text-[10px] text-amber-600 mt-3 font-medium flex items-center gap-1.5">
                                <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                                Lưu ý: Khung giờ tối từ 18:00 đến 20:00 dành riêng cho tư vấn Online.
                            </p>
                        </div>

                        {/* Submit */}
                        <div className="px-6 pb-6">
                            {error && (
                                <div className="mb-4 px-4 py-3 bg-red-50 border border-red-100 rounded-xl text-sm text-red-600">
                                    ⚠ {error}
                                </div>
                            )}
                            <button
                                onClick={handleSubmit}
                                disabled={submitting}
                                className="w-full py-4 rounded-xl font-semibold text-sm text-white bg-gradient-to-r from-[var(--green-mid)] to-[var(--green-dark)] hover:from-[#1580cc] hover:to-[#0b5ca0] shadow-lg shadow-emerald-200 transition-all disabled:opacity-40 flex items-center justify-center gap-2"
                            >
                                {submitting ? (
                                    <>
                                        <svg className="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                                            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                                        </svg>
                                        Đang xử lý...
                                    </>
                                ) : (
                                    <>Tiếp tục →</>
                                )}
                            </button>
                            {!user && (
                                <p className="text-xs text-center text-gray-400 mt-2">
                                    Bạn cần{" "}
                                    <button onClick={() => router.push("/login")} className="text-[var(--green-mid)] underline">
                                        đăng nhập
                                    </button>{" "}
                                    để đặt lịch tư vấn
                                </p>
                            )}
                        </div>
                    </div>
                </div>
            </div>

            <Footer />
        </>
    );
}

