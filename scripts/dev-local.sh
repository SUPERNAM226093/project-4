#!/usr/bin/env bash
# Chạy toàn bộ stack trên máy local (không Docker).
# Cách dùng:
#   ./scripts/dev-local.sh          # chạy tất cả (cần tmux hoặc terminal đa tab)
#   ./scripts/dev-local.sh backend  # chỉ backend
#   ./scripts/dev-local.sh admin    # chỉ admin
#   ./scripts/dev-local.sh client   # chỉ client
#   ./scripts/dev-local.sh ml       # chỉ ML Flask
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if [[ -f "$ROOT/.env" ]]; then
  set -a
  # shellcheck disable=SC1091
  source "$ROOT/.env"
  set +a
  export GEMINI_API_KEY HF_TOKEN ML_BACKEND_URL DB_HOST CLINIC_DB_PORT CLINIC_DB_NAME CLINIC_DB_USER CLINIC_DB_PASS SERVER_PORT JWT_SECRET
fi

export DB_HOST="${DB_HOST:-localhost}"
export CLINIC_DB_PORT="${CLINIC_DB_PORT:-3306}"
export CLINIC_DB_NAME="${CLINIC_DB_NAME:-clinic}"
export CLINIC_DB_USER="${CLINIC_DB_USER:-root}"
export CLINIC_DB_PASS="${CLINIC_DB_PASS:-root}"
export SERVER_PORT="${SERVER_PORT:-8081}"
export ML_BACKEND_URL="${ML_BACKEND_URL:-http://localhost:5000}"

run_backend() {
  echo ">>> Backend Spring Boot — http://localhost:${SERVER_PORT}"
  cd "$ROOT/clinic"
  ./gradlew bootRun --no-daemon
}

run_admin() {
  echo ">>> Admin Vite — http://localhost:3000"
  cd "$ROOT/admin/admin-clinic"
  if [[ ! -d node_modules ]]; then npm install; fi
  BACKEND_URL="http://localhost:${SERVER_PORT}" npm run dev
}

run_client() {
  echo ">>> Client Next.js — http://localhost:5173"
  cd "$ROOT/client-clinic"
  if [[ ! -d node_modules ]]; then npm install; fi
  NEXT_PUBLIC_API_URL="http://localhost:${SERVER_PORT}" npm run dev
}

run_ml() {
  echo ">>> ML Flask — http://localhost:5000"
  cd "$ROOT/heart-predict-model"
  if [[ ! -d .venv ]]; then
    python3 -m venv .venv
    .venv/bin/pip install -r requirements.txt
  fi
  .venv/bin/python app.py
}

check_java() {
  if ! command -v java >/dev/null 2>&1; then
    echo "Thiếu Java 17. Cài: sudo apt install openjdk-17-jdk"
    exit 1
  fi
}

check_mysql() {
  if ! command -v mysql >/dev/null 2>&1; then
    echo "Thiếu MySQL client. Cài: sudo apt install mysql-server"
    exit 1
  fi
}

usage() {
  cat <<EOF
Chạy local (không Docker):

  Yêu cầu: Java 17, Node.js 20+, Python 3, MySQL 8

  1. cp .env.example .env && chỉnh mật khẩu MySQL
  2. ./scripts/setup-db.sh
  3. Mở 4 terminal:
     ./scripts/dev-local.sh backend
     ./scripts/dev-local.sh ml
     ./scripts/dev-local.sh admin
     ./scripts/dev-local.sh client

  URL:
    Admin:  http://localhost:3000
    Client: http://localhost:5173
    API:    http://localhost:${SERVER_PORT}

  Tài khoản mẫu: admin@gmail.com / 123456
EOF
}

SERVICE="${1:-help}"

case "$SERVICE" in
  backend)
    check_java
    check_mysql
    run_backend
    ;;
  admin)
    run_admin
    ;;
  client)
    run_client
    ;;
  ml)
    run_ml
    ;;
  all)
    if command -v tmux >/dev/null 2>&1; then
      check_java
      check_mysql
      tmux new-session -d -s clinic-dev -n backend "./scripts/dev-local.sh backend"
      tmux new-window -t clinic-dev -n ml "./scripts/dev-local.sh ml"
      tmux new-window -t clinic-dev -n admin "./scripts/dev-local.sh admin"
      tmux new-window -t clinic-dev -n client "./scripts/dev-local.sh client"
      echo ">>> Đã khởi chạy trong tmux session 'clinic-dev'. Gõ: tmux attach -t clinic-dev"
    else
      usage
      echo ""
      echo "Cài tmux để chạy một lệnh: sudo apt install tmux"
      echo "Hoặc mở 4 terminal và chạy từng lệnh backend/ml/admin/client ở trên."
    fi
    ;;
  help|*)
    usage
    ;;
esac
