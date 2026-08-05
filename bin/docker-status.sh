#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# shellcheck disable=SC1091
source "${DIR}/docker-common.sh"

require_docker
load_existing_env

compose_migrate ps

echo ""
echo "Frontend probe:"
curl -fsSI --max-time 10 "http://127.0.0.1:${WEB_PORT:-8080}/" | head -5 || true

echo ""
echo "Backend probe:"
curl -fsS --max-time 10 "http://127.0.0.1:${BACKEND_PORT:-7070}/prod-api/captchaImage" | head -c 200 || true
echo ""
