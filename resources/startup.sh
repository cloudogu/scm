#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

INIT_SCRIPT_FOLDER="/opt/scm-server/init.script.d"
MAIN_INIT_SCRIPTS_FOLDER="/var/tmp/scm/init.script.d"
CUSTOM_INIT_SCRIPTS_FOLDER="/var/lib/custom.init.script.d"

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

if ! [ -d "/var/lib/scm/config" ];  then
	mkdir -p "/var/lib/scm/config"
fi

# install plugins
if ! [ -d "/var/lib/scm/plugins" ];  then
	mkdir -p "/var/lib/scm/plugins"

  PLUGINS=(
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-gravatar-plugin/job/2.x/lastSuccessfulBuild/artifact/target/scm-gravatar-plugin-2.0.0-SNAPSHOT.smp"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-cas-plugin/job/develop/lastSuccessfulBuild/artifact/target/scm-cas-plugin-2.0.0-SNAPSHOT.smp"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-mail-plugin/job/2.0.0/lastSuccessfulBuild/artifact/target/scm-mail-plugin-2.0.0-SNAPSHOT.smp"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-jenkins-plugin/job/2.x/lastSuccessfulBuild/artifact/target/scm-jenkins-plugin-2.0-SNAPSHOT.smp"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-review-plugin/job/develop/lastSuccessfulBuild/artifact/target/scm-review-plugin-2.0.0-SNAPSHOT.smp"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-webhook-plugin/job/2.0.0/lastSuccessfulBuild/artifact/target/scm-webhook-plugin-2.0.0-SNAPSHOT.smp"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-tagprotection-plugin/job/feature%252Fv2/lastSuccessfulBuild/artifact/target/scm-tagprotection-plugin-2.0.0-SNAPSHOT.smp"

    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-rest-legacy-plugin/job/develop/lastSuccessfulBuild/artifact/target/scm-rest-legacy-plugin-2.0-SNAPSHOT.smp"

    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-issuetracker-plugin/job/2.0.0/lastSuccessfulBuild/artifact/target/scm-issuetracker-plugin-2.0.0-SNAPSHOT.smp"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-jira-plugin/job/2.x/lastSuccessfulBuild/artifact/target/scm-jira-plugin-2.0.0-SNAPSHOT.smp"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-redmine-plugin/job/2.0.0/lastSuccessfulBuild/artifact/target/scm-redmine-plugin-2.0.0-SNAPSHOT.smp"
  )

  for PLUGIN in "${PLUGINS[@]}"; do
    (cd "/var/lib/scm/plugins" && curl --silent -O "${PLUGIN}")
  done
fi


# Final startup
exec /opt/scm-server/bin/scm-server
