#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# shellcheck disable=SC1091
source "${DIR}/docker-common.sh"

read_with_default() {
  local prompt="$1"
  local default_value="$2"
  local value
  read -r -p "${prompt} [${default_value}]: " value
  printf '%s' "${value:-${default_value}}"
}

require_docker
ensure_env
mkdir -p "${DOCKER_BACKUP_DIR}"

default_host="$(default_old_db_host)"
old_host="${OLD_DB_HOST:-$(read_with_default "Existing MySQL host" "${default_host}")}"
old_port="${OLD_DB_PORT:-$(read_with_default "Existing MySQL port" "3306")}"
old_name="${OLD_DB_NAME:-$(read_with_default "Existing MySQL database name" "${DB_NAME}")}"
old_user="${OLD_DB_USERNAME:-$(read_with_default "Existing MySQL username" "root")}"

if [[ -n "${OLD_DB_PASSWORD:-}" ]]; then
  old_password="${OLD_DB_PASSWORD}"
else
  read -r -s -p "Existing MySQL password: " old_password
  echo ""
fi

echo ""
echo "This will import '${old_name}' from ${old_host}:${old_port} into Docker database '${DB_NAME}'."
echo "The Docker target database will be dropped and recreated before import."
read -r -p "Type MIGRATE to continue: " confirm
if [[ "${confirm}" != "MIGRATE" ]]; then
  echo "Migration cancelled."
  exit 1
fi

compose_migrate up -d mysql redis

timestamp="$(date +%Y%m%d-%H%M%S)"
dump_file="${DOCKER_BACKUP_DIR}/existing-${old_name}-${timestamp}.sql"
network_args=()
while IFS= read -r network_arg; do
  network_args+=("${network_arg}")
done < <(docker_run_mysql_network_args "${old_host}")

echo "Dumping existing database to ${dump_file} ..."
docker run --rm "${network_args[@]}" \
  -e MYSQL_PWD="${old_password}" \
  mysql:8.0 \
  mysqldump \
  --host="${old_host}" \
  --port="${old_port}" \
  --user="${old_user}" \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  --default-character-set=utf8mb4 \
  --column-statistics=0 \
  --set-gtid-purged=OFF \
  "${old_name}" > "${dump_file}"

if [[ ! -s "${dump_file}" ]]; then
  echo "Database dump is empty: ${dump_file}" >&2
  exit 1
fi

echo "Resetting Docker target database ..."
reset_target_database

echo "Importing dump into Docker MySQL ..."
compose_migrate exec -T mysql mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" "${DB_NAME}" < "${dump_file}"

apply_migrations

compose_migrate up -d --build
wait_for_backend
print_access_info

echo "Migration finished. Backup dump kept at: ${dump_file}"
