# Hệ Thống Phòng Khám Đa Khoa (MedPro DATN)

Hệ thống phòng khám tích hợp chatbot RAG AI gồm Backend (Spring Boot), Client bệnh nhân (Next.js), Admin (Vite + React), ML dự đoán tim mạch (Python Flask) và MySQL. **Chạy trực tiếp trên máy local** — không dùng Docker.

---

## Kiến trúc & cổng mặc định

| Dịch vụ | Công nghệ | URL local |
| :--- | :--- | :--- |
| **Backend API** | Java 17 + Gradle | http://localhost:8080 |
| **Admin** | Vite + React | http://localhost:3000 |
| **Client (bệnh nhân)** | Next.js | http://localhost:5173 |
| **ML Predictor** | Flask | http://localhost:5000 |
| **MySQL** | 8.x | `localhost:3306` (gồm phiên chatbot `chat_sessions`) |

---

## Yêu cầu cài đặt

| Thành phần | Phiên bản gợi ý |
| :--- | :--- |
| **JDK** | 17 (`openjdk-17-jdk`) |
| **Node.js** | 20+ |
| **Python** | 3.10+ |
| **MySQL** | 8.0 |

Ubuntu/Debian:

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk nodejs npm python3 python3-venv mysql-server
```

---

## Khởi chạy nhanh

### 1. Biến môi trường

```bash
cp .env.example .env
# Chỉnh CLINIC_DB_PASS nếu MySQL root của bạn khác "root"
```

### 2. Database (lần đầu)

Đảm bảo MySQL đang chạy:

```bash
sudo systemctl start mysql
chmod +x scripts/setup-db.sh scripts/dev-local.sh
./scripts/setup-db.sh
```

Script tạo database `clinic` và import `clinic/database/init.sql` (dữ liệu + tài khoản mẫu).

### 3. Chạy các dịch vụ

Mở **4 terminal** tại thư mục gốc dự án:

```bash
./scripts/dev-local.sh backend   # Spring Boot
./scripts/dev-local.sh ml        # Flask ML
./scripts/dev-local.sh admin     # Admin UI
./scripts/dev-local.sh client    # Client UI
```

Hoặc dùng **tmux** (một lệnh):

```bash
./scripts/dev-local.sh all
tmux attach -t clinic-dev
```

### 4. Truy cập

- **Admin:** http://localhost:3000  
- **Client:** http://localhost:5173  
- **API:** http://localhost:8080  

---

## Tài khoản mẫu

| Vai trò | Email | Mật khẩu |
| :--- | :--- | :--- |
| Admin | `admin@gmail.com` | `123456` |
| Bác sĩ | `doctor@gmail.com` | `123456` |
| Bệnh nhân | `test@gmail.com` | `123456` |

---

## Chạy từng phần thủ công

**Backend** (thư mục `clinic/`, đọc `.env` từ thư mục gốc nếu chạy qua script):

```bash
cd clinic
export $(grep -v '^#' ../.env | xargs) 2>/dev/null || true
./gradlew bootRun
```

**Admin:**

```bash
cd admin/admin-clinic
npm install
BACKEND_URL=http://localhost:8080 npm run dev
```

**Client:**

```bash
cd client-clinic
npm install
NEXT_PUBLIC_API_URL=http://localhost:8080 npm run dev
```

## API keys (tùy chọn)

Trong `.env`:

```env
GEMINI_API_KEY=...    # Chatbot AI
HF_TOKEN=...          # Hugging Face (nếu dùng)
SMTP_EMAIL=...        # Gửi mail quên mật khẩu
APP_PASSWORD=...
```

---

## Xử lý sự cố

| Vấn đề | Gợi ý |
| :--- | :--- |
| Port 8080 bị chiếm | Đổi `SERVER_PORT=8081` trong `.env`, và `BACKEND_URL` / `NEXT_PUBLIC_API_URL` tương ứng |
| Backend không kết nối MySQL | Kiểm tra `sudo systemctl status mysql`, user/pass trong `.env` |
| Admin 403 / CORS | `SecurityConfig` đã cho phép `localhost:3000`, `5173` |
| Chatbot mất ngữ cảnh sau F5 | `ChatWidget` dùng `sessionStorage`; kiểm tra bảng `chat_sessions` và backend đã chạy Flyway V26+ |
| Thiếu Java | `sudo apt install openjdk-17-jdk` |

---

*Phân quyền: xem ma trận quyền tại Admin → Quyền truy cập (`/role-urls`), chỉ tài khoản ADMIN.*
