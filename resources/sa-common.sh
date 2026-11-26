#!/bin/bash
# Shared by remove-sa.sh (classic dogu and dogu v3) and dogu_v3/sa-hook-common.sh.
# It lies next to remove-sa.sh in both: in / of the image and in /hooks of the scm-sa-manager sidecar.

#######################################
# Checks whether a user is the service account of a consumer.
# Dogu v3 names it <consumer>_SCM_SA. Dogu v2 named it <consumer>_ with a random suffix of six lowercase
# characters or digits. Both are matched, so that a service account migrated from dogu v2 is kept instead of
# being replaced by a new one, and is removed with the consumer.
# ARGUMENTS:
#   #1 - the user name
#   #2 - the consumer
# RETURNS:
#   0 if the user is the service account of the consumer, 1 otherwise
#######################################
function isServiceAccountOfConsumer() {
  local username="$1"
  local consumer="$2"
  [[ "${username}" =~ ^${consumer}_(SCM_SA|[a-z0-9]{6})$ ]]
}
