#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
"${DIR}/RUN-DOCKER-FRESH-TEST.sh"
read -r -p "Finished. Press Enter to close..."
