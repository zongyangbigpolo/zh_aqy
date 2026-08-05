#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# shellcheck disable=SC1091
source "${DIR}/docker-common.sh"

require_docker
load_existing_env
mkdir -p "${DOCKER_BACKUP_DIR}"

compose_migrate up -d mysql

timestamp="$(date +%Y%m%d-%H%M%S)"
dump_file="${DOCKER_BACKUP_DIR}/docker-${DB_NAME}-${timestamp}.sql"

echo "Backing up Docker MySQL database to ${dump_file} ..."
compose_migrate exec -T mysql mysqldump \
  -u"${DB_USERNAME}" \
  -p"${DB_PASSWORD}" \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  --default-character-set=utf8mb4 \
  "${DB_NAME}" > "${dump_file}"

echo "Backup finished: ${dump_file}"
