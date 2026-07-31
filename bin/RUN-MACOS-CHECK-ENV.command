#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
"${DIR}/unix-preflight-check.sh"
read -r -p "Finished. Press Enter to close..."
