# Tasks: Remove Redis — Chat Session in MySQL

> Change: `remove-redis-chat-session-db`  
> Schema: spec-driven  
> **Trạng thái:** Implementation đã hoàn thành trước khi bổ sung spec (retroactive documentation).

---

## Phase 1 — Database & domain

- [x] **T1.1** Flyway `V26__create_chat_sessions_table.sql`
- [x] **T1.2** Entity `ChatSession` + `ChatSessionRepository`
- [x] **T1.3** `ChatSessionPersistenceException`

## Phase 2 — Application layer

- [x] **T2.1** `ChatSessionStore` (serialize/deserialize, TTL, validation)
- [x] **T2.2** `ChatSessionCleanupTask` — scheduled purge
- [x] **T2.3** `ChatbotService` — inject store, fail-fast on persist error
- [x] **T2.4** `application.properties` — `chat.session.ttl-minutes`, remove Redis config

## Phase 3 — Remove Redis

- [x] **T3.1** Delete `RedisService.java`, `RedisServiceTest.java`
- [x] **T3.2** Remove `spring-boot-starter-data-redis` from `build.gradle`

## Phase 4 — Frontend & docs

- [x] **T4.1** `ChatWidget` — `sessionStorage` for `sessionId`
- [x] **T4.2** README — bỏ Redis khỏi stack / yêu cầu cài đặt
- [x] **T4.3** `.env.example`, `scripts/dev-local.sh` — bỏ `REDIS_HOST`

## Phase 5 — Tests

- [x] **T5.1** `ChatSessionStoreTest` — save/get/validation
- [x] **T5.2** `ChatbotServiceTest` — mock `ChatSessionStore`, update `IntentClassifier` mocks
- [ ] **T5.3** Integration test: multi-step BOOKING qua `/api/chat` (khuyến nghị)

---

## Verification checklist

```bash
# Sau bootRun, gửi 2 tin chat cùng sessionId → 1 row trong DB
mysql -u root -p clinic -e "SELECT session_id, expires_at FROM chat_sessions LIMIT 5;"

# Không còn redis trong classpath
cd clinic && ./gradlew dependencies | grep -i redis  # expect empty
```

---

If `workflow_id` present → call `workflow_next(workflow_id, action: "Ok")`.  
If standalone → run `/opsx-apply remove-redis-chat-session-db` hoặc `/opsx-verify remove-redis-chat-session-db` để xác nhận trước archive.
