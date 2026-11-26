#!/bin/bash
set -o errexit
set -o pipefail

if [ -z "$2" ]; then
    echo "usage remove-sa.sh irrelevant servicename"
    exit 1
fi

SERVICE="$2"

# connection token
API_TOKEN=$(doguctl config --encrypted ${CES_TOKEN_CONFIGURATION_KEY})

# sa-common.sh lies next to this script in the classic dogu (/) and in the scm-sa-manager sidecar (/hooks).
# shellcheck source=sa-common.sh
source "$(dirname "${BASH_SOURCE[0]}")/sa-common.sh"

# read users
for username in $(curl --silent http://localhost:8080/scm/api/v2/users\?fields=_embedded.users.name\&pageSize=999 -H "${CES_TOKEN_HEADER}: ${API_TOKEN}" | jq -r '._embedded.users | .[] | .name')
do
  if isServiceAccountOfConsumer "${username}" "${SERVICE}"
  then
    echo DELETE $username
    STATUSCODE=$(curl --silent http://localhost:8080/scm/api/v2/users/$username -X DELETE -H "${CES_TOKEN_HEADER}: ${API_TOKEN}" --write-out "%{http_code}")
    if [ $STATUSCODE -ne 204 ]; then
        echo failed deleting user $username: $STATUSCODE
        exit 1
    fi
  fi
done
