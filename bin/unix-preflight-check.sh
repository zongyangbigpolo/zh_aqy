#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=bin/unix-common.sh
source "${SCRIPT_DIR}/unix-common.sh"

REQUIRE_PRODUCTION_CONFIG="${REQUIRE_PRODUCTION_CONFIG:-0}"
SKIP_PACKAGE_CHECK="${SKIP_PACKAGE_CHECK:-0}"
SKIP_NGINX="${SKIP_NGINX:-0}"
MYSQL_HOST_OVERRIDE="${MYSQL_HOST:-}"
MYSQL_PORT_OVERRIDE="${MYSQL_PORT:-}"
REDIS_HOST_CHECK="${REDIS_HOST:-127.0.0.1}"
REDIS_PORT_CHECK="${REDIS_PORT:-6379}"

ERRORS=0
WARNINGS=0

add_issue() {
  local severity="$1"
  local area="$2"
  local message="$3"
  local fix="$4"
  printf '[%s] %s: %s\n' "${severity}" "${area}" "${message}"
  printf '      Fix: %s\n' "${fix}"
  case "${severity}" in
    ERROR) ERRORS=$((ERRORS + 1)) ;;
    WARN) WARNINGS=$((WARNINGS + 1)) ;;
  esac
}

check_env() {
  local name="$1"
  local description="$2"
  local required="$3"
  local value="${!name:-}"
  if [[ -z "${value}" ]]; then
    if [[ "${required}" == "1" ]]; then
      add_issue ERROR Config "${name} is not configured. ${description}" "export ${name}=your-value"
    else
      add_issue WARN Config "${name} is not configured. ${description}" "export ${name}=your-value"
    fi
  else
    log_ok "${name} configured"
  fi
}

parse_mysql_endpoint() {
  local db_url="$1"
  if [[ "${db_url}" =~ ^jdbc:mysql://([^:/?]+)(:([0-9]+))?/ ]]; then
    local host="${BASH_REMATCH[1]}"
    local port="${BASH_REMATCH[3]:-3306}"
    printf '%s:%s' "${host}" "${port}"
    return 0
  fi
  return 1
}

printf '============================================================\n'
printf 'Zh_AqY Ubuntu/macOS preflight check\n'
printf '============================================================\n'

log_step "Checking platform"
PLATFORM="$(detect_platform)"
log_ok "platform: ${PLATFORM}"

if [[ "${SKIP_PACKAGE_CHECK}" != "1" ]]; then
  log_step "Checking release package files"
  if ! check_package_files; then
    add_issue ERROR Package "Release package is incomplete." "Download the complete cross-platform package and extract it again."
  fi
fi

log_step "Checking runtime programs"
if JAVA_PATH="$(find_java)"; then
  log_ok "java found: ${JAVA_PATH}"
  "${JAVA_PATH}" -version 2>&1 | sed 's/^/    /'
  if ! java_is_version_8 "${JAVA_PATH}"; then
    add_issue WARN Java "Java exists but does not look like Java 8." "Install Java 8 or verify this JRE can run aqy-admin.jar."
  fi
else
  add_issue ERROR Java "Java was not found." "Run bin/RUN-UBUNTU-INSTALL-PREREQS.sh or bin/RUN-MACOS-INSTALL-PREREQS.command, or install Java 8 manually."
fi

if MYSQL_CLI="$(find_mysql_cli)"; then
  log_ok "mysql client found: ${MYSQL_CLI}"
else
  add_issue WARN MySQL "mysql client was not found." "Install MySQL client/server before fresh database initialization."
fi

if command -v redis-cli >/dev/null 2>&1; then
  log_ok "redis-cli found: $(command -v redis-cli)"
else
  add_issue INFO Redis "redis-cli was not found." "Install Redis tools if you want CLI diagnostics."
fi

if [[ "${SKIP_NGINX}" != "1" ]]; then
  if NGINX_BIN="$(find_nginx)"; then
    log_ok "nginx found: ${NGINX_BIN}"
  else
    add_issue INFO Nginx "nginx was not found." "Nginx is optional for backend-only tests, but recommended for serving the frontend."
  fi
fi

log_step "Checking backend environment variables"
STRICT=0
if [[ "${REQUIRE_PRODUCTION_CONFIG}" == "1" ]]; then
  STRICT=1
fi

check_env DB_URL "MySQL JDBC URL used by the backend." "${STRICT}"
check_env DB_USERNAME "MySQL application username." "${STRICT}"
check_env DB_PASSWORD "MySQL application password." "${STRICT}"
check_env TOKEN_SECRET "JWT signing secret. Use a long random value." "${STRICT}"

if [[ -n "${TOKEN_SECRET:-}" && "${#TOKEN_SECRET}" -lt 32 ]]; then
  add_issue WARN Config "TOKEN_SECRET is shorter than 32 characters." "Use a long random value and restart the backend."
fi

UPLOAD_PATH="${FILE_UPLOAD_PATH:-${RUOYI_PROFILE:-}}"
if [[ -z "${UPLOAD_PATH}" ]]; then
  add_issue WARN Config "FILE_UPLOAD_PATH is not configured." "export FILE_UPLOAD_PATH=\"${HOME}/zh-aqy/uploadPath\""
elif [[ ! -d "${UPLOAD_PATH}" ]]; then
  add_issue WARN Filesystem "Upload directory does not exist: ${UPLOAD_PATH}" "Create it before starting the backend."
else
  log_ok "upload path exists: ${UPLOAD_PATH}"
fi

log_step "Checking service ports"
MYSQL_ENDPOINT=""
if [[ -n "${MYSQL_HOST_OVERRIDE}" ]]; then
  MYSQL_ENDPOINT="${MYSQL_HOST_OVERRIDE}:${MYSQL_PORT_OVERRIDE:-3306}"
elif [[ -n "${DB_URL:-}" ]]; then
  MYSQL_ENDPOINT="$(parse_mysql_endpoint "${DB_URL}" || true)"
fi

if [[ -n "${MYSQL_ENDPOINT}" ]]; then
  MYSQL_HOST_TO_CHECK="${MYSQL_ENDPOINT%%:*}"
  MYSQL_PORT_TO_CHECK="${MYSQL_ENDPOINT##*:}"
  if test_tcp_port "${MYSQL_HOST_TO_CHECK}" "${MYSQL_PORT_TO_CHECK}"; then
    log_ok "MySQL reachable: ${MYSQL_HOST_TO_CHECK}:${MYSQL_PORT_TO_CHECK}"
  else
    add_issue WARN MySQL "MySQL is not reachable at ${MYSQL_HOST_TO_CHECK}:${MYSQL_PORT_TO_CHECK}." "Start MySQL, check firewall, or fix DB_URL."
  fi
else
  add_issue WARN MySQL "MySQL endpoint could not be inferred." "Set DB_URL or MYSQL_HOST/MYSQL_PORT before checking connectivity."
fi

if test_tcp_port "${REDIS_HOST_CHECK}" "${REDIS_PORT_CHECK}"; then
  log_ok "Redis reachable: ${REDIS_HOST_CHECK}:${REDIS_PORT_CHECK}"
else
  add_issue WARN Redis "Redis is not reachable at ${REDIS_HOST_CHECK}:${REDIS_PORT_CHECK}." "Start Redis or configure REDIS_HOST/REDIS_PORT."
fi

log_step "Checking optional business integrations"
if [[ -z "${MQTT_HOST:-}" ]]; then
  add_issue WARN MQTT "MQTT_HOST is not configured." "Set MQTT_HOST/MQTT_USERNAME/MQTT_PASSWORD if this server receives device data through MQTT."
else
  log_ok "MQTT_HOST configured: ${MQTT_HOST}"
fi

MISSING_SMS=()
for name in ALIYUN_SMS_ACCESS_KEY_ID ALIYUN_SMS_ACCESS_KEY_SECRET ALIYUN_SMS_SIGN_NAME ALIYUN_SMS_TEMPLATE_CODE; do
  if [[ -z "${!name:-}" ]]; then
    MISSING_SMS+=("${name}")
  fi
done
if [[ "${#MISSING_SMS[@]}" -gt 0 ]]; then
  add_issue WARN "Aliyun SMS" "SMS environment variables are incomplete: ${MISSING_SMS[*]}" "Set all SMS variables before enabling alarm SMS notifications."
else
  log_ok "Aliyun SMS variables configured"
fi

log_step "Preflight summary"
printf 'Errors: %s, warnings: %s\n' "${ERRORS}" "${WARNINGS}"

if [[ "${ERRORS}" -gt 0 ]]; then
  exit 1
fi

exit 0
