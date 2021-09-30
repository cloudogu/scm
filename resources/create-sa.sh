#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

{
    SERVICE="$1"
    if [ X"${SERVICE}" = X"" ]; then
        echo "usage create-sa.sh servicename [permission]"
        exit 1
    fi

    PERMISSION="$2"
    if [ X"${PERMISSION}" = X"" ]; then
        PERMISSION="repository:read,pull:*"
    fi

    # create random username suffix and password
    ID=$(doguctl random -l 6 | tr '[:upper:]' '[:lower:]')
    USER="${SERVICE}_${ID}"
    PASSWORD=$(doguctl random)

    # connection token
    API_TOKEN=$(doguctl config --encrypted serviceaccount_token)

    # create user
    curl -v http://localhost:8081/scm/api/v2/ces/serviceaccount -H "X-CES-Token: ${API_TOKEN}" --data '{"username":"'${USER}'","password":"'${PASSWORD}'", "permission":"'${PERMISSION}'"}' -H "Content-Type: application/vnd.scmm-serviceaccount+json"

} >/dev/null 2>&1

# print details
echo "username: ${USER}"
echo "password: ${PASSWORD}"
