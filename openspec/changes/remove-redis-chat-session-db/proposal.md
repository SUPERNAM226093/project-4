# Proposal: Remove Redis — Chat Session in MySQL

## Summary

Loại bỏ hoàn toàn **Redis** khỏi stack clinic. Trạng thái phiên chatbot (trước đây key `chat:session:{id}` trên Redis) chuyển sang bảng MySQL **`chat_sessions`** với cột JSON và TTL 30 phút. Giữ nguyên contract API `POST /api/chat` và luồng `ChatbotService` / `BookingIntentHandler`.

## Problem

| Vấn đề | Tác động |
|--------|----------|
| Redis chỉ phục vụ 1 use case (chat state) | Dev local phải cài thêm service; Docker stack phức tạp |
| `RedisService.saveState` nuốt lỗi | Mất ngữ cảnh hội thoại im lặng khi Redis lỗi |
| `ChatWidget` tạo `sessionId` mới mỗi F5 | User mất luồng BOOKING dù Redis/DB còn data |
| Không có spec OpenSpec trước khi code | Khó trace quyết định kiến trúc |

## Proposed Solution (Option A — đã chọn sau multi-agent review)

1. **Flyway V26:** Tạo `chat_sessions` (`session_id`, `user_id`, `state_json`, `expires_at`, `updated_at`).
2. **`ChatSessionStore`:** Thay `RedisService` — serialize `Map<String,Object>` ↔ JSON; sliding TTL; fail-fast khi persist lỗi.
3. **`ChatSessionCleanupTask`:** `@Scheduled` purge `expires_at < NOW()`.
4. **Gỡ dependency:** `spring-boot-starter-data-redis`, config `spring.data.redis.*`, README/scripts Redis.
5. **FE:** `sessionStorage` giữ `sessionId` trong tab.

## Alternatives Considered

| Option | Verdict |
|--------|---------|
| **A** JSON 1 bảng | ✅ Chọn — dễ triển khai, khớp state Map hiện tại |
| **B** Chuẩn hóa `chat_messages` | Phase 2 — khi cần lịch sử/admin audit |
| **C** Redis + DB dual-write | ❌ Từ chối — split-brain |
| **D** In-memory only | ❌ Từ chối — mất state khi restart |

## Goals

- Một nguồn persistence: **MySQL** (đã bắt buộc cho app).
- Luồng đặt lịch chatbot **không reset** giữa các lượt POST trong TTL.
- Local dev: không cần `redis-server`.
- Lỗi lưu session → phản hồi rõ ràng cho user (không 200 + mất state).

## Non-Goals

- Lưu vĩnh viễn toàn bộ lịch sử chat cho admin (Option B).
- Thay đổi logic intent/booking handlers.
- Session đăng nhập JWT (vẫn stateless).

## Success Criteria

- [x] Không còn `RedisService` / `spring-data-redis` trong `clinic/`.
- [x] Flyway `V26__create_chat_sessions_table.sql` áp dụng được.
- [x] `POST /api/chat` lưu/đọc state qua `ChatSessionStore`.
- [x] TTL ~30 phút (config `chat.session.ttl-minutes`).
- [x] `ChatWidget` persist `sessionId` qua `sessionStorage`.
- [ ] Integration test E2E chat booking multi-step (khuyến nghị bổ sung).

## References

- Multi-agent review: Skeptic / Guardian / Advocate → Option A
- Code: `ChatbotService`, `ChatSessionStore`, `V26__create_chat_sessions_table.sql`
