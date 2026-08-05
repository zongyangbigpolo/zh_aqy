#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
printf 'This upgrades an existing macOS deployment without touching MySQL data.\n'
printf 'Back up the existing database before continuing.\n'
read -r -p "Press Enter to continue, or Ctrl+C to cancel..."
"${DIR}/unix-upgrade-existing.sh"
read -r -p "Finished. Press Enter to close..."
