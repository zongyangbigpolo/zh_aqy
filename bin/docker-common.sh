#!/usr/bin/env bash

set -euo pipefail

DOCKER_BIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_ROOT_DIR="$(cd "${DOCKER_BIN_DIR}/.." && pwd)"
DOCKER_BACKUP_DIR="${DOCKER_ROOT_DIR}/backups"

compose() {
  if docker compose version >/dev/null 2>&1; then
    docker compose "$@"
  elif command -v docker-compose >/dev/null 2>&1; then
    docker-compose "$@"
  else
    echo "Docker Compose was not found. Install Docker Desktop or Docker Engine with Compose plugin first." >&2
    return 1
  fi
}

compose_base() {
  (cd "${DOCKER_ROOT_DIR}" && compose -f docker-compose.yml "$@")
}

compose_fresh() {
  (cd "${DOCKER_ROOT_DIR}" && compose -f docker-compose.yml -f docker-compose.fresh.yml "$@")
}

compose_migrate() {
  (cd "${DOCKER_ROOT_DIR}" && compose -f docker-compose.yml -f docker-compose.migrate.yml "$@")
}

require_docker() {
  if ! docker info >/dev/null 2>&1; then
    echo "Docker is not running. Start Docker Desktop or Docker Engine first." >&2
    exit 1
  fi
}

ensure_env() {
  "${DOCKER_BIN_DIR}/docker-generate-env.sh"
  set -a
  # shellcheck disable=SC1091
  source "${DOCKER_ROOT_DIR}/.env"
  set +a
}

load_existing_env() {
  if [[ ! -f "${DOCKER_ROOT_DIR}/.env" ]]; then
    echo ".env was not found. Start or migrate the Docker deployment first." >&2
    exit 1
  fi

  set -a
  # shellcheck disable=SC1091
  source "${DOCKER_ROOT_DIR}/.env"
  set +a
}

wait_for_backend() {
  local url="http://127.0.0.1:${BACKEND_PORT:-7070}/prod-api/captchaImage"
  local i
  for i in $(seq 1 60); do
    if curl -fsS --max-time 5 "${url}" >/dev/null 2>&1; then
      return 0
    fi
    sleep 5
  done

  echo "Backend did not become healthy in time: ${url}" >&2
  return 1
}

apply_migrations() {
  local migrations_dir="${DOCKER_ROOT_DIR}/sql/migrations"
  local applied=0
  local file

  if [[ ! -d "${migrations_dir}" ]]; then
    echo "No sql/migrations directory found; skipping database migrations."
    return 0
  fi

  shopt -s nullglob
  for file in "${migrations_dir}"/*.sql; do
    echo "Applying migration: ${file##*/}"
    compose_migrate exec -T mysql mysql --default-character-set=utf8mb4 -u"${DB_USERNAME}" -p"${DB_PASSWORD}" "${DB_NAME}" < "${file}"
    applied=$((applied + 1))
  done
  shopt -u nullglob

  if [[ "${applied}" -eq 0 ]]; then
    echo "No SQL migration files found; skipping database migrations."
  fi
}

reset_target_database() {
  compose_migrate exec -T mysql mysql --default-character-set=utf8mb4 -uroot -p"${MYSQL_ROOT_PASSWORD}" -e "DROP DATABASE IF EXISTS \`${DB_NAME}\`; CREATE DATABASE \`${DB_NAME}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci; GRANT ALL PRIVILEGES ON \`${DB_NAME}\`.* TO '${DB_USERNAME}'@'%'; FLUSH PRIVILEGES;"
}

default_old_db_host() {
  case "$(uname -s)" in
    Darwin) printf 'host.docker.internal' ;;
    *) printf '127.0.0.1' ;;
  esac
}

docker_run_mysql_network_args() {
  local old_host="$1"
  if [[ "$(uname -s)" = "Linux" && ( "${old_host}" = "127.0.0.1" || "${old_host}" = "localhost" ) ]]; then
    printf '%s\n' "--network" "host"
  fi
}

print_access_info() {
  echo ""
  echo "Docker deployment is running."
  echo "Frontend: http://127.0.0.1:${WEB_PORT:-8080}/"
  echo "Backend : http://127.0.0.1:${BACKEND_PORT:-7070}/prod-api/captchaImage"
}
