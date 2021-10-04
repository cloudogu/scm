#!/bin/bash
set -o errexit
set -o pipefail

if [ -z "$1" ]; then
    echo "usage create-sa.sh servicename"
    exit 1
fi

SERVICE="$1"

# connection token
API_TOKEN=$(doguctl config --encrypted ${CES_TOKEN_CONFIGURATION_KEY})

# read users
USERMATCH=${SERVICE}_[a-zA-Z0-9]{6}

for username in $(curl --silent http://localhost:8080/scm/api/v2/users\?fields=_embedded.users.name\&pageSize=999 -H "${CES_TOKEN_HEADER}: ${API_TOKEN}" | jq -r '._embedded.users | .[] | .name')
do
  if [[ $username =~ $USERMATCH ]]
  then
    echo DELETE $username
    curl --silent http://localhost:8080/scm/api/v2/users/$username -X DELETE -H "${CES_TOKEN_HEADER}: ${API_TOKEN}"
  fi
done
