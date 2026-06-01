"use client";

import { useRouter } from "next/navigation";

/**
 * Component Nút Quay Lại (Back).
 * Sử dụng `useRouter` từ thư viện `next/navigation` để thực hiện hành động quay lại trang trước đó trong lịch sử duyệt web.
 * Được thiết kế nhỏ gọn với icon SVG để dùng chung trên nhiều trang.
 */
export default function BackButton() {
    const router = useRouter();
    
    return (
        <button
            onClick={() => router.back()}
            className="flex items-center gap-2 text-sm text-gray-500 hover:text-black transition-colors"
        >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
            Quay lại
        </button>
    );
}
