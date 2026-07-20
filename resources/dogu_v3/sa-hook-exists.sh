#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

source /hooks/sa-hook-common.sh

# Checks whether a service account for the given consumer currently exists by using doguctl.
#
# Exit code convention:
#   0 - the service account exists
#   1 - the service account does not exist

if [ $# -ne 1 ]; then
  echo "usage sa-hook-exists.sh <service name>" >&2
  exit 2
fi

CONSUMER="$1"

if getExistingUserForConsumer "${CONSUMER}"; then
  exit 0
else
  exit 1
fi
