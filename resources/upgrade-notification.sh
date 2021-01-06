#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

FROM_VERSION="${1}"
TO_VERSION="${2}"

if [[ ${FROM_VERSION} == "1."* ]] && [[ ${TO_VERSION} == "2."* ]]; then
  echo "############################## MAJOR UPGRADE ##############################"
  echo "You are upgrading your SCM-Manager instance from \"${FROM_VERSION}\" to \"${TO_VERSION}\"."
  echo "The currently installed plugins will be re-installed in SCM-Manager \"${TO_VERSION}\" in the newest compatible version available."
  echo "Not all plugins which have been available in SCM-Manager \"${FROM_VERSION}\" are also available in SCM-Manager \"${TO_VERSION}\". The plugin data will be migrated automatically."
  echo "ATTENTION: Right after the upgrade you need to migrate your repository data via the SCM-Manager migration wizard!"
  echo "Please consider backing up your SCM-Manager volumes before starting the migration. Upgrade problems are rare, but you'll want the backup if anything does happen."
fi
