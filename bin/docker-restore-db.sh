#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# shellcheck disable=SC1091
source "${DIR}/docker-common.sh"

require_docker
load_existing_env

dump_file="${1:-}"
if [[ -z "${dump_file}" ]]; then
  read -r -p "SQL dump file to restore: " dump_file
fi

if [[ ! -f "${dump_file}" ]]; then
  echo "SQL dump file does not exist: ${dump_file}" >&2
  exit 1
fi

echo "This will replace Docker database '${DB_NAME}' with ${dump_file}."
read -r -p "Type RESTORE to continue: " confirm
if [[ "${confirm}" != "RESTORE" ]]; then
  echo "Restore cancelled."
  exit 1
fi

compose_migrate up -d mysql redis
compose_migrate stop backend web >/dev/null 2>&1 || true
reset_target_database

echo "Importing ${dump_file} into Docker MySQL ..."
compose_migrate exec -T mysql mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" "${DB_NAME}" < "${dump_file}"

apply_migrations
compose_migrate up -d --build
wait_for_backend
print_access_info

echo "Restore finished."
