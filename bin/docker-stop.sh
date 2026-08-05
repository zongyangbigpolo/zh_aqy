#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# shellcheck disable=SC1091
source "${DIR}/docker-common.sh"

require_docker
load_existing_env

compose_migrate down

echo "Docker services stopped. Database, Redis, and upload volumes were preserved."
