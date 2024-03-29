#!/bin/bash
set -o errexit
set -o pipefail

if [ -z "$2" ]; then
    echo "usage create-sa.sh <ro|rw|admin> servicename"
    exit 1
fi

SERVICE="$2"

if [[ "$1" == "ro" ]]; then
    PERMISSION="repository:read,pull:*"
elif [[ "$1" == "rw" ]]; then
    PERMISSION="repository:*"
elif [[ "$1" == "admin" ]]; then
    PERMISSION="*"
else
    echo "usage create-sa.sh <ro|rw|admin> servicename"
    exit 1
fi

# create random username suffix and password
ID=$(doguctl random -l 6 | tr '[:upper:]' '[:lower:]')
USER="${SERVICE}_${ID}"
PASSWORD=$(doguctl random)

# connection token
API_TOKEN=$(doguctl config --encrypted ${CES_TOKEN_CONFIGURATION_KEY})

# create user
STATUSCODE=$(curl --silent http://localhost:8080/scm/api/v2/users -H "${CES_TOKEN_HEADER}: ${API_TOKEN}" --data '{"name":"'${USER}'","displayName":"CES Serviceaccount for '${SERVICE}'","active":true,"password":"'${PASSWORD}'"}' -H "Content-Type: application/vnd.scmm-user+json;v=2" --write-out "%{http_code}")
if [ $STATUSCODE -ne 201 ]; then
    echo failed creating user: $STATUSCODE
    exit 2
fi

# create permission
STATUSCODE=$(curl --silent http://localhost:8080/scm/api/v2/users/${USER}/permissions -H "${CES_TOKEN_HEADER}: ${API_TOKEN}" --data '{"permissions":["'${PERMISSION}'"]}' -H "Content-Type: application/vnd.scmm-permissionCollection+json;v=2" -X PUT --write-out "%{http_code}")
if [ $STATUSCODE -ne 204 ]; then
    echo failed setting permission: $STATUSCODE
    exit 3
fi

# print details
echo "username: ${USER}"
echo "password: ${PASSWORD}"
