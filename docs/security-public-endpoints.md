# Public & Bypass Endpoints (Clinic API)

Tài liệu này liệt kê các URI **không** bị chặn bởi `RoleUrlAuthorizationFilter` hoặc được `SecurityConfig` `permitAll()`.

## RoleUrlAuthorizationFilter — PUBLIC_PATHS

| Pattern | Lý do |
|---------|--------|
| `/api/auth/**` | Đăng nhập / đăng ký |
| `/images/**` | Static uploads |
| `/api/role-urls/by-role` | Sidebar admin load menu (cần JWT; filter skip để tránh deadlock cấu hình) |

**Đã gỡ (v1 matrix):** bypass hàng loạt `/api/doctors/**`, `/api/users/**`, … — quyền thật lấy từ bảng `role_urls`.

**Đã gỡ:** emergency bypass `room-bookings` / `feature-image` trong code filter.

## SecurityConfig — permitAll (guest / patient app)

| Pattern | Method | Lý do |
|---------|--------|--------|
| `/api/auth/**`, `/images/**`, `/rooms/**` | * | Auth, assets |
| `/api/role-urls/by-role` | * | Sidebar |
| `/api/appointments/by-patient/**` | * | Patient self-service |
| `/api/medical-records/by-patient/**` | * | Patient |
| `/api/health-package-bookings/patient/**` | * | Patient |
| `/api/online-consultations/patient/**` | * | Patient |
| `/api/room-bookings/by-user/**` | * | Patient |
| `/api/prescriptions/patient/**` | * | Patient |

| `/api/machine-learning/heart-disease/predict` | * | ML demo |
| `/api/chat` | POST | Chatbot guest |
| `/api/doctors`, `/api/doctors/**` | GET | Danh sách bác sĩ công khai |
| `/api/services`, `/api/services/**` | GET | Dịch vụ công khai |
| `/api/rooms`, `/api/rooms/**` | GET | Phòng công khai |
| `/api/specializations`, `/api/specializations/**` | GET | Chuyên khoa công khai |
| `/api/health-packages`, `/api/health-packages/**` | GET | Gói khám công khai |
| `/api/hospitals`, `/api/hospitals/**` | GET | Bệnh viện công khai |
| `/api/login`, `/api/register` | GET | Legacy |
| `/api/health-package-bookings` | POST | Đặt gói guest |
| `/api/health-package-bookings/*/cancel` | POST | Hủy guest |
| `/api/online-consultations` | POST | Đặt tư vấn guest |
| `/api/online-consultations/**` | GET | Xem tư vấn guest |
| `/api/online-consultations/*/cancel` | POST | Hủy guest |

## ADMIN bypass

JWT role `ADMIN` → filter cho phép mọi path (không cần row `role_urls`).

## Authenticated enforcement

Khi có JWT và role ≠ ADMIN: filter kiểm tra `role_urls` (pattern không prefix `/api` trong DB; filter match cả `/api` + pattern).
