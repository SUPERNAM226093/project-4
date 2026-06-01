# Spec: Chat Session Store (MySQL)

## Capability

Persist ephemeral chatbot conversation state in **MySQL** instead of Redis, with equivalent TTL and API behavior for `ChatbotService`.

---

## Requirements

### REQ-CS-001: Table `chat_sessions`

The system SHALL store chat sessions in table `chat_sessions` with columns:

| Column | Type | Notes |
|--------|------|-------|
| `session_id` | VARCHAR(64) PK | Client-provided id |
| `user_id` | BIGINT NULL | From `ChatRequest.userId` when logged in |
| `state_json` | JSON NOT NULL | Serialized conversation state |
| `expires_at` | DATETIME(3) NOT NULL | Sliding expiry |
| `updated_at` | DATETIME(3) NOT NULL | Last write |

Index on `expires_at` for purge queries.

Migration: `V26__create_chat_sessions_table.sql`.

### REQ-CS-002: Store API (internal)

`ChatSessionStore` SHALL provide:

| Method | Behavior |
|--------|----------|
| `getState(sessionId)` | Return `Map` if row exists and `expires_at > now`; else `null` |
| `saveState(sessionId, userId, state)` | Upsert row; set `expires_at = now + TTL` |
| `deleteState(sessionId)` | Delete row by id |

TTL default: **30 minutes**, configurable via `chat.session.ttl-minutes`.

### REQ-CS-003: Fail-fast persistence

If serialize or database save fails, the system SHALL throw `ChatSessionPersistenceException`. `ChatbotService` SHALL return an ERROR intent response — NOT success with lost state.

The system SHALL NOT swallow persistence errors (unlike legacy `RedisService`).

### REQ-CS-004: Session id validation

`saveState` SHALL reject `sessionId` not matching `^[a-zA-Z0-9_-]{8,64}$`.

### REQ-CS-005: Expired session cleanup

A scheduled task SHALL run periodically (default every 10 minutes) and `DELETE` rows where `expires_at < NOW()`.

### REQ-CS-006: No Redis dependency

The `clinic` module SHALL NOT depend on `spring-boot-starter-data-redis` or `RedisService` at runtime.

### REQ-CS-007: Chatbot integration

`ChatbotService.processMessage` SHALL:

1. Load state via `getState(sessionId)`
2. Process message (unchanged intent/booking logic)
3. Save via `saveState(sessionId, userId, state)`

### REQ-CS-008: Client session continuity

`ChatWidget` SHALL persist `sessionId` in `sessionStorage` under key `clinic_chat_session_id` so page refresh within TTL reuses the same session.

---

## Scenarios

### SC-CS-001: New session first message

**Given** no row for `sessionId`  
**When** user sends first chat message  
**Then** new row is created with `state_json` containing `chatHistory` and classified `intent`

### SC-CS-002: Booking flow continuity

**Given** state with `intent=BOOKING` and `bookingStep=SELECT_DOCTOR`  
**When** user sends next message in same `sessionId`  
**Then** `ChatbotService` continues booking without re-classifying from scratch

### SC-CS-003: Expired session

**Given** row with `expires_at` in the past  
**When** `getState` is called  
**Then** returns `null` (new conversation)

### SC-CS-004: DB unavailable on save

**Given** database write fails  
**When** `saveState` is invoked  
**Then** user receives error message; prior in-memory turn is not falsely acknowledged as persisted

---

## Out of scope

- Normalized `chat_messages` table (future change).
- Server-side session listing for admin UI.
- Redis compatibility layer.
