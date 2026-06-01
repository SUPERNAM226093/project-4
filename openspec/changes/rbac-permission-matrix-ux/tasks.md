# Tasks: RBAC Permission Matrix UX (Nhiệm vụ: Giao diện ma trận phân quyền)

> Change: `rbac-permission-matrix-ux`  
> Schema: spec-driven  
> **Thứ tự bắt buộc:** Phase 0 (Bảo mật) → 1 (Model dữ liệu) → 2 (API backend) → 3 (UI frontend) → (4 optional - Sidebar)

---

## Phase 0 — Security Hardening (Thắt chặt bảo mật nền tảng)

- [x] **T0.0** Flyway `V24__normalize_role_url_patterns.sql` — chuẩn hóa `/api/foo/**` → `/foo/**` để đồng nhất định dạng.
- [x] **T0.1** Tạo `docs/security-public-endpoints.md` — tài liệu hóa danh sách endpoint công khai + lý do nghiệp vụ.
- [x] **T0.2** Rà soát `RoleUrlAuthorizationFilter.PUBLIC_PATHS` — gỡ bỏ các đường dẫn nghiệp vụ cần bảo vệ khỏi bypass cứng.
- [x] **T0.3** Rà soát `SecurityConfig.permitAll()` — đồng bộ với tài liệu, thu hẹp các quy tắc cho phép mọi truy cập (permitAll).
- [ ] **T0.4** Cập nhật `DataInitializer` nếu các role mặc định thiếu quyền sau khi gỡ bypass — *kiểm tra lúc chạy ứng dụng*.
- [x] **T0.5** Viết integration test cho Filter: SC-SP-001, SC-SP-002 (`RoleUrlAuthorizationFilterTest`).

---

## Phase 1 — Data Model (Cập nhật Cấu trúc Dữ liệu)

- [x] **T1.1** Flyway `V25__role_urls_permission_source.sql`: thêm cột `permission_source`, `matrix_module` và index tương ứng.
- [x] **T1.2** Cập nhật entity `RoleUrl` trong Spring Boot, cùng các lớp DTO `RoleUrlRequest`/`RoleUrlResponse`.
- [x] **T1.3** Chạy Migration backfill dữ liệu cũ: chuyển các bản ghi cũ sang dạng `MANUAL`, `matrix_module=null` (Mặc định).
- [ ] **T1.4** Unit test tìm kiếm quyền theo `(roleId, matrix_module, permission_source)`.

---

## Phase 2 — Backend Matrix API (Lập trình API Phân quyền)

- [x] **T2.1** Tạo `PermissionModuleCatalog` (liệt kê 16 module chuẩn) và API GET `/api/permission-matrix/catalog`.
- [x] **T2.2** `PermissionMatrixService`: Dịch các ô tích chọn sang các bản ghi DB; đảo ngược khi GET; bọc `@Transactional` cho PUT.
- [x] **T2.3** `PermissionMatrixController`: các endpoint GET/PUT `/api/roles/{roleId}/permission-matrix`.
- [x] **T2.4** Đưa ra cảnh báo: `CONFLICT_MANUAL_MATRIX`, `BYPASS_ACTIVE` (xung đột phân quyền ma trận và thủ công).
- [x] **T2.5** Bảo vệ các endpoint API cấu hình quyền — chỉ cho phép vai trò ADMIN gọi.
- [x] **T2.6** Unit tests: SC-PM-001, SC-PM-003 (`PermissionMatrixServiceTest`).

---

## Phase 3 — Admin UI (Giao diện Quản trị viên)

- [x] **T3.1** Tái cấu trúc `RoleUrlPage.tsx`: Thêm dropdown chọn vai trò, nút Lưu, xử lý deep-link, phân chia Tabs Chuẩn / Nâng cao.
- [x] **T3.2** Tạo component `PermissionMatrixTable` — bảng tích chọn ma trận:
  - Các cột: STT, Tên tài nguyên, Mã tài nguyên, Xem, Thêm, Sửa, Xóa, **Tất cả**.
  - Xử lý checkbox master "Tất cả" của từng hàng cùng trạng thái lấp lửng (indeterminate).
  - Hiển thị dòng phân cách nhóm module từ catalog API.
- [x] **T3.2a** Thêm ô **Tìm nhanh module** để lọc nhanh ở frontend.
- [x] **T3.2b** Nút **Quản lý cột** để ẩn/hiện cột Mã tài nguyên.
- [x] **T3.3** Tải ma trận và catalog tự động khi đổi Role.
- [x] **T3.4** Hộp thoại xem trước các thay đổi (diff preview) trước khi lưu.
- [x] **T3.5** Hiển thị Truth panel — các cảnh báo từ API.
- [x] **T3.6** Tab Nâng cao — giữ nguyên bảng nhập tay URL cũ; hiển thị nhãn `MANUAL` để phân biệt.
- [x] **T3.7** Thêm các khóa đa ngôn ngữ i18n mới `roleUrl.matrix.*` (tiếng Việt/Anh).
- [ ] **T3.8** Danh sách kiểm thử thủ công (manual test checklist).

---

## Phase 4 — Sidebar (Optional - Trình đơn bên cạnh)

- [ ] **T4.1** Giảm tải việc viết cứng (hardcode) vai trò STAFF/DOCTOR trong `Sidebar.tsx` bằng cách đọc từ API role-urls.
- [ ] **T4.2** Thêm ghi chú cho các quy tắc nghiệp vụ đặc biệt còn phải giữ hardcode.

---

## Verification Checklist (Danh sách kiểm tra & Xác thực)

- [x] `compileJava` thành công (phần backend Java).
- [ ] Lệnh kiểm thử `mvn -f clinic test` pass (hiện đang bị nghẽn do một số lỗi compile ở các module khác).
- [ ] SC-SP-001 hoạt động tốt (chặn POST nếu không được cấp quyền).
- [x] SC-PM-001, SC-PM-003 hoạt động tốt (đã viết xong Unit tests backend).
- [ ] Chọn vai trò STAFF → Tích chọn Xem Bác sĩ → Lưu → Tải lại trang → Checkbox vẫn được tích chọn.
- [ ] Tích chọn **Tất cả** hàng "Lịch hẹn" → 4 ô quyền CRUD bật → Lưu → Kiểm tra DB có đủ GET/POST/PUT/PATCH/DELETE.
- [ ] Gõ tìm kiếm "bác sĩ" → bảng chỉ hiển thị dòng bác sĩ và STT cập nhật lại từ 1.
- [ ] Tab Nâng cao: thêm một quyền thủ công (MANUAL) → Lưu ma trận ở tab Chuẩn → Kiểm tra quyền MANUAL kia vẫn được giữ lại.
- [ ] Đăng nhập tài khoản STAFF: các mục trên Sidebar hiển thị khớp với quyền Xem (GET) được cấp.
- [ ] Tài khoản ADMIN vẫn có toàn quyền truy cập (không bị ảnh hưởng).

---

## Deferred / v2 (Để lại phiên bản tiếp theo)

- Khóa lạc quan (Optimistic locking) sử dụng cột `version` của bảng vai trò đề phòng lưu đè đồng thời.
- Tự động sinh danh mục module dựa trên OpenAPI spec.

---

## Implementation Notes (Ghi chú lập trình)

| File tác động | Phase |
|------|-------|
| `clinic/.../RoleUrlAuthorizationFilter.java` | Phase 0 |
| `clinic/.../SecurityConfig.java` | Phase 0 |
| `clinic/.../entity/RoleUrl.java` | Phase 1 |
| `clinic/.../permissionmatrix/*` | Phase 2 |
| `admin/.../RoleUrlPage.tsx` | Phase 3 |

**Lưu ý cực kỳ quan trọng:** Tuyệt đối không thay đổi hay viết lại logic so khớp đường dẫn `AntPathMatcher` bên trong filter (chỉ được cập nhật danh sách `PUBLIC_PATHS` và cấu hình bảo mật).



