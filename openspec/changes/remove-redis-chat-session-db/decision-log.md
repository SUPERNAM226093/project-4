# Decision Log: Remove Redis — Chat Session in MySQL

## 2026-05-22 — Multi-agent review (pre-implementation)

**Question:** Thay Redis bằng persistence nào cho chatbot session?

| Role | Verdict |
|------|---------|
| Skeptic | A với safeguards (fail-fast, purge, validate sessionId); reject C/D |
| Guardian | A — 1 Flyway, bỏ redis dependency; reject dual-write |
| Advocate | UX dài hạn B > A; triển khai ngay **A + sessionStorage** |

**Decision:** **Option A** — `chat_sessions` + JSON `state_json` + TTL job.

---

## 2026-05-22 — Implementation note

Code merged trước khi tạo OpenSpec artifacts (process gap). Artifacts trong `openspec/changes/remove-redis-chat-session-db/` được bổ sung **retroactive** để align spec-driven workflow.

**Disposition:** `implemented` — tasks T1–T4, T5.1–T5.2 done; T5.3 integration test pending.

---

## Deferred

- **Option B** (`chat_messages` normalized) — khi product yêu cầu admin audit / patient chat history UI.
