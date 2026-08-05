#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PACKAGE_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

log_step() {
  printf '\n==> %s\n' "$1"
}

log_ok() {
  printf '[OK] %s\n' "$1"
}

log_warn() {
  printf '[WARN] %s\n' "$1" >&2
}

die() {
  printf '[ERROR] %s\n' "$1" >&2
  exit 1
}

detect_platform() {
  case "$(uname -s)" in
    Linux) printf 'ubuntu' ;;
    Darwin) printf 'macos' ;;
    *) die "Unsupported platform: $(uname -s)" ;;
  esac
}

default_deploy_root() {
  case "$(detect_platform)" in
    ubuntu) printf '%s/zh-aqy' "${HOME}" ;;
    macos) printf '%s/zh-aqy' "${HOME}" ;;
  esac
}

command_path() {
  command -v "$1" 2>/dev/null || true
}

find_java() {
  local java_path
  java_path="$(command_path java)"
  if [[ -n "${java_path}" ]]; then
    printf '%s' "${java_path}"
    return 0
  fi

  local candidate
  for candidate in \
    /usr/lib/jvm/java-8-openjdk-*/bin/java \
    /usr/lib/jvm/temurin-8-*/bin/java \
    /Library/Java/JavaVirtualMachines/temurin-8.jdk/Contents/Home/bin/java \
    /Library/Java/JavaVirtualMachines/jdk1.8*.jdk/Contents/Home/bin/java
  do
    for java_path in ${candidate}; do
      if [[ -x "${java_path}" ]]; then
        printf '%s' "${java_path}"
        return 0
      fi
    done
  done

  return 1
}

java_is_version_8() {
  local java_path="$1"
  local version_text
  version_text="$("${java_path}" -version 2>&1 || true)"
  [[ "${version_text}" =~ version\ \"1\.8\. || "${version_text}" =~ version\ \"8\. ]]
}

find_mysql_cli() {
  local mysql_path
  mysql_path="$(command_path mysql)"
  if [[ -n "${mysql_path}" ]]; then
    printf '%s' "${mysql_path}"
    return 0
  fi
  return 1
}

find_nginx() {
  local nginx_path
  nginx_path="$(command_path nginx)"
  if [[ -n "${nginx_path}" ]]; then
    printf '%s' "${nginx_path}"
    return 0
  fi
  return 1
}

test_tcp_port() {
  local host="$1"
  local port="$2"
  if command -v python3 >/dev/null 2>&1; then
    python3 - "$host" "$port" <<'PY'
import socket
import sys

host = sys.argv[1]
port = int(sys.argv[2])
sock = socket.socket()
sock.settimeout(3)
try:
    sock.connect((host, port))
except OSError:
    sys.exit(1)
finally:
    sock.close()
PY
    return $?
  fi

  if command -v nc >/dev/null 2>&1; then
    nc -z -w 3 "$host" "$port" >/dev/null 2>&1
    return $?
  fi

  return 1
}

random_secret() {
  local bytes="${1:-32}"
  if command -v openssl >/dev/null 2>&1; then
    openssl rand -base64 "${bytes}" | tr '+/' 'AB' | tr -d '=\n'
    return 0
  fi

  LC_ALL=C tr -dc 'A-Za-z0-9' </dev/urandom | head -c "$((bytes * 2))"
}

safe_identifier() {
  local value="$1"
  local name="$2"
  [[ "${value}" =~ ^[A-Za-z0-9_]+$ ]] || die "${name} may only contain letters, numbers, and underscores: ${value}"
}

escape_mysql_string() {
  printf '%s' "$1" | sed "s/\\\\/\\\\\\\\/g; s/'/''/g"
}

package_required_files() {
  cat <<'EOF'
server/aqy-admin.jar
web/index.html
sql/ry_20240629.sql
sql/quartz.sql
sql/zh_aqy_schema.sql
bin/unix-common.sh
bin/unix-preflight-check.sh
bin/unix-install-prerequisites.sh
bin/unix-fresh-test-deploy.sh
bin/unix-upgrade-existing.sh
docker-compose.yml
docker-compose.fresh.yml
docker-compose.migrate.yml
bin/docker-common.sh
bin/docker-generate-env.sh
bin/docker-migrate-existing-db.sh
bin/docker-backup-db.sh
bin/docker-restore-db.sh
bin/docker-status.sh
bin/docker-stop.sh
deploy/docker/backend.Dockerfile
deploy/docker/nginx.conf
deploy/docker/mysql-init/00-import-release-sql.sh
EOF
}

check_package_files() {
  local missing=0
  while IFS= read -r relative_path; do
    [[ -z "${relative_path}" ]] && continue
    if [[ -f "${PACKAGE_ROOT}/${relative_path}" ]]; then
      log_ok "${relative_path}"
    else
      printf '[ERROR] Missing release package file: %s\n' "${relative_path}" >&2
      missing=1
    fi
  done < <(package_required_files)

  [[ "${missing}" -eq 0 ]] || return 1
}

write_env_file() {
  local output_file="$1"
  shift
  : >"${output_file}"
  local pair key value
  for pair in "$@"; do
    key="${pair%%=*}"
    value="${pair#*=}"
    printf 'export %s=%q\n' "${key}" "${value}" >>"${output_file}"
  done
}

load_env_file_if_exists() {
  local env_file="$1"
  if [[ -f "${env_file}" ]]; then
    # shellcheck disable=SC1090
    source "${env_file}"
  fi
}
