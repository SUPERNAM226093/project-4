"use client";

/**
 * FILE: RoomBookingHistory.tsx
 * MÔ TẢ: Thành phần hiển thị danh sách các phòng mà người dùng đã đặt.
 * Cho phép xem trạng thái (Đang chờ, Đã xác nhận, Đã hủy) và thực hiện hủy đặt phòng.
 */
import { useEffect, useState } from "react";
import { fetchRoomBookingsByUser, RoomBookingResponse, cancelRoomBooking } from "../lib/api";

import { toast } from "react-toastify";

interface Props {
    userId: number; // ID của người dùng đang đăng nhập
}

export default function RoomBookingHistory({ userId }: Props) {
    // --- 1. KHỞI TẠO HOOK VÀ STATE ---
    
    const [bookings, setBookings] = useState<RoomBookingResponse[]>([]); // Danh sách lịch sử đặt phòng
    const [loading, setLoading] = useState(true); // Trạng thái chờ tải dữ liệu

    /**
     * HÀM 1: useEffect loadBookings
     * MÔ TẢ: Lấy dữ liệu đặt phòng từ Backend dựa trên ID người dùng.
     */
    useEffect(() => {
        fetchRoomBookingsByUser(userId)
            .then(setBookings)
            .finally(() => setLoading(false));
    }, [userId]);

    /**
     * HÀM 2: handleCancelBooking
     * MÔ TẢ: Xử lý yêu cầu hủy đặt phòng. Yêu cầu người dùng nhập lý do hủy.
     */
    const handleCancelBooking = async (id: number) => {
        const reason = window.prompt("Nhập lý do hủy đặt phòng (không bắt buộc):");
        if (reason === null) return; // Nếu nhấn Cancel trên prompt thì dừng lại
        
        try {
            await cancelRoomBooking(id, userId, reason);
            toast.success("Hủy đặt phòng thành công!");
            // Cập nhật lại UI ngay lập tức mà không cần load lại trang
            setBookings(prev => prev.map(b => 
                b.id === id ? { ...b, status: "CANCELLED", cancelReason: reason } : b
            ));
        } catch (err: any) {
            toast.error(err.message || "Lỗi khi hủy đặt phòng");
        }
    };

    /**
     * HÀM 3: formatPrice
     * MÔ TẢ: Định dạng giá tiền VNĐ.
     */
    const formatPrice = (price: number) => {
        return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(price);
    };

    /**
     * HÀM 4: formatDate
     * MÔ TẢ: Bóc tách chuỗi ngày tháng thành các thành phần Ngày, Tháng, Năm riêng biệt để hiển thị đẹp hơn.
     */
    const formatDate = (dateStr: string) => {
        const d = new Date(dateStr);
        return {
            day: d.getDate(),
            month: d.getMonth() + 1,
            year: d.getFullYear(),
            time: d.toLocaleTimeString("vi-VN", { hour: '2-digit', minute: '2-digit' })
        };
    };

    if (loading) return null;
    if (bookings.length === 0) return null; // Nếu không có dữ liệu thì không hiển thị gì

    return (
        <div className="mt-12">
            {/* Tiêu đề mục Lịch sử đặt phòng */}
            <h2 className="text-xl font-black text-[#392E7B] mb-6 flex items-center gap-3">
                <span className="w-8 h-8 bg-[#f26522]/10 text-[#f26522] rounded-lg flex items-center justify-center">🏨</span>
                Lịch sử đặt chỗ ở nội trú
            </h2>

            <div className="space-y-6">
                {bookings.map((booking) => {
                    const date = formatDate(booking.checkInDate);
                    return (
                        /* THẺ LỊCH SỬ ĐẶT PHÒNG (Booking Card) */
                        <div key={booking.id} className="bg-white rounded-[2rem] shadow-xl shadow-gray-100 overflow-hidden border border-gray-50 flex flex-col md:flex-row">
                            
                            {/* CỘT TRÁI: Hiển thị Ngày Nhận Phòng theo dạng lịch */}
                            <div className="bg-[#f8f7ff] p-8 flex flex-col items-center justify-center border-r border-gray-50 min-w-[140px]">
                                <span className="text-gray-400 text-xs font-black uppercase mb-1">{date.year}</span>
                                <span className="text-4xl font-black text-[#392E7B]">{date.day}</span>
                                <span className="text-sm font-black text-[#7C6EE6] uppercase mt-1">Tháng {date.month}</span>
                                <span className="text-[10px] text-gray-300 mt-4 font-bold">{date.time}</span>
                            </div>

                            {/* CỘT PHẢI: Thông tin chi tiết phòng, bệnh nhân và trạng thái */}
                            <div className="p-8 flex-1 relative">
                                <div className="flex justify-between items-start mb-4">
                                    <div>
                                        <h3 className="text-lg font-black text-[#392E7B] mb-1">
                                            {booking.roomName} - Bệnh nhân: <span className="text-[#f26522]">{booking.patientName}</span>
                                        </h3>
                                        <div className="space-y-1">
                                            <p className="text-xs text-gray-500 font-bold">
                                                Ngày nhận: <span className="text-gray-700">{new Date(booking.checkInDate).toLocaleString('vi-VN')}</span>
                                            </p>
                                            <p className="text-xs text-gray-500 font-bold">
                                                Ngày trả: <span className="text-gray-700">{new Date(booking.checkOutDate).toLocaleString('vi-VN')}</span>
                                            </p>
                                        </div>
                                    </div>
                                    {/* Nhãn hiển thị trạng thái (Status Badge) */}
                                    <div className="text-right">
                                        <span className={`px-4 py-1.5 rounded-full text-[10px] font-black tracking-widest ${
                                            booking.status === 'PENDING' ? 'bg-amber-100 text-amber-600' :
                                            booking.status === 'CONFIRMED' ? 'bg-green-100 text-green-600' :
                                            'bg-gray-100 text-gray-600'
                                        }`}>
                                            {booking.status === 'PENDING' ? 'CHỜ DUYỆT' : booking.status === 'CONFIRMED' ? 'ĐÃ XÁC NHẬN' : 'ĐÃ HỦY'}
                                        </span>
                                    </div>
                                </div>

                                {/* Thông tin lệ phí dự kiến */}
                                <div className="grid grid-cols-2 gap-4 mt-6">
                                    <div>
                                        <p className="text-[10px] text-gray-400 font-black uppercase mb-1">Dịch vụ</p>
                                        <p className="text-xs font-bold text-gray-700">Lưu trú bệnh nhân nội trú</p>
                                    </div>
                                    <div>
                                        <p className="text-[10px] text-gray-400 font-black uppercase mb-1">Phí dự kiến</p>
                                        <p className="text-sm font-black text-[#f26522]">{formatPrice(booking.estimatedFee)}</p>
                                    </div>
                                </div>

                                {/* Hiển thị ghi chú của bệnh nhân (nếu có) */}
                                {booking.specialNotes && (
                                    <div className="mt-4 pt-4 border-t border-gray-50">
                                        <p className="text-[10px] text-gray-400 font-black uppercase mb-1">Ghi chú đặc biệt</p>
                                        <p className="text-xs text-gray-500 italic">{booking.specialNotes}</p>
                                    </div>
                                )}

                                {/* Nút Hủy phòng (Chỉ hiển thị khi trạng thái là Đang chờ hoặc Đã xác nhận) */}
                                {(booking.status === 'PENDING' || booking.status === 'CONFIRMED') && (
                                    <button 
                                        onClick={() => handleCancelBooking(booking.id)}
                                        className="absolute bottom-8 right-8 text-[10px] font-black text-red-400 border border-red-100 px-4 py-2 rounded-xl hover:bg-red-50 transition-all"
                                    >
                                        Hủy đặt chỗ
                                    </button>
                                )}
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
