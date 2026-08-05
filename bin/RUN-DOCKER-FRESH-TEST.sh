#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${DIR}/.." && pwd)"

cd "${ROOT_DIR}"
"${DIR}/docker-generate-env.sh"
set -a
# shellcheck disable=SC1091
source "${ROOT_DIR}/.env"
set +a

if docker compose version >/dev/null 2>&1; then
  docker compose up -d --build
elif command -v docker-compose >/dev/null 2>&1; then
  docker-compose up -d --build
else
  echo "Docker Compose was not found. Install Docker Desktop or Docker Engine with Compose plugin first." >&2
  exit 1
fi

echo ""
echo "Docker fresh-test deployment started."
echo "Frontend: http://127.0.0.1:${WEB_PORT:-8080}/"
echo "Backend : http://127.0.0.1:${BACKEND_PORT:-7070}/prod-api/captchaImage"
echo "Login   : admin / ChangeMe@123456"
echo "Change the admin password immediately after first login."
