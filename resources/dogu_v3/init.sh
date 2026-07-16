#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

# DoguV3-only init-container:
#   1. Materialize the /etc/ces/dogu_json/scm/{current,<version>} layout that doguctl expects
#   2. Fix ownership of the persistent volumes for the scm user (uid/gid 1000)

mkdir -p /var/lib/scm /var/ces/config

# --- 1. dogu_json layout ------------------------------------------------------
# doguctl resolves the descriptor from /etc/ces/dogu_json/${HOSTNAME}/.
# As a StatefulSet the pod hostname is the pod name (e.g. scm-0), NOT a fixed "scm"
# (the controller overrides spec.hostname), so the descriptor dir must follow ${HOSTNAME}.
TARGET_DIR="/etc/ces/dogu_json/${HOSTNAME}"
SOURCE_DOGU_JSON="/dogu.json"

# Take the first "Version" line
DOGU_VERSION="$(awk -F'"' '/"Version"[[:space:]]*:/ {print $4; exit}' "${SOURCE_DOGU_JSON}")"
if [ -z "${DOGU_VERSION}" ]; then
  echo "unable to determine dogu version from ${SOURCE_DOGU_JSON}" >&2
  exit 1
fi

mkdir -p "${TARGET_DIR}"
printf '%s' "${DOGU_VERSION}" > "${TARGET_DIR}/current"
cp "${SOURCE_DOGU_JSON}" "${TARGET_DIR}/${DOGU_VERSION}"

echo "prepared dogu.json in ${TARGET_DIR} for to be used by doguctl"

# --- persistence ownership -------------------------------------------------
# Must run last (see header comment): fixes up anything doguctl created as root above.
chown -R 1000:1000 /var/lib/scm /var/ces/config

echo "set ownership for /var/lib/scm and /var/ces/config"
