# Decision Log — rbac-permission-matrix-ux (Nhật ký quyết định thiết kế)

| ID | Quyết định (Decision) | Phương án thay thế (Alternatives) | Ý kiến phản đối (Objections) | Kết quả giải quyết (Resolution) |
|----|----------|--------------|------------|------------|
| D1 | **Phase 0 trước khi làm Matrix UI** (Thắt chặt bảo mật trước) | Triển khai UI trước | Sợ rằng chỉ làm đẹp bề nổi mà API vẫn hở (Security theater) | **Chấp nhận** — Đưa Phase 0 vào danh sách nhiệm vụ |
| D2 | **Thêm cột `permission_source` + `matrix_module`** | Đặt thẻ ghi chú trong trường description `[MATRIX:]` | Dễ sinh ra các quyền thừa, rác khi xóa/cập nhật (Zombie permissions) | **Chấp nhận** |
| D3 | **Gộp HTTP PATCH vào quyền Sửa (Edit)** | Chỉ dùng PUT | Nguy cơ bị bypass quyền khi gọi PATCH | **Chấp nhận** |
| D4 | **Giữ nguyên logic so khớp của bộ lọc filter** | Lập trình lại cơ chế matcher | Rủi ro gây lỗi hàng loạt các trang đang hoạt động | **Chấp nhận** |
| D5 | **Giao diện ma trận phẳng kết hợp cột Tất cả** | Dạng Accordion thu gọn | Gây tải nhận thức lớn cho người dùng (quá nhiều dòng) | **Chấp nhận** — Đáp ứng yêu cầu của người dùng |
| D6 | **16 module chuẩn + danh sách nâng cao riêng** | Đưa toàn bộ API Controller vào ma trận | Làm phình to phạm vi phát triển | **Chấp nhận** |
| D7 | **Trích xuất Functional Core** | Chỉ viết Service | Không cần thiết ở phiên bản đầu tiên | **Từ chối** — Không làm ở v1 để tránh over-engineering |
| D8 | **Lưu ma trận theo dạng Transactional** | Xóa và lưu thông thường | Trạng thái dữ liệu bị lưu một nửa nếu gặp lỗi giữa chừng | **Chấp nhận** |

**Review session (Phiên đánh giá):** `rbac-ux-m8k2`  
**Disposition (Kết quả):** APPROVED WITH CONDITIONS (Đã phê duyệt có điều kiện)  
**Saturation (Mức độ đồng thuận):** 1 round (3/4 tác vụ đồng ý)  

