#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=bin/unix-common.sh
source "${SCRIPT_DIR}/unix-common.sh"

DEPLOY_ROOT="${DEPLOY_ROOT:-$(default_deploy_root)}"
DATABASE_NAME="${DATABASE_NAME:-zh_aqy}"
MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_ADMIN_USER="${MYSQL_ADMIN_USER:-root}"
MYSQL_ADMIN_PASSWORD="${MYSQL_ADMIN_PASSWORD:-}"
MYSQL_USE_SUDO="${MYSQL_USE_SUDO:-0}"
DB_USERNAME="${DB_USERNAME:-zh_aqy_app}"
DB_PASSWORD="${DB_PASSWORD:-}"
REDIS_HOST="${REDIS_HOST:-127.0.0.1}"
REDIS_PORT="${REDIS_PORT:-6379}"
REDIS_PASSWORD="${REDIS_PASSWORD:-}"
MQTT_HOST="${MQTT_HOST:-tcp://127.0.0.1:1883}"
MQTT_USERNAME="${MQTT_USERNAME:-}"
MQTT_PASSWORD="${MQTT_PASSWORD:-}"
SERVER_PORT="${SERVER_PORT:-7070}"
WEB_PORT="${WEB_PORT:-8080}"
SKIP_DATABASE_INIT="${SKIP_DATABASE_INIT:-0}"
SKIP_SQL_IMPORT="${SKIP_SQL_IMPORT:-0}"
SKIP_REDIS_CHECK="${SKIP_REDIS_CHECK:-0}"
SKIP_NGINX="${SKIP_NGINX:-0}"
NO_START_BACKEND="${NO_START_BACKEND:-0}"
FORCE_REINITIALIZE="${FORCE_REINITIALIZE:-0}"

mysql_base_args() {
  printf '%s\0' --protocol=tcp -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" -u "${MYSQL_ADMIN_USER}" --default-character-set=utf8mb4
}

mysql_exec() {
  local database="${1:-}"
  local sql="$2"
  local args=()
  while IFS= read -r -d '' item; do
    args+=("${item}")
  done < <(mysql_base_args)
  [[ -n "${database}" ]] && args+=("--database=${database}")

  if [[ "${MYSQL_USE_SUDO}" == "1" ]]; then
    printf '%s' "${sql}" | sudo mysql "${args[@]}"
  else
    MYSQL_PWD="${MYSQL_ADMIN_PASSWORD}" mysql "${args[@]}" <<<"${sql}"
  fi
}

mysql_query_scalar() {
  local database="${1:-}"
  local sql="$2"
  local args=()
  while IFS= read -r -d '' item; do
    args+=("${item}")
  done < <(mysql_base_args)
  [[ -n "${database}" ]] && args+=("--database=${database}")
  args+=(--batch --skip-column-names)

  if [[ "${MYSQL_USE_SUDO}" == "1" ]]; then
    printf '%s' "${sql}" | sudo mysql "${args[@]}" | head -n 1 | tr -d '[:space:]'
  else
    MYSQL_PWD="${MYSQL_ADMIN_PASSWORD}" mysql "${args[@]}" <<<"${sql}" | head -n 1 | tr -d '[:space:]'
  fi
}

mysql_import_file() {
  local database="$1"
  local file="$2"
  local args=()
  while IFS= read -r -d '' item; do
    args+=("${item}")
  done < <(mysql_base_args)
  args+=("--database=${database}")

  log_step "Importing SQL: ${file}"
  if [[ "${MYSQL_USE_SUDO}" == "1" ]]; then
    sudo mysql "${args[@]}" <"${file}"
  else
    MYSQL_PWD="${MYSQL_ADMIN_PASSWORD}" mysql "${args[@]}" <"${file}"
  fi
}

write_nginx_runtime_config() {
  local nginx_bin="$1"
  local frontend_dir="$2"
  local nginx_root="${DEPLOY_ROOT}/nginx-runtime"
  mkdir -p "${nginx_root}/conf" "${nginx_root}/logs"
  cat >"${nginx_root}/conf/nginx.conf" <<EOF
worker_processes  1;
error_log  logs/error.log;
pid        logs/nginx.pid;

events {
    worker_connections  1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile      on;

    server {
        listen ${WEB_PORT};
        server_name localhost;

        location / {
            root "${frontend_dir}";
            try_files \$uri \$uri/ /index.html;
        }

        location /prod-api/ {
            proxy_pass http://127.0.0.1:${SERVER_PORT}/prod-api/;
            proxy_set_header Host \$host;
            proxy_set_header X-Real-IP \$remote_addr;
            proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto \$scheme;
        }
    }
}
EOF

  if [[ -f "${nginx_root}/logs/nginx.pid" ]]; then
    "${nginx_bin}" -p "${nginx_root}/" -c conf/nginx.conf -s reload
  else
    "${nginx_bin}" -p "${nginx_root}/" -c conf/nginx.conf
  fi
}

safe_identifier "${DATABASE_NAME}" DATABASE_NAME
safe_identifier "${DB_USERNAME}" DB_USERNAME

printf '============================================================\n'
printf 'Zh_AqY fresh Ubuntu/macOS test deployment\n'
printf '============================================================\n'
printf 'This script is only for new test machines or empty disposable test databases.\n'

log_step "Checking release package"
check_package_files || die "Release package is incomplete."

JAVA_BIN="$(find_java || true)"
[[ -n "${JAVA_BIN}" ]] || die "Java was not found. Install Java 8 first."

MYSQL_CLI="$(find_mysql_cli || true)"
[[ -n "${MYSQL_CLI}" || "${MYSQL_USE_SUDO}" == "1" ]] || die "mysql client was not found. Install MySQL client first."

if [[ "${SKIP_REDIS_CHECK}" != "1" ]]; then
  test_tcp_port "${REDIS_HOST}" "${REDIS_PORT}" || die "Redis is not reachable at ${REDIS_HOST}:${REDIS_PORT}. Start Redis first, or set SKIP_REDIS_CHECK=1 for a partial backend test."
fi

if [[ "${SKIP_DATABASE_INIT}" != "1" ]]; then
  log_step "Preparing MySQL database"
  if [[ "${MYSQL_USE_SUDO}" != "1" && -z "${MYSQL_ADMIN_PASSWORD}" ]]; then
    read -rsp "Enter MySQL admin password for user '${MYSQL_ADMIN_USER}' (leave empty if none): " MYSQL_ADMIN_PASSWORD
    printf '\n'
  fi

  [[ -n "${DB_PASSWORD}" ]] || DB_PASSWORD="$(random_secret 24)"

  mysql_exec "" "CREATE DATABASE IF NOT EXISTS \`${DATABASE_NAME}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
  TABLE_COUNT="$(mysql_query_scalar "" "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '${DATABASE_NAME}';")"
  if [[ "${TABLE_COUNT}" -gt 0 && "${FORCE_REINITIALIZE}" != "1" && "${SKIP_SQL_IMPORT}" != "1" ]]; then
    die "Database '${DATABASE_NAME}' already has ${TABLE_COUNT} tables. Use a new database name, SKIP_SQL_IMPORT=1, or FORCE_REINITIALIZE=1 only for disposable test data."
  fi

  ESCAPED_DB_PASSWORD="$(escape_mysql_string "${DB_PASSWORD}")"
  mysql_exec "" "
CREATE USER IF NOT EXISTS '${DB_USERNAME}'@'localhost' IDENTIFIED BY '${ESCAPED_DB_PASSWORD}';
CREATE USER IF NOT EXISTS '${DB_USERNAME}'@'%' IDENTIFIED BY '${ESCAPED_DB_PASSWORD}';
ALTER USER '${DB_USERNAME}'@'localhost' IDENTIFIED BY '${ESCAPED_DB_PASSWORD}';
ALTER USER '${DB_USERNAME}'@'%' IDENTIFIED BY '${ESCAPED_DB_PASSWORD}';
GRANT ALL PRIVILEGES ON \`${DATABASE_NAME}\`.* TO '${DB_USERNAME}'@'localhost';
GRANT ALL PRIVILEGES ON \`${DATABASE_NAME}\`.* TO '${DB_USERNAME}'@'%';
FLUSH PRIVILEGES;"

  if [[ "${SKIP_SQL_IMPORT}" != "1" ]]; then
    mysql_import_file "${DATABASE_NAME}" "${PACKAGE_ROOT}/sql/ry_20240629.sql"
    mysql_import_file "${DATABASE_NAME}" "${PACKAGE_ROOT}/sql/quartz.sql"
    mysql_import_file "${DATABASE_NAME}" "${PACKAGE_ROOT}/sql/zh_aqy_schema.sql"
  fi
elif [[ -z "${DB_PASSWORD}" ]]; then
  die "DB_PASSWORD is required when SKIP_DATABASE_INIT=1."
fi

TOKEN_SECRET="${TOKEN_SECRET:-$(random_secret 32)}"
DRUID_LOGIN_PASSWORD="${DRUID_LOGIN_PASSWORD:-$(random_secret 18)}"
BACKEND_DIR="${DEPLOY_ROOT}/server"
FRONTEND_DIR="${DEPLOY_ROOT}/web"
UPLOAD_DIR="${DEPLOY_ROOT}/uploadPath"
mkdir -p "${BACKEND_DIR}" "${FRONTEND_DIR}" "${UPLOAD_DIR}"

log_step "Deploying packaged backend and frontend"
cp "${PACKAGE_ROOT}/server/aqy-admin.jar" "${BACKEND_DIR}/aqy-admin.jar"
rm -rf "${FRONTEND_DIR:?}/"*
cp -R "${PACKAGE_ROOT}/web/." "${FRONTEND_DIR}/"

DB_URL="jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${DATABASE_NAME}?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8"
ENV_FILE="${BACKEND_DIR}/backend.env"
write_env_file "${ENV_FILE}" \
  "DB_URL=${DB_URL}" \
  "DB_USERNAME=${DB_USERNAME}" \
  "DB_PASSWORD=${DB_PASSWORD}" \
  "DRUID_LOGIN_USERNAME=admin" \
  "DRUID_LOGIN_PASSWORD=${DRUID_LOGIN_PASSWORD}" \
  "TOKEN_SECRET=${TOKEN_SECRET}" \
  "SERVER_PORT=${SERVER_PORT}" \
  "SERVER_CONTEXT_PATH=/prod-api" \
  "RUOYI_PROFILE=${UPLOAD_DIR}" \
  "REDIS_HOST=${REDIS_HOST}" \
  "REDIS_PORT=${REDIS_PORT}" \
  "REDIS_DATABASE=0" \
  "REDIS_PASSWORD=${REDIS_PASSWORD}" \
  "MQTT_HOST=${MQTT_HOST}" \
  "MQTT_USERNAME=${MQTT_USERNAME}" \
  "MQTT_PASSWORD=${MQTT_PASSWORD}" \
  "ALIYUN_SMS_ACCESS_KEY_ID=" \
  "ALIYUN_SMS_ACCESS_KEY_SECRET=" \
  "ALIYUN_SMS_SIGN_NAME=" \
  "ALIYUN_SMS_TEMPLATE_CODE=" \
  "ALIYUN_SMS_COOLDOWN_SECONDS=600" \
  "FILE_DOMAIN=http://127.0.0.1:${WEB_PORT}" \
  "FILE_UPLOAD_PATH=${UPLOAD_DIR}" \
  "FILE_PREFIX=/zhbg"

cat >"${BACKEND_DIR}/run-backend.sh" <<'EOF'
#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/backend.env"
cd "${SCRIPT_DIR}"
exec java -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -jar aqy-admin.jar
EOF
chmod +x "${BACKEND_DIR}/run-backend.sh"

if [[ "${NO_START_BACKEND}" != "1" ]]; then
  log_step "Starting backend"
  nohup "${BACKEND_DIR}/run-backend.sh" >"${BACKEND_DIR}/backend.log" 2>&1 &
  BACKEND_PID=$!
  printf '%s\n' "${BACKEND_PID}" >"${BACKEND_DIR}/backend.pid"
  sleep 8
  if command -v curl >/dev/null 2>&1; then
    curl -fsS "http://127.0.0.1:${SERVER_PORT}/prod-api/captchaImage" >/dev/null || log_warn "Backend health check failed. Check ${BACKEND_DIR}/backend.log."
  fi
fi

if [[ "${SKIP_NGINX}" != "1" ]]; then
  if NGINX_BIN="$(find_nginx)"; then
    log_step "Starting local nginx frontend"
    write_nginx_runtime_config "${NGINX_BIN}" "${FRONTEND_DIR}" || log_warn "Could not start nginx. You can still test the backend directly."
  else
    log_warn "nginx was not found. Install nginx or set SKIP_NGINX=1 for backend-only tests."
  fi
fi

log_step "Fresh deployment finished"
printf 'Backend health: http://127.0.0.1:%s/prod-api/captchaImage\n' "${SERVER_PORT}"
printf 'Frontend URL   : http://127.0.0.1:%s/\n' "${WEB_PORT}"
printf 'Deploy root    : %s\n' "${DEPLOY_ROOT}"
printf 'Database       : %s\n' "${DATABASE_NAME}"
printf 'Database user  : %s\n' "${DB_USERNAME}"
printf 'Initial login  : admin / ChangeMe@123456\n'
printf 'Change the admin password immediately after first login.\n'
