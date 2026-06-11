"use client";
/**
 * MÔ TẢ: Thành phần hiển thị danh sách các phòng bệnh/chỗ ở nội trú của phòng khám.
 */
import { useEffect, useState, useRef } from "react";

import { useRouter } from "next/navigation";
import { fetchRooms, RoomResponse, getImageUrl } from "../lib/api";
import RoomDetailModal from "./RoomDetailModal";
import RoomBookingModal from "./RoomBookingModal";

export default function FeaturedClinics() {

    const router = useRouter();
    const [rooms, setRooms] = useState<RoomResponse[]>([]); // Lưu danh sách các phòng lấy từ API
    const [loading, setLoading] = useState(true); // Trạng thái đang tải dữ liệu
    const [selectedRoom, setSelectedRoom] = useState<RoomResponse | null>(null); // Lưu thông tin phòng đang được người dùng chọn
    const [isBooking, setIsBooking] = useState(false); // Trạng thái điều khiển việc mở Modal đặt phòng
    const scrollRef = useRef<HTMLDivElement>(null); // Tham chiếu đến container cuộn ngang

    /**
     * HÀM 0: scroll
     * MÔ TẢ: Xử lý cuộn ngang khi nhấn nút mũi tên.
     */
    const scroll = (direction: "left" | "right") => {
        if (scrollRef.current) {
            const scrollAmount = 350;
            scrollRef.current.scrollBy({
                left: direction === "left" ? -scrollAmount : scrollAmount,
                behavior: "smooth",
            });
        }
    };

    /**
     * HÀM 1: useEffect lấy danh sách phòng từ Backend khi trang web vừa được tải (mount).
     * Mục đích: Hiển thị các lựa chọn chỗ ở cho bệnh nhân.
     */
    useEffect(() => {
        fetchRooms()
            .then((data) => {
                setRooms(data);
                setLoading(false);
            })
            .catch(() => setLoading(false));
    }, []);

    /**
     * HÀM 2: formatPrice
     * MÔ TẢ: Chuyển đổi con số giá tiền thành định dạng tiền tệ Việt Nam (VNĐ).
     */
    const formatPrice = (price: number) => {
        return new Intl.NumberFormat("vi-VN").format(price) + "đ / đêm";
    };

    if (loading) return null;

    return (
        <section className="py-20 bg-transparent" id="hotel-rooms">
            <div className="max-w-7xl mx-auto px-6">
                <div className="flex justify-between items-end mb-12">
                    {/* Phần tiêu đề của mục Chỗ ở - Modern Navy Style */}
                    <div>
                        <h2 className="text-3xl md:text-4xl font-extrabold text-[#0a3d2e] tracking-tight">
                            Phòng bệnh
                        </h2>
                        <div className="w-16 h-1.5 bg-[#0d6b52] mt-4 rounded-full opacity-20"></div>
                    </div>
                    {/* Nút xem tất cả - Premium Blue */}
                    <button
                        onClick={() => router.push("/rooms")}
                        className="text-sm font-bold text-[#0d6b52] hover:text-[#1E40AF] transition-all flex items-center gap-2 group"
                    >
                        Xem tất cả phòng
                        <svg className="w-4 h-4 transform group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M13 7l5 5m0 0l-5 5m5-5H6" />
                        </svg>
                    </button>
                </div>

                <div className="relative group/carousel">
                    {/* NÚT ĐIỀU HƯỚNG TRÁI/PHẢI - Premium Style */}
                    <button
                        onClick={() => scroll("left")}
                        className="absolute -left-6 top-1/2 -translate-y-1/2 z-10 w-12 h-12 rounded-full bg-white shadow-xl flex items-center justify-center text-[#0d6b52] hover:bg-[#0d6b52] hover:text-white transition-all opacity-0 group-hover/carousel:opacity-100"
                    >
                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M15 19l-7-7 7-7" />
                        </svg>
                    </button>

                    <button
                        onClick={() => scroll("right")}
                        className="absolute -right-6 top-1/2 -translate-y-1/2 z-10 w-12 h-12 rounded-full bg-white shadow-xl flex items-center justify-center text-[#0d6b52] hover:bg-[#0d6b52] hover:text-white transition-all opacity-0 group-hover/carousel:opacity-100"
                    >
                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M9 5l7 7-7 7" />
                        </svg>
                    </button>

                    <div
                        ref={scrollRef}
                        className="flex gap-8 overflow-x-auto pb-10 px-2 no-scrollbar snap-x snap-mandatory"
                        style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}
                    >
                        {rooms.map((room) => (
                            <div
                                key={room.id}
                                className="flex-shrink-0 w-[300px] md:w-[320px] snap-start bg-white rounded-[2.5rem] overflow-hidden shadow-[0_15px_35px_-10px_rgba(0,0,0,0.05)] border border-[#b2e8d9] group hover:translate-y-[-8px] transition-all duration-500 hover:shadow-[0_20px_45px_-10px_rgba(13,107,82,0.15)] hover:border-[#0d6b52]/30"
                            >
                                {/* PHẦN TRÊN THẺ: Hình ảnh và Trạng thái phòng */}
                                <div className="relative h-60 overflow-hidden">
                                    {room.images && room.images.length > 0 ? (
                                        <img
                                            src={getImageUrl(room.images[0])}
                                            alt={room.name}
                                            className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
                                        />
                                    ) : (
                                        <div className="w-full h-full bg-[var(--green-ultra)] flex items-center justify-center">
                                            <span className="text-4xl font-black text-[#b2e8d9]">{room.roomCode}</span>
                                        </div>
                                    )}
                                    {/* Nhãn trạng thái - Modern style */}
                                    <div className="absolute top-5 right-5">
                                        <span className={`px-4 py-1.5 rounded-full text-[10px] font-extrabold tracking-widest shadow-lg ${room.status === 'AVAILABLE' ? 'bg-[#0d6b52] text-white' :
                                                room.status === 'MAINTENANCE' ? 'bg-amber-500 text-white' :
                                                    'bg-red-500 text-white'
                                            }`}>
                                            {room.status === 'AVAILABLE' ? 'CÒN PHÒNG' : room.status === 'MAINTENANCE' ? 'BẢO TRÌ' : 'HẾT PHÒNG'}
                                        </span>
                                    </div>
                                </div>

                                {/* THÂN THẺ: Tên, Mô tả và Giá phòng */}
                                <div className="p-8 text-center">
                                    <h3 className="text-xl font-extrabold text-[#0a3d2e] mb-3 uppercase tracking-tight group-hover:text-[#0d6b52] transition-colors">{room.name}</h3>
                                    <p className="text-[#4d8871] text-[13px] font-medium leading-relaxed mb-6 line-clamp-2 min-h-[40px]">
                                        {room.description || "Phòng nghỉ hiện đại, tiện nghi dành cho bệnh nhân nội trú với sự theo dõi y tế 24/7."}
                                    </p>
                                    <div className="text-[#0a3d2e] font-extrabold text-lg mb-8">
                                        {formatPrice(room.pricePerNight)}
                                    </div>
                                    <button
                                        onClick={() => setSelectedRoom(room)}
                                        disabled={room.status === 'MAINTENANCE'}
                                        className={`w-full font-bold py-4 rounded-2xl transition-all duration-300 ${room.status !== 'MAINTENANCE'
                                                ? 'bg-[var(--green-ultra)] text-[#0d6b52] border border-[#b2e8d9] hover:bg-[#0d6b52] hover:text-white hover:border-[#0d6b52] hover:shadow-lg hover:shadow-emerald-100'
                                                : 'bg-gray-50 text-gray-400 cursor-not-allowed border border-gray-100'
                                            }`}
                                    >
                                        {room.status === 'MAINTENANCE' ? 'Đang bảo trì' : 'Xem chi tiết'}
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Modal hiển thị chi tiết phòng */}
            {selectedRoom && !isBooking && (
                <RoomDetailModal
                    room={selectedRoom}
                    onClose={() => setSelectedRoom(null)}
                    onBook={() => setIsBooking(true)}
                />
            )}

            {/* Modal thực hiện đặt phòng */}
            {selectedRoom && isBooking && (
                <RoomBookingModal
                    room={selectedRoom}
                    onClose={() => {
                        setSelectedRoom(null);
                        setIsBooking(false);
                    }}
                />
            )}
        </section>
    );
}

