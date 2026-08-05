#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# shellcheck disable=SC1091
source "${DIR}/docker-common.sh"

require_docker
ensure_env

echo "Resetting Docker fresh-test containers and volumes..."
compose_fresh down -v --remove-orphans

compose_fresh up -d --build
wait_for_backend

echo ""
echo "Docker fresh-test deployment started."
echo "Frontend: http://127.0.0.1:${WEB_PORT:-8080}/"
echo "Backend : http://127.0.0.1:${BACKEND_PORT:-7070}/prod-api/captchaImage"
echo "Login   : admin / ChangeMe@123456"
echo "Change the admin password immediately after first login."
