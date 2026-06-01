"use client";

/**
 * FILE: SWRegistration.tsx
 * MÔ TẢ: Thành phần đảm nhận việc đăng ký Service Worker (sw.js) để hỗ trợ tính năng PWA (Progressive Web App).
 * Service Worker giúp ứng dụng có thể hoạt động ngoại tuyến, nhận thông báo đẩy và lưu bộ nhớ đệm (caching).
 */
import { useEffect } from 'react';
import { useToast } from '../../context/ToastContext';

export default function SWRegistration() {
  const { showToast } = useToast();

  useEffect(() => {
    // 1. Kiểm tra xem trình duyệt có hỗ trợ Service Worker hay không
    if ('serviceWorker' in navigator) {
      window.addEventListener('load', () => {
        // 2. Thực hiện đăng ký file sw.js (nằm trong thư mục public)
        navigator.serviceWorker
          .register('/sw.js')
          .then((registration) => {
            console.log('Service Worker đã đăng ký thành công: ', registration);

            // 3. Lắng nghe sự kiện cập nhật phiên bản mới của ứng dụng
            registration.onupdatefound = () => {
              const installingWorker = registration.installing;
              if (installingWorker) {
                installingWorker.onstatechange = () => {
                  // Khi phiên bản mới đã được tải về và cài đặt xong
                  if (installingWorker.state === 'installed') {
                    if (navigator.serviceWorker.controller) {
                      // Thông báo cho người dùng biết có bản cập nhật mới
                      showToast("Ứng dụng đã có bản cập nhật mới. Vui lòng tải lại trang để áp dụng.", "success");
                    }
                  }
                };
              }
            };
          })
          .catch((registrationError) => {
            console.error('Đăng ký Service Worker thất bại: ', registrationError);
          });
      });
    }
  }, [showToast]);

  // Component này không hiển thị gì lên giao diện (Renderless component)
  return null;
}
