#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

# DoguV3-only init-container:
#   - Fix ownership of the persistent volumes for the nexus user (uid/gid 1000)

mkdir -p /var/lib/nexus /var/ces/config

# --- persistence ownership -------------------------------------------------
# Must run last (see header comment): fixes up anything doguctl created as root above.
chown -R 1000:1000 /var/lib/nexus /var/ces/config

echo "set ownership for /var/lib/nexus and /var/ces/config"
