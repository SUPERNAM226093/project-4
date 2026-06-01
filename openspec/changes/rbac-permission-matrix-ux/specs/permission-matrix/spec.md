# Spec: Permission Matrix (Đặc tả: Ma trận phân quyền)

## Capability (Khả năng đáp ứng)

Admin configures role permissions using a **module × action** matrix; backend translates to `role_urls` records without changing authorization filter semantics.
(Quản trị viên cấu hình quyền bằng ma trận **module × hành động**; Backend tự động dịch sang các dòng trong bảng `role_urls` mà không làm thay đổi logic kiểm tra quyền hiện tại).

---

## Requirements (Các yêu cầu chi tiết)

### REQ-PM-001: Module catalog (Danh mục Module)

The system SHALL expose a fixed catalog of matrix modules (16 standard modules per design-brief) each mapping to exactly one canonical `urlPattern` prefix `/{key}/**` **without** `/api` prefix.
(Hệ thống phải trả về danh mục gồm 16 module chuẩn, mỗi module trỏ tới một tiền tố `urlPattern` duy nhất dạng `/{key}/**` và không chứa tiền tố `/api`).

### REQ-PM-001a: Pattern canonicalization (Chuẩn hóa định dạng URL trước)

Before matrix PUT is enabled, existing `role_urls.url_pattern` values SHALL be normalized to the canonical form (no `/api` prefix). The filter already matches both forms at runtime, but matrix translation and Sidebar mapping assume canonical paths. Include Flyway data migration `V24__normalize_role_url_patterns.sql` (or equivalent) to rewrite `/api/{module}/**` → `/{module}/**` where applicable.
(Trước khi kích hoạt API cập nhật ma trận, dữ liệu cũ trong cột `url_pattern` của bảng `role_urls` cần được đưa về dạng chuẩn (bỏ prefix `/api`). Sử dụng Flyway migration `V24` để chạy chuẩn hóa này).

### REQ-PM-002: Action translation (Bản đồ dịch hành động sang HTTP Methods)

For each enabled action on a module, the system SHALL create `RoleUrl` rows:
(Với mỗi ô tích chọn trên giao diện, hệ thống sẽ tạo các dòng tương ứng trong DB):

| Hành động (Action) | Phương thức HTTP tương ứng (Methods) |
|--------|---------|
| Xem (view) | GET |
| Thêm (create) | POST |
| Sửa (edit) | PUT, PATCH |
| Xóa (delete) | DELETE |

### REQ-PM-003: Permission source (Nguồn gốc phân quyền)

Every `RoleUrl` row SHALL have `permission_source` ∈ {`MANUAL`, `MATRIX`}. Rows created by matrix API SHALL set `matrix_module` to the module key.
(Mỗi bản ghi phân quyền phải phân loại rõ nguồn gốc: `MATRIX` nếu sinh ra từ ma trận, `MANUAL` nếu nhập thủ công ở tab Nâng cao. Dòng `MATRIX` phải đi kèm tên module ở cột `matrix_module`).

### REQ-PM-004: Matrix replace scope (Phạm vi ghi đè dữ liệu)

PUT `/api/roles/{roleId}/permission-matrix` SHALL delete only rows where `permission_source = MATRIX` and `matrix_module` is in the request payload, then insert new MATRIX rows. Rows with `MANUAL` SHALL NOT be deleted.
(Khi lưu ma trận, hệ thống chỉ được xóa và ghi đè những dòng có `permission_source = MATRIX` tương ứng. Các dòng nhập thủ công `MANUAL` tuyệt đối không được xóa).

Modules **omitted** from the PUT body SHALL leave existing MATRIX rows unchanged (partial update). The Admin UI SHALL send the **full** 16-module state on every save to avoid accidental partial retention.
(Các module không có trong payload gửi lên sẽ được giữ nguyên. UI cần gửi toàn bộ trạng thái của cả 16 module lên mỗi khi lưu).

### REQ-PM-008: Module catalog API (API trả về danh mục)

GET `/api/permission-matrix/catalog` (or equivalent) SHALL return module groups, keys, labels (i18n keys), and canonical paths for rendering the matrix without hardcoding in the frontend.

### REQ-PM-009: Checkbox matrix UI (Giao diện bảng ma trận - Tab Chuẩn)

The Admin UI SHALL render permissions as a **table matrix** aligned with enterprise RBAC patterns:
(UI phân quyền được thiết kế trực quan dưới dạng bảng):

| Cột trên UI | Ánh xạ tới |
|-----------|---------|
| STT | Số thứ tự dòng sau khi lọc tìm kiếm |
| Tên tài nguyên | Nhãn tiếng Việt của Module |
| Mã tài nguyên | Mã Key của Module (có thể ẩn/hiện) |
| Xem / Thêm / Sửa / Xóa | Checkbox tương ứng các quyền GET / POST / PUT-PATCH / DELETE |
| Tất cả | Checkbox chọn nhanh cả dòng |

**Row master ("Tất cả") behavior (Hành vi của cột Chọn tất cả):**
- Tích chọn: Bật toàn bộ 4 ô quyền còn lại của dòng.
- Bỏ tích: Tắt toàn bộ 4 ô quyền còn lại của dòng.
- Nếu chỉ chọn từ 1 đến 3 quyền: Ô "Tất cả" hiển thị ở trạng thái lấp lửng (indeterminate - dấu gạch ngang).

**Search (Tìm kiếm):** Cho phép gõ tìm kiếm nhanh theo Tên hoặc Mã module ở phía Client.

### REQ-PM-005: Transactionality (Tính toàn vẹn giao dịch)

PUT matrix SHALL execute in a single database transaction. On failure, no partial MATRIX state SHALL remain.
(Thao tác lưu ma trận phải được bọc trong một Transaction duy nhất. Nếu xảy ra bất kỳ lỗi gì, toàn bộ tiến trình sẽ rollback, không để lại dữ liệu rác).

### REQ-PM-006: GET matrix state (Lấy trạng thái ma trận)

GET `/api/roles/{roleId}/permission-matrix` SHALL return:
- `modules`: Map các module và trạng thái 4 quyền CRUD.
- `manualPermissions`: Danh sách các quyền cấu hình thủ công.
- `warnings`: Các cảnh báo xung đột (như trùng lặp quyền, hoặc quyền đã được bypass mở công khai).

### REQ-PM-007: Authorization on matrix API (Phân quyền gọi API cấu hình)

Only users with role ADMIN (or existing admin-only role-url rules) SHALL call matrix GET/PUT endpoints.
(Chỉ tài khoản có quyền ADMIN mới được phép gọi các API cấu hình ma trận này).

---

## Scenarios (Các kịch bản kiểm thử GWT)

### SC-PM-001: Save matrix for STAFF on doctors view-only (Lưu ma trận chỉ có quyền xem bác sĩ)
- **Given (Giả sử):** Role STAFF chưa có cấu hình ma trận cho module `doctors`.
- **When (Khi):** Admin lưu ma trận cấu hình `doctors.view=true` (các quyền khác = false).
- **Then (Thì):** Database chỉ tạo duy nhất một dòng MATRIX: `GET /doctors/**`.
- **And (Và):** Các dòng quyền MANUAL không bị ảnh hưởng.

### SC-PM-002: Upgrade edit enables PATCH (Quyền sửa bao gồm cả PATCH)
- **Given:** STAFF có quyền `doctors.edit=true`.
- **When:** Gửi request `PATCH /api/doctors/1` kèm JWT của STAFF.
- **Then:** Bộ lọc bảo mật cho phép truy cập (không trả về 403).

### SC-PM-003: Manual row survives matrix save (Quyền thủ công không bị ghi đè)
- **Given:** Có sẵn một bản ghi thủ công `POST /custom/**` (MANUAL).
- **When:** Admin lưu cấu hình ma trận cho module `appointments`.
- **Then:** Dòng MANUAL `POST /custom/**` vẫn tồn tại nguyên vẹn.

### SC-PM-004: Conflict warning (Cảnh báo xung đột)
- **Given:** Có quyền thủ công `GET /doctors/salaries/**` (MANUAL).
- **And:** Ma trận cấu hình tắt quyền xem bác sĩ `doctors.view=false`.
- **When:** Gọi API lấy ma trận quyền.
- **Then:** Trả về cảnh báo có sự chồng chéo/xung đột ở module `doctors`.

---

## API Contract (sketch)

### GET `/api/roles/{roleId}/permission-matrix`

Response `200`:

```json
{
  "roleId": 2,
  "roleName": "STAFF",
  "modules": {
    "doctors": { "view": true, "create": false, "edit": false, "delete": false }
  },
  "manualPermissions": [
    { "id": 99, "urlPattern": "/custom/**", "httpMethod": "GET", "description": "..." }
  ],
  "warnings": [
    { "code": "BYPASS_ACTIVE", "message": "GET /api/doctors/** is public via SecurityConfig" }
  ]
}
```

### PUT `/api/roles/{roleId}/permission-matrix`

Request body: `{ "modules": { ... } }`  
Response `200`: same shape as GET after apply.

Errors: `404` role not found, `400` unknown module key, `403` forbidden.
