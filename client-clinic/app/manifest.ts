import type { MetadataRoute } from 'next'

export default function manifest(): MetadataRoute.Manifest {
  return {
    name: 'MedPro - Đặt khám nhanh',
    short_name: 'MedPro',
    description: 'Đặt lịch hẹn khám bệnh dễ dàng, khám từ xa qua video call, quản lý hồ sơ sức khỏe cá nhân.',
    start_url: '/',
    scope: '/',
    display: 'standalone',
    orientation: 'portrait',
    background_color: '#042f2e',
    theme_color: '#0f766e',
    categories: ['health', 'medical', 'lifestyle'],
    icons: [
      {
        src: '/icons/icon-192.png',
        sizes: '192x192',
        type: 'image/png',
        purpose: 'any',
      },
      {
        src: '/icons/icon-192.png',
        sizes: '192x192',
        type: 'image/png',
        purpose: 'maskable',
      },
      {
        src: '/icons/icon-512.png',
        sizes: '512x512',
        type: 'image/png',
        purpose: 'any',
      },
      {
        src: '/icons/icon-512.png',
        sizes: '512x512',
        type: 'image/png',
        purpose: 'maskable',
      },
      {
        src: '/icons/apple-touch-icon.png',
        sizes: '180x180',
        type: 'image/png',
        purpose: 'any',
      },
    ],
    shortcuts: [
      {
        name: 'Đặt lịch khám',
        url: '/specialization',
        description: 'Đặt lịch khám tại phòng khám',
      },
      {
        name: 'Tư vấn trực tuyến',
        url: '/video-call/booking',
        description: 'Đặt lịch tư vấn qua video call',
      },
    ],
  }
}
