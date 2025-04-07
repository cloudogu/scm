#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

# Script to perform a health check
#
# This script monitors the health of the SCM-Dogu on startup.
#
# Notes:
# - Ensure that the script has execute permissions (`chmod +x healthcheck.sh`).
#
# Example:
#   ./healthcheck.sh

function runHealthCheck() {
  local api_token=${1}

  # Perform health check against scm endpoint
  HTTP_STATUS=$(curl --write-out "%{http_code}" --silent --output /dev/null --max-time 10 http://localhost:8080/scm/api/v2 -H "${CES_TOKEN_HEADER}: ${api_token}") || HTTP_STATUS=0
  if [[ "$HTTP_STATUS" -ne 200 ]]; then
    echo "scm is unhealthy, received http status code ${HTTP_STATUS}"
    return 1
  fi

  return 0
}

waitForHealth () {
  doguctl state "starting"

  local wait_timeout api_token
  wait_timeout=${1}
  api_token=${2}
  for i in $(seq 1 "${wait_timeout}"); do
    set +e
    local requestExitCode
    runHealthCheck "${api_token}"
    requestExitCode=$?
    set -e
    if [[ "${requestExitCode}" -eq 0 ]]; then
      echo "Reached SCM-Manager health endpoint successfully"
      doguctl state "ready"
      return 0
    fi
    if [[ "${i}" -eq ${wait_timeout} ]] ; then
      echo "SCM-Manager did not get healthy within ${wait_timeout} seconds. Dogu exits now"
      doguctl state "unhealthy"
      exit 1
    fi
    sleep 1
  done
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  waitForHealth "${1}" "${2}"
  exit $?
fi