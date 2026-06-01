import type { MetadataRoute } from 'next'

export default function manifest(): MetadataRoute.Manifest {
  return {
    name: 'MedPro - Hệ thống quản lý phòng khám',
    short_name: 'MedPro Clinic',
    description: 'Hệ thống kết nối bệnh nhân và bác sĩ, quản lý lịch khám sức khỏe chuyên nghiệp.',
    start_url: '/',
    display: 'standalone',
    background_color: '#ffffff',
    theme_color: '#7C6EE6',
    icons: [
      {
        src: '/icons/icon-192.png',
        sizes: '192x192',
        type: 'image/png',
        purpose: 'any',
      },
      {
        src: '/icons/icon-512.png',
        sizes: '512x512',
        type: 'image/png',
        purpose: 'any',
      },
    ],
  }
}
