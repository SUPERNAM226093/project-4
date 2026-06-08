/**
 * A lightweight localization utility for translating labels, menu items, and statuses in the Admin Portal.
 */
const translations: Record<string, string> = {
    // Sidebar Menu Items
    'menu.dashboard': 'Tổng quan',
    'menu.users': 'Người dùng',
    'menu.roles': 'Vai trò & Quyền',
    'menu.doctors': 'Bác sĩ',
    'menu.specializations': 'Chuyên khoa',
    'menu.services': 'Dịch vụ & Phòng',
    'menu.registrations': 'Đặt phòng',
    'menu.serviceregistrations': 'Đăng ký dịch vụ',
    'menu.serviceRegistrations': 'Đăng ký dịch vụ',
    'menu.healthpackages': 'Gói khám sức khỏe',
    'menu.healthPackages': 'Gói khám sức khỏe',
    'menu.healthpackagebookings': 'Đăng ký gói khám',
    'menu.healthPackageBookings': 'Đăng ký gói khám',
    'menu.onlineconsultations': 'Tư vấn trực tuyến',
    'menu.onlineConsultations': 'Tư vấn trực tuyến',
    'menu.appointments': 'Lịch hẹn',
    'menu.medicalrecords': 'Hồ sơ bệnh án',
    'menu.medicalRecords': 'Hồ sơ bệnh án',
    'menu.prescriptions': 'Đơn thuốc',
    'menu.roleurls': 'Đường dẫn Role',
    'menu.profile': 'Hồ sơ cá nhân',

    // Gender translations
    'gender.male': 'Nam',
    'gender.female': 'Nữ',
    'gender.other': 'Khác',

    // Status translations
    'status.pending': 'Chờ duyệt',
    'status.confirmed': 'Đã xác nhận',
    'status.completed': 'Hoàn thành',
    'status.cancelled': 'Đã hủy',
    'status.approved': 'Đã phê duyệt',
    'status.rejected': 'Đã từ chối',
    'status.active': 'Đang hoạt động',
    'status.inactive': 'Ngưng hoạt động',
    'status.paid': 'Đã thanh toán',
    'status.unpaid': 'Chưa thanh toán',
};

export function t(key: string, defaultValue?: string): string {
    if (!key) return defaultValue || '';
    const lowerKey = key.toLowerCase();
    if (translations[lowerKey] !== undefined) {
        return translations[lowerKey];
    }
    // Fallback: If it's a dotted path like status.pending, try resolving the last part as human readable
    if (key.includes('.')) {
        const parts = key.split('.');
        const lastPart = parts[parts.length - 1];
        // Capitalize the first letter
        return lastPart.charAt(0).toUpperCase() + lastPart.slice(1);
    }
    return defaultValue || key;
}
