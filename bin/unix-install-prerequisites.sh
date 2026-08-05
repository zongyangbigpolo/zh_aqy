#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=bin/unix-common.sh
source "${SCRIPT_DIR}/unix-common.sh"

INCLUDE_NGINX="${INCLUDE_NGINX:-0}"
DRY_RUN="${DRY_RUN:-0}"
START_SERVICES="${START_SERVICES:-1}"

run_cmd() {
  if [[ "${DRY_RUN}" == "1" ]]; then
    printf 'DRY RUN: %q ' "$@"
    printf '\n'
    return 0
  fi
  "$@"
}

install_ubuntu() {
  log_step "Installing Ubuntu prerequisites"
  if ! command -v apt-get >/dev/null 2>&1; then
    die "apt-get was not found. This Linux installer currently targets Ubuntu/Debian."
  fi

  run_cmd sudo apt-get update

  if ! java_path="$(find_java)" || ! java_is_version_17 "${java_path}"; then
    if apt-cache show openjdk-17-jdk >/dev/null 2>&1; then
      run_cmd sudo apt-get install -y openjdk-17-jdk
    else
      log_warn "openjdk-17-jdk is not available in the default apt repository. Installing Temurin 17 via Adoptium apt repository."
      run_cmd sudo apt-get install -y wget apt-transport-https gpg ca-certificates
      run_cmd sudo mkdir -p /etc/apt/keyrings
      if [[ "${DRY_RUN}" != "1" ]]; then
        wget -qO- https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo gpg --dearmor -o /etc/apt/keyrings/adoptium.gpg
        . /etc/os-release
        echo "deb [signed-by=/etc/apt/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb ${VERSION_CODENAME} main" | sudo tee /etc/apt/sources.list.d/adoptium.list >/dev/null
      fi
      run_cmd sudo apt-get update
      run_cmd sudo apt-get install -y temurin-17-jdk
    fi
  else
    log_ok "Java 17 already installed: ${java_path}"
  fi

  if ! command -v mysql >/dev/null 2>&1; then
    run_cmd sudo apt-get install -y mysql-server
  else
    log_ok "MySQL client already installed: $(command -v mysql)"
  fi

  if ! command -v redis-server >/dev/null 2>&1; then
    run_cmd sudo apt-get install -y redis-server
  else
    log_ok "Redis already installed: $(command -v redis-server)"
  fi

  if [[ "${INCLUDE_NGINX}" == "1" ]]; then
    if ! command -v nginx >/dev/null 2>&1; then
      run_cmd sudo apt-get install -y nginx
    else
      log_ok "Nginx already installed: $(command -v nginx)"
    fi
  fi

  if [[ "${START_SERVICES}" == "1" ]]; then
    run_cmd sudo systemctl enable --now mysql || log_warn "Could not enable/start mysql service automatically."
    run_cmd sudo systemctl enable --now redis-server || log_warn "Could not enable/start redis-server service automatically."
    if [[ "${INCLUDE_NGINX}" == "1" ]]; then
      run_cmd sudo systemctl enable --now nginx || log_warn "Could not enable/start nginx service automatically."
    fi
  fi
}

install_macos() {
  log_step "Installing macOS prerequisites"
  if ! command -v brew >/dev/null 2>&1; then
    die "Homebrew was not found. Install Homebrew first: https://brew.sh/"
  fi

  if ! java_path="$(find_java)" || ! java_is_version_17 "${java_path}"; then
    run_cmd brew install --cask temurin17 || run_cmd brew install openjdk@17
  else
    log_ok "Java 17 already installed: ${java_path}"
  fi

  command -v mysql >/dev/null 2>&1 || run_cmd brew install mysql
  command -v redis-server >/dev/null 2>&1 || run_cmd brew install redis

  if [[ "${INCLUDE_NGINX}" == "1" ]]; then
    command -v nginx >/dev/null 2>&1 || run_cmd brew install nginx
  fi

  if [[ "${START_SERVICES}" == "1" ]]; then
    run_cmd brew services start mysql || log_warn "Could not start mysql via brew services."
    run_cmd brew services start redis || log_warn "Could not start redis via brew services."
    if [[ "${INCLUDE_NGINX}" == "1" ]]; then
      run_cmd brew services start nginx || log_warn "Could not start nginx via brew services."
    fi
  fi
}

printf '============================================================\n'
printf 'Zh_AqY Ubuntu/macOS prerequisite installer\n'
printf '============================================================\n'
printf 'Required: Java 17, MySQL, Redis\n'
printf 'Optional: Nginx\n'

case "$(detect_platform)" in
  ubuntu) install_ubuntu ;;
  macos) install_macos ;;
esac

log_step "Next steps"
printf '1. Reopen your terminal so PATH changes take effect.\n'
printf '2. Make sure MySQL is running and you know the root/admin password.\n'
printf '3. Make sure Redis is running on 127.0.0.1:6379, or configure REDIS_HOST/REDIS_PORT.\n'
printf '4. Run the preflight script for your platform.\n'
