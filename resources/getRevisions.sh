#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

PLUGINS=(
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-gravatar-plugin/job/2.x/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-cas-plugin/job/develop/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-mail-plugin/job/2.0.0/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-jenkins-plugin/job/2.x/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-review-plugin/job/develop/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-webhook-plugin/job/2.0.0/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-tagprotection-plugin/job/develop/lastSuccessfulBuild"

    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-rest-legacy-plugin/job/develop/lastSuccessfulBuild"

    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-issuetracker-plugin/job/2.0.0/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-jira-plugin/job/2.x/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-redmine-plugin/job/2.0.0/lastSuccessfulBuild"

    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-activity-plugin/job/2.0.0/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-statistic-plugin/job/2.0.0/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-cockpit-legacy-plugin/job/develop/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-pathwp-plugin/job/2.0.0/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-branchwp-plugin/job/2.0.0/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-notify-plugin/job/2.0.0/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-authormapping-plugin/job/2.0.0/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-groupmanager-plugin/job/develop/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-pushlog-plugin/job/2.0.0/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-script-plugin/job/2.0.0/lastSuccessfulBuild"
    "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-bitbucket/job/scm-support-plugin/job/2.0.0/lastSuccessfulBuild"
)

mkdir -p revisions

for PLUGIN in "${PLUGINS[@]}"; do
  RELEASE_FILE=$(echo $PLUGIN | awk -F '/' '{print $10}')
  echo ${RELEASE_FILE}
  curl -g --silent "${PLUGIN}/api/xml?xpath=/*/action[@_class='hudson.plugins.git.util.BuildData']/lastBuiltRevision/SHA1" > revisions/${RELEASE_FILE}
done

curl -g --silent "https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-2.x/job/2.0.0-m3/lastSuccessfulBuild/api/xml?xpath=/*/action[@_class='hudson.plugins.git.util.BuildData']/lastBuiltRevision/SHA1" > revisions/scm-manager