#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
printf 'This is only for a new Ubuntu test machine or an empty disposable database.\n'
printf 'Do not use it to upgrade an old production server.\n'
read -r -p "Press Enter to continue, or Ctrl+C to cancel..."
"${DIR}/unix-fresh-test-deploy.sh"
read -r -p "Finished. Press Enter to close..."
