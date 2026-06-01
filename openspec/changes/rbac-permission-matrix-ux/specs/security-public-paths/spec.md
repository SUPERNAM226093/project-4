# Spec: Security Public Paths (Phase 0) (Đặc tả: Đường dẫn công khai - Phase 0)

## Capability (Khả năng đáp ứng)

Align `RoleUrlAuthorizationFilter` and `SecurityConfig` so URL-based RBAC enforcement is truthful and testable before Permission Matrix UX ships.
(Đồng bộ hóa `RoleUrlAuthorizationFilter` và `SecurityConfig` để việc kiểm soát quyền truy cập theo URL hoạt động chính xác và có thể kiểm thử trước khi triển khai giao diện Ma trận Phân quyền).

---

## Requirements (Các yêu cầu chi tiết)

### REQ-SP-001: Documented public endpoints (Tài liệu hóa endpoint công khai)

The project SHALL maintain `docs/security-public-endpoints.md` listing every URI pattern that bypasses RoleUrl checks, with business justification per entry.
(Hệ thống phải duy trì tài liệu `docs/security-public-endpoints.md` liệt kê mọi URI pattern bỏ qua lớp kiểm tra RoleUrl, kèm theo lý do nghiệp vụ cho từng dòng).

### REQ-SP-002: Minimal PUBLIC_PATHS (Thu hẹp tối đa PUBLIC_PATHS)

`RoleUrlAuthorizationFilter.PUBLIC_PATHS` SHALL contain only patterns required for unauthenticated or explicitly public flows. Protected business APIs SHALL NOT appear in PUBLIC_PATHS unless documented in REQ-SP-001.
(Danh sách `PUBLIC_PATHS` trong Filter chỉ được chứa những đường dẫn thực sự cần công khai hoặc không cần đăng nhập. Các API nghiệp vụ nội bộ tuyệt đối không được đưa vào đây).

### REQ-SP-003: SecurityConfig alignment (Đồng bộ SecurityConfig)

`SecurityConfig` `permitAll()` rules SHALL not grant broader access than documented public endpoints. Authenticated staff operations on protected resources SHALL require authentication and pass through `RoleUrlAuthorizationFilter`.
(Các quy tắc `permitAll()` trong `SecurityConfig` phải khớp và không được rộng hơn tài liệu. Mọi thao tác của nhân viên đã đăng nhập đều phải đi qua bộ lọc kiểm tra quyền).

### REQ-SP-004: ADMIN bypass preserved (Giữ quyền tối cao cho ADMIN)

Role ADMIN SHALL retain full access via existing filter bypass (`ADMIN` role check) without requiring RoleUrl rows.
(Vai trò ADMIN sẽ luôn được bỏ qua kiểm tra và có toàn quyền mà không cần cấu hình các bản ghi trong DB).

### REQ-SP-006: Emergency bypass audit (Kiểm toán các bypass khẩn cấp)

`RoleUrlAuthorizationFilter` contains a hardcoded bypass for URIs containing `room-bookings` or `feature-image`. Phase 0 SHALL either (a) remove this bypass and enforce via RoleUrl rows, or (b) document it in `docs/security-public-endpoints.md` with justification. Undocumented bypasses are not allowed after Phase 0.
(Các bypass cứng trong filter cho `room-bookings` hoặc `feature-image` cần được gỡ bỏ để kiểm soát qua DB hoặc phải được tài liệu hóa rõ ràng lý do).

### REQ-SP-007: Patient / guest flows (Các luồng của Bệnh nhân/Khách vãng lai)

Phase 0 changes SHALL include a checklist of patient-app and guest flows (public doctor list, health package booking POST, online consultation) verified against the final public-endpoint document. No undocumented regression on unauthenticated flows.

### REQ-SP-005: Regression tests (Kiểm thử regression)

Integration tests SHALL verify:
- Tài khoản STAFF không có quyền tạo lịch hẹn (`POST`) sẽ nhận mã lỗi 403.
- Tài khoản STAFF có quyền xem lịch hẹn (`GET`) truy cập thành công sau khi đăng nhập.
- Khách vãng lai gọi các API công khai (như xem danh sách bác sĩ công khai) vẫn thành công.

---

## Scenarios (Các kịch bản kiểm thử GWT)

### SC-SP-001: Protected POST blocked (Chặn POST nếu không có quyền)
- **Given:** Phase 0 được triển khai.
- **And:** Vai trò STAFF không có bản ghi cho phép `POST /appointments/**` trong database.
- **When:** STAFF gửi request tạo lịch hẹn `POST /api/appointments` kèm JWT hợp lệ.
- **Then:** HTTP Status trả về là **403 Forbidden**.

### SC-SP-002: Public guest read (Khách vãng lai đọc thông tin công khai)
- **Given:** API `/api/doctors` được cấu hình công khai cho ứng dụng bệnh nhân.
- **When:** Gửi request `GET /api/doctors` mà không đăng nhập.
- **Then:** Không bị lỗi 403 từ bộ lọc phân quyền.

### SC-SP-003: Truth panel data (Dữ liệu Truth Panel trên UI)
- **Given:** Quản trị viên mở trang ma trận phân quyền.
- **When:** Danh sách bypass được tính toán.
- **Then:** Giao diện hiển thị đúng số lượng và chi tiết các API bypass khớp với tài liệu.

---

## Migration notes (Ghi chú dịch chuyển dữ liệu)

- Kiểm tra lại toàn bộ các đường dẫn trong `PUBLIC_PATHS` cũ (doctors, users, appointments, prescriptions, medical-records, services, rooms, health-packages, specializations, room-bookings, online-consultations, health-package-bookings, prescriptions, users).
- Gỡ bỏ chúng khỏi bypass; chuyển sang cấp quyền thông qua DB RoleUrl hoặc ma trận mặc định cho các vai trò cần sử dụng.
- Lưu ý định dạng `url_pattern` trong DB không sử dụng tiền tố `/api`.
