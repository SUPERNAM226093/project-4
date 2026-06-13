
/**
 * Trả về chuỗi ngày hôm nay định dạng YYYY-MM-DD dựa trên múi giờ địa phương.
 */
export const getTodayStr = (): string => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

/**
 * Trả về chuỗi giờ hiện tại định dạng HH:mm.
 */
export const getCurrentTimeStr = (): string => {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
};

/**
 * Trả về chuỗi ngày giờ hiện tại định dạng YYYY-MM-DDThh:mm.
 */
export const getCurrentDateTimeLocalStr = (): string => {
    return `${getTodayStr()}T${getCurrentTimeStr()}`;
};

/**
 * Kiểm tra xem một khung giờ (HH:mm) đã trôi qua so với thời gian hiện tại hay chưa.
 */
export const isPastTime = (dateStr: string, timeStr: string): boolean => {
    const today = getTodayStr();
    if (dateStr < today) return true;
    if (dateStr > today) return false;

    const nowTime = getCurrentTimeStr();
    return timeStr <= nowTime;
};
