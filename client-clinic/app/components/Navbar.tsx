"use client";
/**
 * FILE: Navbar.tsx
 * MÔ TẢ: Thành phần thanh điều hướng chính, quản lý menu, đa ngôn ngữ, và xác thực người dùng (Đăng nhập/Đăng ký).
 */
import { useState, useEffect, useRef } from "react";
import Image from "next/image";
import Link from "next/link";

import { login, register, forgotPassword, resetPassword, LoginRequest, RegisterRequest, AuthResponse } from "../lib/api";
import { useToast } from "../context/ToastContext";
import { usePWA } from "../hooks/usePWA";

const socialLinks = [
    { name: "Tiktok", icon: "🎵" },
    { name: "Facebook", icon: "f" },
    { name: "Zalo", icon: "Z" },
    { name: "Youtube", icon: "▶" },
];

interface MenuItem {
    key: string;
    hasDropdown: boolean;
    name?: string;
    href?: string;
    subItems?: Array<{
        key: string;
        href: string;
        name?: string;
    }>;
}

/**
 * CẤU HÌNH MENU CHÍNH: Định nghĩa các mục trên thanh điều hướng và menu con (dropdown).
 */
const menuItems: MenuItem[] = [
    {
        key: "facilities",
        name: "Cơ sở y tế",
        hasDropdown: true,
        subItems: [
            { key: "allHospitals", name: "Tất cả cơ sở y tế", href: "/hospitals" },
            { key: "priorityBooking", name: "Đặt lịch khám ưu tiên", href: "/priority-booking" }
        ]
    },
    {
        key: "services",
        name: "Dịch vụ y tế",
        hasDropdown: true,
        subItems: [
            { key: "facilityRegistration", name: "Đăng ký khám tại cơ sở", href: "/service-registration" },
            { key: "specializationBooking", name: "Đặt lịch khám theo chuyên khoa", href: "/specialization" },
            { key: "healthPackage", name: "Gói khám sức khỏe toàn diện", href: "/health-package" }
        ]
    },
    {
        key: "doctors",
        name: "Đội ngũ Bác sĩ",
        hasDropdown: false,
        href: "/doctor"
    },

    {
        key: "guide",
        name: "Hướng dẫn",
        hasDropdown: true,
        subItems: [
            { key: "faq", name: "Câu hỏi thường gặp (FAQ)", href: "/guide/faq" }
        ]
    },
    {
        key: "contact",
        name: "Liên hệ",
        hasDropdown: true,
        subItems: [
            { key: "contactAll", name: "Thông tin liên hệ", href: "/contact" }
        ]
    },
    {
        key: "nearestClinic",
        name: "Tìm cơ sở y tế",
        href: "/nearest-clinic",
        hasDropdown: false
    },
    {
        key: "yourInfo",
        name: "Thông tin của bạn",
        hasDropdown: true,
        subItems: [
            { key: "profile", name: "Hồ sơ cá nhân", href: "/ho-so-kham" },
            { key: "history", name: "Lịch sử khám", href: "/lich-su-kham" },
            { key: "prescriptions", name: "Đơn thuốc của tôi", href: "/don-thuoc" }
        ]
    }
];

type AuthTab = "login" | "register" | "forgot";

export default function Navbar() {
    // --- 1. KHỞI TẠO HOOKS VÀ CÁC TRẠNG THÁI (STATE) CỦA COMPONENT ---
    
    // Sử dụng context Toast để hiển thị các thông báo nổi ở góc màn hình (Thành công, Lỗi, Thông tin...)
    const { showToast } = useToast(); 
    
    // State dùng để theo dõi xem người dùng đã cuộn trang xuống chưa (scrollY > 50px) để thay đổi CSS (thêm shadow, đổi opacity)
    const [isScrolled, setIsScrolled] = useState(false); 
    
    // Quản lý việc đóng/mở menu rút gọn khi hiển thị trên các thiết bị di động (Mobile Menu)
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false); 
    
    // Trạng thái hiển thị (ẩn/hiện) của Modal chứa luồng xác thực Đăng nhập / Đăng ký / Quên mật khẩu
    const [showAuthModal, setShowAuthModal] = useState(false); 
    
    // Phân loại giao diện đang hiển thị trong Modal xác thực: "login" (Đăng nhập), "register" (Đăng ký) hoặc "forgot" (Quên mật khẩu)
    const [authTab, setAuthTab] = useState<AuthTab>("login"); 
    
    // Lưu trữ thông tin định danh của người dùng hiện tại (Họ tên, email, vai trò, token) sau khi đăng nhập thành công
    const [user, setUser] = useState<AuthResponse | null>(null); 
    
    // Quản lý việc ẩn/hiện danh sách menu con khi người dùng nhấp vào ảnh đại diện (avatar) của mình
    const [showUserMenu, setShowUserMenu] = useState(false); 
    
    // Xác định menu dropdown nào trên thanh điều hướng đang được rê chuột (hover) vào (Facilities, Services, Guide, Contact...)
    const [activeDropdown, setActiveDropdown] = useState<string | null>(null); 
    
    // Lấy các thuộc tính và hàm từ Custom Hook PWA dùng cho việc cài đặt ứng dụng web thành ứng dụng di động/máy tính độc lập
    const { isInstallable, isStandalone, isIOS, installApp } = usePWA(); 

    // Biến trạng thái lưu thông tin nhập liệu cho Form Đăng nhập (Email và Mật khẩu)
    const [loginForm, setLoginForm] = useState<LoginRequest>({ email: "", password: "" });
    
    // Biến trạng thái lưu thông tin nhập liệu cho Form Đăng ký tài khoản mới của bệnh nhân
    const [registerForm, setRegisterForm] = useState<RegisterRequest>({
        email: "",
        password: "",
        fullName: "",
        phone: "",
        gender: "",
        dateOfBirth: "",
        address: "",
    });
    
    // Trạng thái hiển thị hoạt ảnh tải dữ liệu (loading spinner) khi đang gọi các API xác thực lên máy chủ backend
    const [authLoading, setAuthLoading] = useState(false); 
    const [authError, setAuthError] = useState<string | null>(null);
    const [authSuccess, setAuthSuccess] = useState<string | null>(null);
    
    // Lưu email của người dùng khi họ cần khôi phục mật khẩu trong luồng Quên mật khẩu
    const [forgotEmail, setForgotEmail] = useState(""); 
    
    // Mã Token xác thực (OTP) do hệ thống gửi về Email của bệnh nhân để xác thực danh tính
    const [resetToken, setResetToken] = useState(""); 
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    
    // Quản lý các bước trong luồng khôi phục mật khẩu: Bước 1 (Nhập Email gửi OTP), Bước 2 (Nhập OTP & Mật khẩu mới)
    const [forgotStep, setForgotStep] = useState(1); 
    
    // Bộ đếm ngược thời gian (đơn vị: giây) để giới hạn tần suất nhấn nút "Gửi lại mã OTP" nhằm chống spam mail
    const [resendCountdown, setResendCountdown] = useState(0); 

    // Các tham chiếu Ref trỏ trực tiếp đến phần tử DOM giúp nhận diện sự kiện click ra ngoài vùng hiển thị để đóng popup tự động
    const modalRef = useRef<HTMLDivElement>(null); // Tham chiếu đến hộp thoại Modal
    const userMenuRef = useRef<HTMLDivElement>(null); // Tham chiếu đến Menu thả xuống của Avatar người dùng

    /**
     * EFFECT: loadUser
     * Nhiệm vụ: Tự động kiểm tra trạng thái đăng nhập của bệnh nhân mỗi khi trang được tải lại (F5).
     * Đọc token và thông tin user từ LocalStorage. Đồng thời lắng nghe sự kiện cập nhật profile để đồng bộ dữ liệu.
     */
    useEffect(() => {
        const loadUser = () => {
            const savedUser = localStorage.getItem("clinic_user");
            const savedToken = localStorage.getItem("clinic_token");
            if (savedUser && savedToken) {
                try {
                    // Chuyển đổi chuỗi JSON lưu trong bộ nhớ thành đối tượng JavaScript
                    setUser(JSON.parse(savedUser));
                } catch {
                    // Nếu dữ liệu bị hỏng hoặc không đúng định dạng, tiến hành xóa sạch bộ nhớ để tránh lỗi hệ thống
                    localStorage.removeItem("clinic_user");
                    localStorage.removeItem("clinic_token");
                }
            } else {
                setUser(null);
            }
        };

        loadUser();

        // Lắng nghe sự kiện tùy chỉnh 'userProfileUpdated' (được kích hoạt khi người dùng thay đổi thông tin cá nhân ở trang Hồ sơ)
        window.addEventListener("userProfileUpdated", loadUser);
        return () => window.removeEventListener("userProfileUpdated", loadUser);
    }, []);

    /**
     * EFFECT: handleScroll
     * Nhiệm vụ: Lắng nghe sự kiện cuộn chuột trên cửa sổ trình duyệt (window scroll).
     * Khi cuộn xuống quá 50px, biến isScrolled chuyển sang true, kích hoạt đổi màu nền thanh Navbar sang đục và thêm bóng đổ.
     */
    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 50);
        };
        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    /**
     * EFFECT: handleOutsideClick
     * Nhiệm vụ: Tối ưu hóa trải nghiệm người dùng bằng cách tự động ẩn các cửa sổ popup (Modal, Menu Avatar)
     * khi họ nhấp chuột ra ngoài vùng hiển thị của các phần tử này.
     */
    useEffect(() => {
        const handleClickOutside = (e: MouseEvent) => {
            // Nếu click ra ngoài khung Modal xác thực đang mở thì đóng Modal và xóa sạch dữ liệu form
            if (modalRef.current && !modalRef.current.contains(e.target as Node)) {
                setShowAuthModal(false);
                resetForm();
            }
            // Nếu click ra ngoài khung Menu của Avatar đang mở thì ẩn Menu đi
            if (userMenuRef.current && !userMenuRef.current.contains(e.target as Node)) {
                setShowUserMenu(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    /**
     * EFFECT: handleEscKey
     * Nhiệm vụ: Lắng nghe sự kiện nhấn phím ESC trên bàn phím để người dùng có thể đóng Modal xác thực nhanh chóng.
     */
    useEffect(() => {
        const handleEsc = (e: KeyboardEvent) => {
            if (e.key === "Escape") {
                setShowAuthModal(false);
                resetForm();
            }
        };
        document.addEventListener("keydown", handleEsc);
        return () => document.removeEventListener("keydown", handleEsc);
    }, []);

    /**
     * HÀM: resetForm
     * Nhiệm vụ: Xóa sạch dữ liệu đã nhập trong các Form đăng nhập, đăng ký và làm sạch thông báo lỗi/thành công.
     */
    const resetForm = () => {
        setLoginForm({ email: "", password: "" });
        setRegisterForm({
            email: "", password: "", fullName: "", phone: "", gender: "", dateOfBirth: "", address: "",
        });
        setAuthError(null);
        setAuthSuccess(null);
    };

    /**
     * HÀM: openAuthModal
     * Nhiệm vụ: Mở hộp thoại Modal xác thực và đặt Tab hiển thị mong muốn (login, register, forgot).
     * Đồng thời tự động đóng menu mobile nếu đang mở.
     */
    const openAuthModal = (tab: AuthTab) => {
        setAuthTab(tab);
        setShowAuthModal(true);
        setMobileMenuOpen(false);
    };

    /**
     * HÀM: handleLogin
     * Nhiệm vụ: Xử lý quy trình gửi yêu cầu Đăng nhập tài khoản bệnh nhân lên backend.
     */
    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setAuthLoading(true);
        setAuthError(null);
        try {
            // Gọi hàm API thực hiện POST request đăng nhập
            const res = await login(loginForm);

            // Chặn đăng nhập nếu người dùng sử dụng tài khoản nhân viên/bác sĩ/admin ở cổng bệnh nhân
            if (res.role === "ADMIN" || res.role === "STAFF" || res.role === "DOCTOR") {
                showToast("Tài khoản này không có quyền truy cập trang khách hàng. Vui lòng dùng tài khoản Patient.", "error");
                setAuthLoading(false);
                return;
            }

            // Lưu thông tin người dùng vào state và bộ nhớ trình duyệt (localStorage)
            setUser(res);
            localStorage.setItem("clinic_token", res.token);
            localStorage.setItem("clinic_user", JSON.stringify(res));
            
            // Đóng Modal và làm sạch form
            setShowAuthModal(false);
            resetForm();
            showToast("Đăng nhập thành công!", "success");

            // Tải lại trang web để cập nhật toàn bộ trạng thái hệ thống và đồng bộ giỏ hàng, thông tin khám
            setTimeout(() => {
                window.location.href = "/";
            }, 500);
        } catch (err: unknown) {
            const message = err instanceof Error ? err.message : "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.";
            showToast(message, "error");
        } finally {
            setAuthLoading(false);
        }
    };

    /**
     * HÀM: handleForgotPassword
     * Nhiệm vụ: Xử lý yêu cầu gửi mã xác nhận (OTP) về Email của bệnh nhân để chuẩn bị đổi mật khẩu mới.
     */
    const handleForgotPassword = async (e: React.FormEvent) => {
        e.preventDefault();
        // Kiểm tra bảo mật: Hệ thống chỉ chấp nhận định dạng email @gmail.com
        if (!forgotEmail.match(/^[a-zA-Z0-9._%+-]+@gmail\.com$/)) {
            showToast("Hệ thống chỉ hỗ trợ khôi phục mật khẩu cho tài khoản Gmail (@gmail.com).", "error");
            return;
        }
        setAuthLoading(true);
        try {
            // Gọi API yêu cầu đặt lại mật khẩu
            const res = await forgotPassword(forgotEmail);
            showToast(res.message, "success");
            // Chuyển form sang bước thứ 2 (Nhập OTP & Mật khẩu mới)
            setForgotStep(2);
            // Kích hoạt đồng hồ đếm ngược chờ gửi lại mã
            startCountdown();
        } catch (err: any) {
            showToast(err.message, "error");
        } finally {
            setAuthLoading(false);
        }
    };

    /**
     * HÀM: handleResetPassword
     * Nhiệm vụ: Gửi mã OTP xác thực và mật khẩu mới lên backend để ghi đè mật khẩu cũ của bệnh nhân.
     */
    const handleResetPassword = async (e: React.FormEvent) => {
        e.preventDefault();
        // Ràng buộc bảo mật: Mật khẩu tối thiểu phải từ 6 ký tự
        if (newPassword.length < 6) {
            showToast("Mật khẩu mới phải có ít nhất 6 ký tự để đảm bảo an toàn.", "error");
            return;
        }
        // Kiểm tra xem hai lần nhập mật khẩu có trùng khớp hay không
        if (newPassword !== confirmPassword) {
            showToast("Mật khẩu xác nhận không trùng khớp. Vui lòng nhập lại.", "error");
            return;
        }
        setAuthLoading(true);
        try {
            // Gọi API gửi mã OTP cùng mật khẩu mới lên server để thực hiện thay đổi
            const res = await resetPassword(resetToken, newPassword);
            showToast(res.message, "success");
            setTimeout(() => {
                // Đổi giao diện hiển thị về Tab Đăng nhập và reset các thông số form
                setAuthTab("login");
                setForgotStep(1);
                resetForm();
            }, 2000);
        } catch (err: any) {
            showToast(err.message, "error");
        } finally {
            setAuthLoading(false);
        }
    };

    /**
     * HÀM: startCountdown
     * Nhiệm vụ: Đếm ngược thời gian chờ (60 giây) trước khi kích hoạt lại tính năng gửi mã OTP.
     */
    const startCountdown = () => {
        setResendCountdown(60);
        const timer = setInterval(() => {
            setResendCountdown((prev) => {
                if (prev <= 1) {
                    clearInterval(timer);
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);
    };

    /**
     * HÀM: handleRegister
     * Nhiệm vụ: Xử lý gửi thông tin cá nhân của bệnh nhân lên backend để đăng ký tài khoản mới.
     */
    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();

        // Ràng buộc định dạng email: Chỉ hỗ trợ email đuôi @gmail.com
        if (!registerForm.email.toLowerCase().endsWith("@gmail.com")) {
            showToast("Hệ thống chỉ hỗ trợ đăng ký tài khoản qua Gmail (@gmail.com).", "error");
            return;
        }

        // Ràng buộc số điện thoại: Phải bao gồm đúng 10 chữ số
        if (registerForm.phone && !/^\d{10}$/.test(registerForm.phone)) {
            showToast("Số điện thoại không hợp lệ. Vui lòng nhập đúng 10 chữ số.", "error");
            return;
        }

        setAuthLoading(true);
        try {
            // Tiến hành gọi API đăng ký tài khoản mới kèm các giá trị mặc định cho hồ sơ bệnh nhân
            const res = await register({
                ...registerForm,
                dateOfBirth: "2000-01-01", // Đặt tạm ngày sinh mặc định
                address: "Chưa cập nhật",   // Đặt tạm địa chỉ mặc định
            });
            
            // Đăng nhập tự động cho người dùng ngay sau khi đăng ký thành công
            setUser(res);
            localStorage.setItem("clinic_token", res.token);
            localStorage.setItem("clinic_user", JSON.stringify(res));
            showToast("Đăng ký tài khoản thành công!", "success");
            
            setTimeout(() => {
                setShowAuthModal(false);
                resetForm();
                // Tải lại trang chủ
                window.location.href = "/";
            }, 1000);
        } catch (err: unknown) {
            const message = err instanceof Error ? err.message : "Đăng ký thất bại. Email này có thể đã được sử dụng.";
            showToast(message, "error");
        } finally {
            setAuthLoading(false);
        }
    };

    /**
     * HÀM: handleLogout
     * Nhiệm vụ: Đăng xuất tài khoản bệnh nhân ra khỏi trình duyệt.
     * Xóa sạch Token và thông tin lưu trữ trong localStorage, sau đó tải lại trang chủ.
     */
    const handleLogout = () => {
        setUser(null);
        localStorage.removeItem("clinic_token");
        localStorage.removeItem("clinic_user");
        showToast("Đã đăng xuất thành công khỏi hệ thống!", "info");
        setTimeout(() => {
            window.location.href = "/";
        }, 500);
    };

    return (
        <>
            {/* --- KHỐI THANH ĐIỀU HƯỚNG (HEADER) --- */}
            <header
                className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${isScrolled ? "shadow-md" : ""
                    }`}
            >
                {/* --- 1. THANH PHỤ TRÊN CÙNG (TOP BAR) --- */}
                <div className="bg-white/95 backdrop-blur-md border-b border-[#D6EAFE] relative z-[60]">
                    <div className="max-w-7xl mx-auto px-4 flex items-center justify-between h-10">
                        <div className="flex items-center gap-3 ml-auto">
                            {/* NÚT CÀI ĐẶT ỨNG DỤNG (PWA) - Hiển thị nếu thiết bị hỗ trợ cài App */}
                            {!isStandalone && (isInstallable || isIOS) && (
                                <button
                                    onClick={() => isIOS ? showToast("Hệ điều hành iOS/Mac hiện chưa được hỗ trợ tải ứng dụng này.", "error") : installApp()}
                                    className="bg-[#EAF4FF] text-[#2563EB] text-[11px] font-bold px-4 py-1.5 rounded-full hover:bg-[#2563EB] hover:text-white transition-all flex items-center gap-1.5"
                                >
                                    <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" /></svg>
                                    {"Tải ứng dụng"}
                                </button>
                            )}



                            {/* KHU VỰC TÀI KHOẢN NGƯỜI DÙNG (AUTHENTICATION AREA) */}
                            {user ? (
                                <div className="relative" ref={userMenuRef}>
                                    <button
                                        onClick={() => setShowUserMenu(!showUserMenu)}
                                        className="flex items-center gap-2 bg-white border border-[#2563EB] text-[#2563EB] text-[11px] font-bold px-3 py-1.5 rounded-full hover:bg-[#F2FAFF] transition-all shadow-sm group"
                                    >
                                        <div className="w-5 h-5 rounded-full bg-gradient-to-br from-[#2563EB] to-[#56CCF2] text-white flex items-center justify-center text-[10px] font-bold shadow-sm">
                                            {user.fullName?.charAt(0)?.toUpperCase() || user.email.charAt(0).toUpperCase()}
                                        </div>
                                        <span className="max-w-[120px] truncate">{user.fullName || user.email}</span>
                                        <svg className={`w-3 h-3 transition-transform ${showUserMenu ? 'rotate-180' : ''}`} fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" /></svg>
                                    </button>

                                    {/* MENU THẢ XUỐNG KHI ĐÃ ĐĂNG NHẬP */}
                                    {showUserMenu && (
                                        <div className="absolute right-0 top-full w-64 bg-white rounded-xl shadow-2xl border border-[#D6EAFE] overflow-hidden z-50 animate-in fade-in slide-in-from-top-1">
                                            <div className="px-4 py-4 bg-[#F8FCFF] border-b border-[#D6EAFE]">
                                                <div className="flex items-center gap-2 mb-2">
                                                    <span className="text-[10px] font-bold text-white bg-[#2563EB] px-2 py-0.5 rounded-md uppercase tracking-wider">{user.role}</span>
                                                </div>
                                                <p className="text-sm font-bold text-[#102A56] truncate">{user.fullName || "Người dùng"}</p>
                                                <p className="text-[11px] text-[#5F789A] truncate">{user.email}</p>
                                            </div>
                                            <div className="py-1">
                                                <Link href="/ho-so-kham" className="w-full text-left px-4 py-2.5 text-sm text-[#102A56] hover:bg-[#EAF4FF] flex items-center gap-3 transition-colors group">
                                                    <svg className="w-4 h-4 text-[#2563EB]" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>
                                                    <span className="group-hover:text-[#2563EB]">{"Hồ sơ cá nhân"}</span>
                                                </Link>
                                                <Link href="/lich-su-kham" className="w-full text-left px-4 py-2.5 text-sm text-[#102A56] hover:bg-[#EAF4FF] flex items-center gap-3 transition-colors group">
                                                    <svg className="w-4 h-4 text-[#2563EB]" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" /></svg>
                                                    <span className="group-hover:text-[#2563EB]">{"Lịch sử khám"}</span>
                                                </Link>
                                                <Link href="/don-thuoc" className="w-full text-left px-4 py-2.5 text-sm text-[#102A56] hover:bg-[#EAF4FF] flex items-center gap-3 transition-colors group">
                                                    <svg className="w-4 h-4 text-[#2563EB]" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" /></svg>
                                                    <span className="group-hover:text-[#2563EB]">{"Đơn thuốc của tôi"}</span>
                                                </Link>
                                            </div>
                                            <div className="border-t border-[#D6EAFE] py-1">
                                                <button onClick={handleLogout} className="w-full text-left px-4 py-2.5 text-sm text-red-500 hover:bg-red-50 flex items-center gap-2 transition-colors">
                                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" /></svg>
                                                    {"Đăng xuất"}
                                                </button>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            ) : (
                                <button
                                    onClick={() => openAuthModal("login")}
                                    className="btn-premium text-xs px-6 py-2 rounded-full"
                                >
                                    Đăng nhập
                                </button>
                            )}

                        </div>
                    </div>
                </div>

                {/* --- 2. THANH MENU ĐIỀU HƯỚNG CHÍNH (MAIN NAV) --- */}
                <div className="bg-[var(--background)]/90 backdrop-blur-lg shadow-sm border-b border-blue-50 relative z-[50]">
                    <div className="max-w-7xl mx-auto px-4 flex items-center justify-between h-16">
                        {/* LOGO THƯƠNG HIỆU */}
                        <a href="/" className="flex items-center gap-2 shrink-0">
                            <Image src="/logo-medpro.png" alt="MedPro" width={120} height={40} className="h-10 w-auto object-contain" />
                        </a>

                        {/* DANH SÁCH MENU (CHO MÀN HÌNH MÁY TÍNH) */}
                        <nav className="hidden lg:flex items-center gap-1 ml-auto">
                            {menuItems.map((item) => (
                                <div key={item.key} className="relative group" onMouseEnter={() => setActiveDropdown(item.key)} onMouseLeave={() => setActiveDropdown(null)}>
                                    {item.hasDropdown ? (
                                        <button className={`flex items-center gap-1 px-3 py-2 text-sm transition-all duration-200 font-bold whitespace-nowrap hover:text-[#2563EB] ${activeDropdown === item.key ? "text-[#2563EB]" : "text-[#102A56]"}`}>
                                            {item.name}
                                            <svg className={`w-3 h-3 transition-transform duration-200 ${activeDropdown === item.key ? "rotate-180" : ""}`} fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" /></svg>
                                            <div className={`absolute bottom-0 left-3 right-3 h-0.5 bg-[#2563EB] transition-all duration-300 ${activeDropdown === item.key ? "opacity-100" : "opacity-0"}`} />
                                        </button>
                                    ) : (
                                        <Link href={item.href || "#"} className={`flex items-center gap-1 px-3 py-2 text-sm transition-all duration-200 font-bold whitespace-nowrap hover:text-[#2563EB] ${activeDropdown === item.key ? "text-[#2563EB]" : "text-[#102A56]"}`}>
                                            {item.name}
                                            <div className={`absolute bottom-0 left-3 right-3 h-0.5 bg-[#2563EB] transition-all duration-300 ${activeDropdown === item.key ? "opacity-100" : "opacity-0"}`} />
                                        </Link>
                                    )}

                                    {/* DROPDOWN MENU CON */}
                                    {item.hasDropdown && item.subItems && (
                                        <div className={`absolute top-full left-0 w-64 bg-white rounded-xl shadow-2xl border border-[#D6EAFE] py-0 z-50 transition-all duration-200 transform origin-top ${activeDropdown === item.key ? "opacity-100 scale-100 translate-y-0" : "opacity-0 scale-95 -translate-y-2 pointer-events-none"}`}>
                                            {item.key === 'yourInfo' && user ? (
                                                <div className="flex flex-col">
                                                    <div className="px-4 py-4 bg-[#F8FCFF] border-b border-[#D6EAFE]">
                                                        <div className="flex items-center gap-2 mb-2">
                                                            <span className="text-[10px] font-bold text-white bg-[#2563EB] px-2 py-0.5 rounded-md uppercase tracking-wider">{user.role}</span>
                                                        </div>
                                                        <p className="text-sm font-bold text-[#102A56] truncate">{user.fullName || "Người dùng"}</p>
                                                        <p className="text-[11px] text-[#5F789A] truncate">{user.email}</p>
                                                    </div>
                                                    <div className="py-1">
                                                        {item.subItems.map((sub) => (
                                                            <Link key={sub.key} href={sub.href} className="w-full text-left px-4 py-2.5 text-sm text-[#102A56] hover:bg-[#EEF6FF] flex items-center gap-3 transition-colors group">
                                                                <svg className="w-4 h-4 text-[#2563EB]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                    {sub.key === 'profile' && <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />}
                                                                    {sub.key === 'history' && <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />}
                                                                    {sub.key === 'prescriptions' && <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />}
                                                                </svg>
                                                                <span className="group-hover:text-[#2563EB]">{sub.name}</span>
                                                            </Link>
                                                        ))}
                                                    </div>
                                                    <div className="border-t border-[#D6EAFE] py-1">
                                                        <button onClick={handleLogout} className="w-full text-left px-4 py-2.5 text-sm text-red-500 hover:bg-red-50 flex items-center gap-2 transition-colors">
                                                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" /></svg>
                                                            {"Đăng xuất"}
                                                        </button>
                                                    </div>
                                                </div>
                                            ) : (
                                                <div className="px-3 py-2">
                                                    <p className="text-[10px] font-bold text-[#5F789A] uppercase tracking-widest px-3 mb-1">
                                                        {item.name}
                                                    </p>
                                                    {item.subItems.map((sub) => (
                                                        <Link key={sub.key} href={sub.href} className="flex items-center gap-2 px-3 py-2.5 text-sm text-[#102A56] hover:text-[#2563EB] hover:bg-[#EEF6FF] rounded-lg transition-all" onClick={() => setActiveDropdown(null)}>
                                                            <div className="w-1.5 h-1.5 rounded-full bg-[#D6EAFE] group-hover:bg-[#2563EB]" />
                                                            {sub.name}
                                                        </Link>
                                                    ))}
                                                </div>
                                            )}
                                        </div>
                                    )}
                                </div>
                            ))}
                        </nav>

                        {/* NÚT MỞ MENU TRÊN ĐIỆN THOẠI */}
                        <button className="lg:hidden p-2" onClick={() => setMobileMenuOpen(!mobileMenuOpen)}>
                            <svg className="w-6 h-6 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                {mobileMenuOpen ? <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" /> : <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />}
                            </svg>
                        </button>
                    </div>

                    {/* HIỂN THỊ MENU MOBILE */}
                    {mobileMenuOpen && (
                        <div className="lg:hidden bg-white border-t border-gray-100 shadow-lg max-h-[80vh] overflow-y-auto">
                            <div className="px-4 py-3 space-y-2">
                                {menuItems.map((item) => (
                                    <div key={item.key} className="space-y-1">
                                        <div className="flex items-center justify-between px-3 py-3 text-sm font-semibold text-gray-700 hover:bg-gray-100 hover:text-black rounded-lg transition-colors cursor-pointer" onClick={() => setActiveDropdown(activeDropdown === item.key ? null : item.key)}>
                                            <Link href={item.href || "#"} className="flex-1">{item.name}</Link>
                                            {item.hasDropdown && <svg className={`w-4 h-4 transition-transform ${activeDropdown === item.key ? "rotate-180" : ""}`} fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" /></svg>}
                                        </div>
                                        {item.hasDropdown && activeDropdown === item.key && (
                                            <div className="ml-4 pl-4 border-l-2 border-black/20 space-y-1">
                                                {item.subItems?.map((sub) => (
                                                    <Link key={sub.key} href={sub.href} className="block px-3 py-2.5 text-sm text-gray-600 hover:text-black" onClick={() => setMobileMenuOpen(false)}>{sub.name}</Link>
                                                ))}
                                            </div>
                                        )}
                                    </div>
                                ))}
                                {!user && (
                                    <div className="pt-4 mt-2 border-t border-gray-100 flex gap-2">
                                        <button onClick={() => openAuthModal("login")} className="flex-1 text-center px-3 py-2.5 text-sm font-semibold text-black border border-black rounded-lg hover:bg-black hover:text-white transition-colors">{"Đăng nhập"}</button>
                                        <button onClick={() => openAuthModal("register")} className="flex-1 text-center px-3 py-2.5 text-sm font-semibold text-white bg-black rounded-lg hover:bg-gray-800 transition-colors">{"Đăng ký"}</button>
                                    </div>
                                )}
                            </div>
                        </div>
                    )}
                </div>

            </header>

            {/* --- KHỐI MODAL ĐĂNG NHẬP / ĐĂNG KÝ (AUTH MODAL) --- */}
            {showAuthModal && (
                <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
                    <div className="absolute inset-0 bg-black/50 backdrop-blur-sm" />
                    <div
                        ref={modalRef}
                        className="relative bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden"
                        style={{ animation: "modalIn 0.3s ease-out" }}
                    >
                        {/* ĐẦU MODAL - HIỆN TIÊU ĐỀ THEO TAB */}
                        <div className="bg-black px-6 py-5 text-white relative overflow-hidden">
                            <div className="absolute -top-10 -right-10 w-32 h-32 bg-white/10 rounded-full" />
                            <h2 className="text-lg font-bold relative z-10 text-white">
                                {authTab === "login" ? "Đăng nhập" : authTab === "register" ? "Đăng ký tài khoản" : "Quên mật khẩu"}
                            </h2>
                            <button onClick={() => { setShowAuthModal(false); resetForm(); }} className="absolute top-4 right-4 w-8 h-8 rounded-full bg-white/10 hover:bg-white/20 flex items-center justify-center transition-colors z-10">
                                <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" /></svg>
                            </button>
                        </div>

                        {/* THANH CHỌN TAB */}
                        <div className="flex border-b border-gray-200">
                            <button onClick={() => { setAuthTab("login"); setAuthError(null); }} className={`flex-1 py-3 text-sm font-semibold transition-colors relative ${authTab === "login" ? "text-black" : "text-gray-400"}`}>
                                {"Đăng nhập"}
                                {authTab === "login" && <span className="absolute bottom-0 left-1/4 right-1/4 h-0.5 bg-black rounded-full" />}
                            </button>
                            <button onClick={() => { setAuthTab("register"); setAuthError(null); }} className={`flex-1 py-3 text-sm font-semibold transition-colors relative ${authTab === "register" ? "text-black" : "text-gray-400"}`}>
                                {"Đăng ký"}
                                {authTab === "register" && <span className="absolute bottom-0 left-1/4 right-1/4 h-0.5 bg-black rounded-full" />}
                            </button>
                        </div>

                        {/* NỘI DUNG FORM */}
                        <div className="px-6 py-5">
                            {authTab === "login" ? (
                                /* FORM ĐĂNG NHẬP */
                                <form onSubmit={handleLogin} className="space-y-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1.5">{"Email"}</label>
                                        <input type="email" required value={loginForm.email} onChange={(e) => setLoginForm({ ...loginForm, email: e.target.value })} placeholder="example@gmail.com" className="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-black/30 outline-none" />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1.5">{"Mật khẩu"}</label>
                                        <input type="password" required value={loginForm.password} onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })} placeholder="••••••••" className="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-black/30 outline-none" />
                                    </div>
                                    <button type="submit" disabled={authLoading} className="w-full btn-premium py-3 rounded-xl">
                                        {authLoading ? "Đang xử lý..." : "Đăng nhập"}
                                    </button>
                                    <div className="text-right">
                                        <button type="button" onClick={() => { setAuthTab("forgot"); setForgotStep(1); }} className="text-xs text-[#2e5bff] hover:underline">Quên mật khẩu?</button>
                                    </div>
                                </form>
                            ) : authTab === "register" ? (
                                /* FORM ĐĂNG KÝ */
                                <form onSubmit={handleRegister} className="space-y-3">
                                    <div>
                                        <label className="block text-xs font-medium text-gray-700 mb-1">Họ và tên</label>
                                        <input type="text" required value={registerForm.fullName} onChange={(e) => setRegisterForm({ ...registerForm, fullName: e.target.value })} placeholder="Nguyễn Văn A" className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-black/30 outline-none" />
                                    </div>
                                    <div>
                                        <label className="block text-xs font-medium text-gray-700 mb-1">Email <span className="text-red-500">*</span></label>
                                        <input type="email" required value={registerForm.email} onChange={(e) => setRegisterForm({ ...registerForm, email: e.target.value })} placeholder="example@gmail.com" className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-black/30 outline-none" />
                                    </div>
                                    <div className="grid grid-cols-2 gap-3">
                                        <div>
                                            <label className="block text-xs font-medium text-gray-700 mb-1">Mật khẩu</label>
                                            <input type="password" required minLength={6} value={registerForm.password} onChange={(e) => setRegisterForm({ ...registerForm, password: e.target.value })} placeholder="••••••••" className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-black/30 outline-none" />
                                        </div>
                                        <div>
                                            <label className="block text-xs font-medium text-gray-700 mb-1">Số điện thoại</label>
                                            <input type="tel" required value={registerForm.phone} onChange={(e) => setRegisterForm({ ...registerForm, phone: e.target.value })} placeholder="09xxxx" className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:ring-2 focus:ring-black/30 outline-none" />
                                        </div>
                                    </div>
                                    <button type="submit" disabled={authLoading} className="w-full btn-premium py-3 rounded-xl mt-2">
                                        {authLoading ? "Đang xử lý..." : "Đăng ký tài khoản"}
                                    </button>
                                </form>
                            ) : (
                                /* LUỒNG QUÊN MẬT KHẨU */
                                <div className="space-y-4">
                                    {forgotStep === 1 ? (
                                        <form onSubmit={handleForgotPassword} className="space-y-4">
                                            <p className="text-sm text-gray-500">Chúng tôi sẽ gửi mã xác thực tới Email của bạn.</p>
                                            <input type="email" required value={forgotEmail} onChange={(e) => setForgotEmail(e.target.value)} placeholder="example@gmail.com" className="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black/30" />
                                            <button type="submit" disabled={authLoading} className="w-full bg-[#2e5bff] text-white py-3 rounded-lg">{authLoading ? "Đang gửi..." : "Tiếp tục"}</button>
                                        </form>
                                    ) : (
                                        <form onSubmit={handleResetPassword} className="space-y-3">
                                            <input type="text" required maxLength={8} value={resetToken} onChange={(e) => setResetToken(e.target.value)} placeholder="Mã xác thực (OTP)" className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm text-center font-bold" />
                                            <input type="password" required value={newPassword} onChange={(e) => setNewPassword(e.target.value)} placeholder="Mật khẩu mới" className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm" />
                                            <input type="password" required value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} placeholder="Xác nhận mật khẩu" className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm" />
                                            <button type="submit" disabled={authLoading} className="w-full bg-[#2e5bff] text-white py-2.5 rounded-lg">Cập nhật mật khẩu</button>
                                            <button type="button" onClick={() => setForgotStep(1)} className="w-full text-xs text-gray-500 hover:underline mt-1">Quay lại nhập Email</button>
                                        </form>
                                    )}
                                </div>
                            )}
                        </div>
                    </div>

                    <style jsx>{`
                        @keyframes modalIn {
                            from { opacity: 0; transform: scale(0.95) translateY(10px); }
                            to { opacity: 1; transform: scale(1) translateY(0); }
                        }
                    `}</style>
                </div>
            )}
        </>
    );
}
