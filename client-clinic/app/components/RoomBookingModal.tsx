"use client";
/**
 * FILE: RoomBookingModal.tsx
 * MÔ TẢ: Modal cho phép bệnh nhân thực hiện đặt chỗ ở nội trú, chọn ngày nhận/trả phòng và nhập thông tin liên hệ.
 */
import { useState, useEffect } from "react";

import { RoomResponse, createRoomBooking, AuthResponse, fetchRoomBookingsByUser } from "../lib/api";
import { useToast } from "../context/ToastContext";
import { getCurrentDateTimeLocalStr } from "../lib/dateUtils";

interface Props {
    room: RoomResponse;
    onClose: () => void;
}

export default function RoomBookingModal({ room, onClose }: Props) {
    // --- 1. KHAI BÁO HOOK VÀ STATE ---
    
    const { showToast } = useToast();
    const [user, setUser] = useState<AuthResponse | null>(null); // Thông tin người dùng hiện tại
    const [form, setForm] = useState({ // Dữ liệu form đặt phòng
        patientName: "",
        checkInDate: "",
        checkOutDate: "",
        numberOfPatients: 1,
        specialNotes: "",
        contactPhone: ""
    });
    const [error, setError] = useState<string | null>(null); // Lưu lỗi từ Backend
    const [phoneError, setPhoneError] = useState<string | null>(null); // Lỗi nhập số điện thoại
    const [loading, setLoading] = useState(false); // Trạng thái đang gửi yêu cầu

    /**
     * HÀM 1: validatePhone
     * MÔ TẢ: Kiểm tra số điện thoại người dùng nhập có đúng 10 chữ số hay không.
     */
    const validatePhone = (phone: string) => {
        if (!/^[0-9]{10}$/.test(phone)) {
            setPhoneError("Số điện thoại phải có đúng 10 chữ số.");
            return false;
        }
        setPhoneError(null);
        return true;
    };

    /**
     * HÀM 2: useEffect loadUser
     * MÔ TẢ: Tự động điền thông tin Tên và SĐT của người dùng đã đăng nhập vào form.
     */
    useEffect(() => {
        const saved = localStorage.getItem("clinic_user");
        if (saved) {
            const userData = JSON.parse(saved);
            setUser(userData);
            setForm(f => ({ ...f, patientName: userData.fullName, contactPhone: userData.phone || "" }));
        }
    }, []);

    /**
     * HÀM 3: handleSubmit
     * MÔ TẢ: Xử lý khi nhấn nút Xác nhận đặt chỗ. Gửi dữ liệu lên Server và xử lý lỗi trùng lịch (409 Conflict).
     */
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!user) {
            showToast("Vui lòng đăng nhập để tiếp tục.", "info");
            return;
        }

        if (!validatePhone(form.contactPhone)) {
            return;
        }

        setLoading(true);
        setError(null);
        try {
            // Validate maximum 3 active room bookings to prevent spam
            try {
                const userRooms = await fetchRoomBookingsByUser(user.userId);
                const activeRooms = userRooms.filter(r => 
                    r.status !== "CANCELLED" && r.status !== "COMPLETED"
                );
                if (activeRooms.length >= 3) {
                    setError("Bạn đã đạt giới hạn tối đa 3 lượt đặt chỗ ở đang hoạt động. Vui lòng chờ sử dụng xong hoặc hủy bớt để đặt thêm.");
                    setLoading(false);
                    return;
                }
            } catch (fetchErr) {
                console.error("Không thể kiểm tra lịch sử đặt phòng:", fetchErr);
            }

            // Fix time to 12:00 PM as requested
            const checkIn = `${form.checkInDate}T12:00:00`;
            const checkOut = `${form.checkOutDate}T12:00:00`;

            await createRoomBooking(user.userId, {
                roomId: room.id,
                ...form,
                checkInDate: new Date(checkIn).toISOString(),
                checkOutDate: new Date(checkOut).toISOString()
            });
            showToast("Đã gửi yêu cầu đặt chỗ thành công!", "success");
            onClose();
        } catch (err: any) {
            let msg = err.message || "Đặt chỗ thất bại. Vui lòng thử lại.";
            
            // Aggressively strip technical prefixes if they exist
            if (msg.includes(" - 409 CONFLICT")) {
                const parts = msg.split(" - 409 CONFLICT");
                if (parts.length > 1) {
                    msg = parts[1].replace(/"/g, "").trim();
                }
            } else if (msg.includes("[FIXED_V4]")) {
                msg = msg.replace(/\[FIXED_V4\].*?:\s*/, "").trim();
            }

            // Map common conflict messages
            if (msg.toLowerCase().includes("đã có người đặt")) {
                // Keep the detailed message from backend which includes the dates
            }

            setError(msg);
            showToast(msg, "error");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 z-[110] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm animate-in fade-in duration-300">
            <div className="bg-white rounded-3xl w-full max-w-md overflow-hidden shadow-2xl">
                <div className="p-6 border-b border-gray-100 flex justify-between items-center">
                    <h2 className="text-xl font-black text-[#000000]">Đặt chỗ ở</h2>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" /></svg>
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-5">
                    {/* CHỌN BỆNH NHÂN */}
                    <div>
                        <label className="block text-[11px] font-black text-gray-400 uppercase mb-2">Chọn Bệnh nhân</label>
                        <select 
                            className="w-full bg-gray-50 border-none rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-[#0052CC] outline-none"
                            value={form.patientName}
                            onChange={e => setForm({ ...form, patientName: e.target.value })}
                        >
                            <option value={user?.fullName}>{user?.fullName}</option>
                        </select>
                    </div>

                    {/* CHỌN NGÀY NHẬN VÀ TRẢ PHÒNG */}
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-[11px] font-black text-gray-400 uppercase mb-2">Ngày nhận phòng</label>
                            <input 
                                type="date"
                                required
                                min={new Date().toISOString().split('T')[0]}
                                className="w-full bg-gray-50 border-none rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-[#0052CC] outline-none"
                                value={form.checkInDate}
                                onChange={e => setForm({ ...form, checkInDate: e.target.value })}
                            />
                            <p className="text-[9px] text-gray-400 mt-1 italic">Nhận phòng: 12:00 trưa</p>
                        </div>

                        <div>
                            <label className="block text-[11px] font-black text-gray-400 uppercase mb-2">Ngày trả phòng</label>
                            <input 
                                type="date"
                                required
                                min={form.checkInDate || new Date().toISOString().split('T')[0]}
                                className="w-full bg-gray-50 border-none rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-[#0052CC] outline-none"
                                value={form.checkOutDate}
                                onChange={e => setForm({ ...form, checkOutDate: e.target.value })}
                            />
                            <p className="text-[9px] text-gray-400 mt-1 italic">Trả phòng: 12:00 trưa</p>
                        </div>
                    </div>

                    <div>
                        <label className="block text-[11px] font-black text-gray-400 uppercase mb-2">Số điện thoại liên hệ</label>
                        <input 
                            type="text"
                            required
                            placeholder="Nhập số điện thoại..."
                            className={`w-full bg-gray-50 rounded-xl px-4 py-3 text-sm focus:ring-2 outline-none transition-all ${
                                phoneError ? "ring-2 ring-red-500 bg-red-50" : "focus:ring-[#0052CC]"
                            }`}
                            value={form.contactPhone}
                            onChange={e => {
                                setForm({ ...form, contactPhone: e.target.value });
                                if (phoneError) validatePhone(e.target.value);
                            }}
                            onBlur={e => validatePhone(e.target.value)}
                        />
                        {phoneError && <p className="text-[10px] text-red-500 mt-1 ml-1">{phoneError}</p>}
                    </div>

                    {/* GHI CHÚ ĐẶC BIỆT */}
                    <div>
                        <label className="block text-[11px] font-black text-gray-400 uppercase mb-2">Ghi chú (Tùy chọn)</label>
                        <textarea 
                            className="w-full bg-gray-50 border-none rounded-xl px-4 py-3 text-sm focus:ring-2 focus:ring-[#0052CC] outline-none"
                            rows={2}
                            placeholder="Ví dụ: Cần phòng yên tĩnh, hỗ trợ xe lăn..."
                            value={form.specialNotes}
                            onChange={e => setForm({ ...form, specialNotes: e.target.value })}
                        />
                    </div>

                    {error && (
                        <div className="p-3 bg-red-50 border border-red-100 rounded-xl text-xs text-red-600 animate-in slide-in-from-top-1 duration-200">
                            ⚠ {error}
                        </div>
                    )}

                    {/* CÁC NÚT ĐIỀU KHIỂN */}
                    <div className="flex gap-3 pt-4">
                        <button 
                            type="button"
                            onClick={onClose}
                            className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-600 font-bold py-3.5 rounded-2xl transition-all"
                        >
                            Hủy
                        </button>
                        <button 
                            type="submit"
                            disabled={loading}
                            className="flex-[2] bg-[#0052CC] hover:bg-[#e05611] text-white font-black py-3.5 rounded-2xl shadow-xl shadow-[#0052CC]/20 transition-all disabled:opacity-50"
                        >
                            {loading ? "Đang xử lý..." : "Xác nhận đặt chỗ"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

