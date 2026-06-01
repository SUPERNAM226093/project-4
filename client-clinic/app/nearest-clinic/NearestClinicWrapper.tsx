"use client";

import dynamic from "next/dynamic";

const NearestClinicContent = dynamic(
  () => import("./NearestClinicContent"),
  { 
    ssr: false, 
    loading: () => (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-xl text-gray-600">Đang tải bản đồ...</p>
      </div>
    ) 
  }
);

/**
 * Component bọc (Wrapper) cho giao diện bản đồ.
 * Sử dụng tính năng `dynamic` của Next.js với thuộc tính `ssr: false` để tắt Server-Side Rendering.
 * Lý do: Thư viện `react-leaflet` cần truy cập vào đối tượng `window` của trình duyệt để tính toán và render bản đồ,
 * nếu chạy trên server (Node.js) sẽ gây ra lỗi `window is not defined`.
 */
export default function NearestClinicWrapper() {
  return <NearestClinicContent />;
}
