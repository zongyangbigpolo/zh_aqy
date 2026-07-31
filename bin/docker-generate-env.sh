#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="${ROOT_DIR}/.env"

if [[ -f "${ENV_FILE}" ]]; then
  printf '.env already exists: %s\n' "${ENV_FILE}"
  exit 0
fi

random_secret() {
  local bytes="${1:-32}"
  if command -v openssl >/dev/null 2>&1; then
    openssl rand -base64 "${bytes}" | tr '+/' 'AB' | tr -d '=\n'
  else
    LC_ALL=C tr -dc 'A-Za-z0-9' </dev/urandom | head -c "$((bytes * 2))"
  fi
}

cat >"${ENV_FILE}" <<EOF
DB_NAME=zh_aqy
MYSQL_ROOT_PASSWORD=$(random_secret 24)
DB_USERNAME=zh_aqy_app
DB_PASSWORD=$(random_secret 24)
DRUID_LOGIN_USERNAME=admin
DRUID_LOGIN_PASSWORD=$(random_secret 18)
TOKEN_SECRET=$(random_secret 32)
WEB_PORT=8080
BACKEND_PORT=7070
MQTT_HOST=tcp://127.0.0.1:1883
MQTT_USERNAME=
MQTT_PASSWORD=
ALIYUN_SMS_ACCESS_KEY_ID=
ALIYUN_SMS_ACCESS_KEY_SECRET=
ALIYUN_SMS_SIGN_NAME=
ALIYUN_SMS_TEMPLATE_CODE=
ALIYUN_SMS_COOLDOWN_SECONDS=600
EOF

chmod 600 "${ENV_FILE}" || true
printf 'Created Docker Compose environment file: %s\n' "${ENV_FILE}"
