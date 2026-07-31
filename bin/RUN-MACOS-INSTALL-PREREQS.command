#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
read -r -p "Install optional Nginx too? Type Y to include it: " INCLUDE
if [[ "${INCLUDE}" =~ ^[Yy]$ ]]; then
  INCLUDE_NGINX=1 "${DIR}/unix-install-prerequisites.sh"
else
  "${DIR}/unix-install-prerequisites.sh"
fi
read -r -p "Finished. Press Enter to close..."
