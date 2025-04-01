#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

# Script to perform a health check
#
# This script monitors the health of the SCM-Dogu. It uses `doguctl`
# and a HTTP endpoint to perform the health checks. This is necessary as SCM-Dogu provides no dedicated health
# endpoint at the moment.
#
# Notes:
# - Ensure that the script has execute permissions (`chmod +x health_check.sh`).
# - Configure the script in the Docker container as a health check using Docker's `HEALTHCHECK` instruction.
#
# Example:
#   ./health_check.sh

HEALTH_STATUS=0

# connection token
API_TOKEN=$(doguctl config --encrypted ${CES_TOKEN_CONFIGURATION_KEY})

# Perform health check against scm endpoint
HTTP_STATUS=$(curl --write-out "%{http_code}" --silent --output /dev/null --max-time 10 http://localhost:8080/scm/api/v2 -H "${CES_TOKEN_HEADER}: ${API_TOKEN}") || HTTP_STATUS=0
if [[ "$HTTP_STATUS" -ne 200 ]]; then
  echo "scm is unhealthy, received http status code ${HTTP_STATUS}"
  HEALTH_STATUS=1
fi

# Successful http check means tcp port is also available
doguctl state "ready"

# Perform health check using doguctl
if [[ "$HEALTH_STATUS" -eq 0 ]]; then
  if ! doguctl healthy -t 10 scm; then
    echo "doguctl is unhealthy"
    HEALTH_STATUS=1
  fi
fi

# Check general health status
if [[ "$HEALTH_STATUS" -ne 0 ]]; then
  doguctl state "unhealthy"
  exit 1
fi

doguctl state "ready"
exit 0