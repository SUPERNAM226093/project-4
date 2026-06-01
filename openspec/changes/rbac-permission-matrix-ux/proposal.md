# Proposal: RBAC Permission Matrix UX (Đề xuất: Giao diện Ma trận Phân quyền RBAC)

## Summary (Tóm tắt đề xuất)

Cải tiến trải nghiệm cấu hình phân quyền (Role & URL permissions) trên Admin Clinic: thay giao diện nhập tay URL/HTTP method bằng **ma trận module × hành động** (Xem/Thêm/Sửa/Xóa), kèm tab Nâng cao cho Super Admin, trong khi **giữ nguyên** cơ chế `RoleUrl` + `RoleUrlAuthorizationFilter`.

## Problem (Vấn đề hiện tại)

| Vấn đề | Tác động |
|--------|----------|
| Admin phải biết URL pattern (`/doctors/**`) và HTTP verb (GET/POST) | BA/HR không dùng được vì quá kỹ thuật; cấu hình dễ bị sai sót |
| Bảng phẳng chứa hàng trăm dòng phân quyền | Cực kỳ khó quản lý và theo dõi theo từng tính năng |
| UI không phản ánh bypass bảo mật | Checkbox trên UI hiển thị “tắt” quyền nhưng API thực chất vẫn public qua `PUBLIC_PATHS` / `permitAll` |
| Không phân biệt quyền matrix vs thủ công | Rủi ro xóa nhầm các quyền cấu hình thủ công (zombie permissions) |

## Proposed Solution (Giải pháp đề xuất)

1. **Phase 0 (Security):** Thu hẹp `PUBLIC_PATHS` và `SecurityConfig.permitAll` — chỉ giữ lại các endpoint thực sự cần public.
2. **Phase 1 (Data):** Thêm cột phân loại nguồn phân quyền `permission_source` (`MATRIX` hoặc `MANUAL`) và tên module `matrix_module` vào bảng `role_urls`.
3. **Phase 2 (API):** Cung cấp các API `GET/PUT /api/roles/{roleId}/permission-matrix` để chuyển đổi ma trận sang bản ghi `role_urls` tương ứng.
4. **Phase 3 (UI):** Thiết kế lại trang `RoleUrlPage` — hiển thị Tab Chuẩn (dưới dạng **ma trận tích chọn**) + Tab Nâng cao (cấu hình thủ công cho Dev/Super Admin).

## Goals (Mục tiêu dự án)

- Cho phép quản trị viên cấu hình quyền bằng **ngôn ngữ nghiệp vụ** dễ hiểu (Bác sĩ, Lịch hẹn, ...).
- Bảo toàn cấu trúc bảng cơ sở dữ liệu `role_urls` và logic kiểm tra phân quyền (authorization filter) hiện tại của Java Spring Boot.
- Tránh cấp thừa quyền (over-permissioning): khớp chính xác CRUD với HTTP Methods.
- Cung cấp Tab nâng cao cho các trường hợp cấu hình API đặc biệt không nằm trong ma trận chuẩn.

## Non-Goals (Ngoài phạm vi giải quyết)

- Thay thế hoàn toàn cơ chế `@PreAuthorize` của Spring Security trong toàn hệ thống.
- Cấu hình phân quyền cho ứng dụng Mobile của Bệnh nhân.
- Tự động phát hiện module từ OpenAPI (phiên bản v1 dùng danh sách cố định).

## Success Criteria (Tiêu chí thành công)

- [ ] Tài khoản STAFF không có quyền tạo lịch hẹn (`POST /appointments/**`) truy cập API sẽ bị trả về lỗi **403 Forbidden** (sau khi làm sạch bảo mật ở Phase 0).
- [ ] Khi Admin tích chọn "Xem Bác sĩ" cho vai trò, DB tự động sinh ra quyền `GET /doctors/**` với `permission_source=MATRIX`.
- [ ] Việc lưu ma trận chuẩn không ảnh hưởng hay xóa đi các phân quyền thủ công (`MANUAL`).
- [ ] Hiển thị bản so sánh thay đổi (diff preview) chuẩn xác trước khi lưu.
- [ ] Checkbox cột **Tất cả** hoạt động chính xác (bật/tắt nhanh 4 quyền).
- [ ] Thanh menu bên trái (Sidebar) hiển thị đúng theo các quyền Xem (GET) được cấp.

## Risks & Mitigations (Rủi ro & Cách khắc phục)

| Rủi ro | Giải pháp khắc phục |
|------|------------|
| **DB url_pattern bị lệch định dạng `/api` và không `/api`** | Chạy migration để chuẩn hóa dữ liệu cũ |
| Làm ảnh hưởng các API công khai của khách hàng (landing page) | Viết tài liệu lưu trữ và chạy thử E2E luồng không đăng nhập |
| Các ngoại lệ bypass khẩn cấp trong filter | Rà soát kỹ và đưa vào tài liệu bảo mật chính thức |
| Xung đột dữ liệu giữa ma trận và thủ công | Gán nhãn `MANUAL` rõ ràng và đưa ra cảnh báo cảnh báo trên UI |

## Stakeholders (Các bên liên quan)

- **Admin / Quản lý:** Người cấu hình quyền.
- **Dev:** Người lập trình logic API và cơ sở dữ liệu.
- **Người dùng cuối (STAFF/DOCTOR/PATIENT):** Người bị giới hạn quyền sau khi thắt chặt bảo mật.

## References (Tài liệu tham khảo)

- Phiên họp đánh giá của Multi-agent: `rbac-ux-m8k2` (ĐÃ PHÊ DUYỆT CÓ ĐIỀU KIỆN)
- Code liên quan: `RoleUrlPage.tsx`, `RoleUrlAuthorizationFilter.java`, `DataInitializer.java`, `SecurityConfig.java`
