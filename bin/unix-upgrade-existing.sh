#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=bin/unix-common.sh
source "${SCRIPT_DIR}/unix-common.sh"

DEPLOY_ROOT="${DEPLOY_ROOT:-$(default_deploy_root)}"
BACKEND_DIR="${BACKEND_DIR:-${DEPLOY_ROOT}/server}"
FRONTEND_DIR="${FRONTEND_DIR:-${DEPLOY_ROOT}/web}"
BACKEND_PORT="${BACKEND_PORT:-7070}"
SERVICE_NAME="${SERVICE_NAME:-}"
SKIP_STOP="${SKIP_STOP:-0}"
START_BACKEND="${START_BACKEND:-1}"
RESTART_NGINX="${RESTART_NGINX:-0}"
SKIP_PREFLIGHT="${SKIP_PREFLIGHT:-0}"

printf '============================================================\n'
printf 'Zh_AqY existing Ubuntu/macOS server upgrade\n'
printf '============================================================\n'
printf 'This script replaces backend jar and frontend files only.\n'
printf 'It does not initialize, delete, or modify your old MySQL database.\n'

if [[ "${SKIP_PREFLIGHT}" != "1" ]]; then
  log_step "Running strict preflight"
  REQUIRE_PRODUCTION_CONFIG=1 "${SCRIPT_DIR}/unix-preflight-check.sh"
fi

log_step "Checking release package"
check_package_files || die "Release package is incomplete."

BACKUP_DIR="${DEPLOY_ROOT}/backups/$(date +%Y%m%d-%H%M%S)"
mkdir -p "${BACKEND_DIR}" "${FRONTEND_DIR}" "${BACKUP_DIR}"

if [[ "${SKIP_STOP}" != "1" ]]; then
  log_step "Stopping existing backend"
  if [[ -n "${SERVICE_NAME}" ]]; then
    sudo systemctl stop "${SERVICE_NAME}"
  elif [[ -f "${BACKEND_DIR}/backend.pid" ]]; then
    OLD_PID="$(cat "${BACKEND_DIR}/backend.pid")"
    if [[ -n "${OLD_PID}" ]] && kill -0 "${OLD_PID}" 2>/dev/null; then
      kill "${OLD_PID}"
      sleep 2
    fi
  else
    while IFS= read -r pid; do
      [[ -z "${pid}" ]] && continue
      log_warn "Stopping PID ${pid} matching aqy-admin.jar"
      kill "${pid}" || true
    done < <(ps -eo pid=,args= | awk '/aqy-admin[.]jar/ && !/awk/ {print $1}')
  fi
fi

log_step "Backing up current files"
if [[ -f "${BACKEND_DIR}/aqy-admin.jar" ]]; then
  cp "${BACKEND_DIR}/aqy-admin.jar" "${BACKUP_DIR}/aqy-admin.jar"
fi
if [[ -d "${FRONTEND_DIR}" ]]; then
  mkdir -p "${BACKUP_DIR}/web"
  cp -R "${FRONTEND_DIR}/." "${BACKUP_DIR}/web/" 2>/dev/null || true
fi

log_step "Deploying packaged backend and frontend"
cp "${PACKAGE_ROOT}/server/aqy-admin.jar" "${BACKEND_DIR}/aqy-admin.jar"
rm -rf "${FRONTEND_DIR:?}/"*
cp -R "${PACKAGE_ROOT}/web/." "${FRONTEND_DIR}/"

if [[ "${START_BACKEND}" == "1" ]]; then
  log_step "Starting backend"
  if [[ -n "${SERVICE_NAME}" ]]; then
    sudo systemctl start "${SERVICE_NAME}"
  elif [[ -x "${BACKEND_DIR}/run-backend.sh" ]]; then
    nohup "${BACKEND_DIR}/run-backend.sh" >"${BACKEND_DIR}/backend.log" 2>&1 &
    printf '%s\n' "$!" >"${BACKEND_DIR}/backend.pid"
  else
    nohup java -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -jar "${BACKEND_DIR}/aqy-admin.jar" >"${BACKEND_DIR}/backend.log" 2>&1 &
    printf '%s\n' "$!" >"${BACKEND_DIR}/backend.pid"
  fi
  sleep 8
  if command -v curl >/dev/null 2>&1; then
    curl -fsS "http://127.0.0.1:${BACKEND_PORT}/prod-api/captchaImage" >/dev/null || log_warn "Backend health check failed. Check backend logs."
  fi
fi

if [[ "${RESTART_NGINX}" == "1" ]]; then
  log_step "Reloading nginx"
  if NGINX_BIN="$(find_nginx)"; then
    "${NGINX_BIN}" -s reload || log_warn "nginx reload failed. Reload it manually."
  else
    log_warn "nginx command not found. Reload nginx manually."
  fi
fi

log_step "Upgrade finished"
printf 'Database was not touched by this script.\n'
printf 'Backup location: %s\n' "${BACKUP_DIR}"
