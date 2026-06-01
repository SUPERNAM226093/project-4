import { Metadata } from "next";
import NearestClinicWrapper from "./NearestClinicWrapper";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import BackButton from "./BackButton";

export const metadata: Metadata = {
  title: "Tìm cơ sở y tế gần bạn | Clinic",
  description: "Tìm và xem khoảng cách từ vị trí của bạn đến các cơ sở y tế của chúng tôi.",
};

/**
 * Trang "Tìm cơ sở y tế gần bạn".
 * Sử dụng cấu trúc layout chung của ứng dụng bao gồm Navbar, Footer và nút BackButton.
 * Toàn bộ logic hiển thị bản đồ và tính toán khoảng cách được đóng gói trong NearestClinicWrapper
 * để xử lý việc tải bất đồng bộ (CSR - Client Side Rendering) do React-Leaflet yêu cầu đối tượng `window`.
 */
export default function NearestClinicPage() {
  return (
    <>
      <Navbar />
      <div className="bg-white min-h-screen pt-36">
        <div className="max-w-6xl mx-auto px-4 mb-4">
          <BackButton />
        </div>
        <NearestClinicWrapper />
      </div>
      <Footer />
    </>
  );
}
