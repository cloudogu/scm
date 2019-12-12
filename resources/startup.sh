#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

INIT_SCRIPT_FOLDER="/opt/scm-server/init.script.d"
MAIN_INIT_SCRIPTS_FOLDER="/var/tmp/scm/init.script.d"
CUSTOM_INIT_SCRIPTS_FOLDER="/var/lib/custom.init.script.d"
SCM_DATA="/var/lib/scm"
SCM_REQUIRED_PLUGINS="/opt/scm-server/required-plugins"

# remove old folder to be sure, 
# that it contains no script which is already removed from custom init script folder
if [ -d "${INIT_SCRIPT_FOLDER}" ]; then
  rm -rf "${INIT_SCRIPT_FOLDER}"
fi

# copy fresh main init scripts
mkdir -p "${INIT_SCRIPT_FOLDER}"
cp -rf "${MAIN_INIT_SCRIPTS_FOLDER}"/*.groovy "${INIT_SCRIPT_FOLDER}/"

# merge custom init scripts, if the volume is not empty
if [ "$(ls -A ${CUSTOM_INIT_SCRIPTS_FOLDER}/*.groovy)" ]; then
  cp "${CUSTOM_INIT_SCRIPTS_FOLDER}/"*.groovy "${INIT_SCRIPT_FOLDER}/"
fi

# create truststore, which is used in the default file
create_truststore.sh /opt/scm-server/conf/truststore.jks > /dev/null

# create ca certificate store for mercurial
create-ca-certificates.sh /opt/scm-server/conf/ca-certificates.crt

if ! [ -d "${SCM_DATA}/config" ];  then
	mkdir -p "${SCM_DATA}/config"
fi

# delete outdated plugins
if [ -a "${SCM_DATA}/plugins/delete_on_update" ];  then
  rm -rf "${SCM_DATA}/plugins"
fi

start_scm_server () {
  # install required plugins
  if ! [ -d "${SCM_DATA}/plugins" ];  then
    mkdir "${SCM_DATA}/plugins"
  fi
  if { ! [ -d "${SCM_DATA}/plugins/scm-cas-plugin" ] || [ -a "${SCM_DATA}/plugins/scm-cas-plugin/uninstall" ] ; } && ! [ -a "${SCM_DATA}/plugins/scm-cas-plugin.smp" ] ;  then
    echo "Reinstalling scm-cas-plugin from default plugin folder"
    cp "${SCM_REQUIRED_PLUGINS}/scm-cas-plugin.smp" "${SCM_DATA}/plugins"
  fi
  if { ! [ -d "${SCM_DATA}/plugins/scm-script-plugin" ] || [ -a "${SCM_DATA}/plugins/scm-script-plugin/uninstall" ] ; } && ! [ -a "${SCM_DATA}/plugins/scm-script-plugin.smp" ] ;  then
    echo "Reinstalling scm-script-plugin from default plugin folder"
    cp "${SCM_REQUIRED_PLUGINS}/scm-script-plugin.smp" "${SCM_DATA}/plugins"
  fi

  /opt/scm-server/bin/scm-server
}

# Final startup

while start_scm_server ; scm_exit_code=$? ; [ $scm_exit_code -eq 42 ] ; do
  echo Got exit code $scm_exit_code -- restarting SCM-Manager
done
exit $scm_exit_code
