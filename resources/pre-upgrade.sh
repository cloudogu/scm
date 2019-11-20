#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

OLD_VERSION="$1"
NEW_OLD_VERSION="$2"

IFS='-' read -ra version_parts <<< "$OLD_VERSION"

echo Found old version "${version_parts[0]}"-"${version_parts[1]}"

# When the old dogu version is less then 2.0.0-30, we have to remove the old plugin folder
if [ "${version_parts[0]}" == "2.0.0" ]; then
  MINOR_VERSION_DELETE_PLUGINS=30
  if [ "${version_parts[1]}" -lt "${MINOR_VERSION_DELETE_PLUGINS}" ]; then
    echo "Found old version less 2.0.0-${MINOR_VERSION_DELETE_PLUGINS}. Creating marker file."
    touch /var/lib/scm/plugins/delete_on_update
  else
    echo "Found old version greater or equal to 2.0.0-${MINOR_VERSION_DELETE_PLUGINS}. Nothing to do."
  fi
fi
