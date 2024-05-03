#!groovy
@Library(['github.com/cloudogu/dogu-build-lib@v1.4.1', 'github.com/cloudogu/zalenium-build-lib@3092363']) _
import com.cloudogu.ces.dogubuildlib.*

def NAMESPACES = ["testing", "official"]
def IGNORE_TAG = "ignore-tag"
def BUILD_TAG = "build-existing-tag"
def TAG_STRATEGIES = [IGNORE_TAG, BUILD_TAG]

node('vagrant') {

    String cronTrigger = BRANCH_NAME == "develop" ? "@daily" : ""

    timestamps {
        def props = [];
        props.add(string(defaultValue: getScmReleaseVersion(), description: 'SCM Version', name: 'ScmVersion', trim: true))
        props.add(string(defaultValue: getDoguReleaseCounter(), description: 'Dogu Version Counter', name: 'DoguVersionCounter', trim: true))
        props.add(choice(name: 'Tag_Strategy', choices: TAG_STRATEGIES))
        for (namespace in NAMESPACES) {
            props.add(booleanParam(defaultValue: isReleaseBuild(), description: "Push new dogu into registry with namespace '${namespace}'", name: "Push_${namespace}"))
        }
        properties([
                // Keep only the last x builds to preserve space
                buildDiscarder(logRotator(numToKeepStr: '10')),
                // Don't run concurrent builds for a branch, because they use the same workspace directory
                disableConcurrentBuilds(),
                parameters(props),
                pipelineTriggers([cron(cronTrigger)]),
        ])

        catchError {

            EcoSystem ecoSystem = new EcoSystem(this, "gcloud-ces-operations-internal-packer", "jenkins-gcloud-ces-operations-internal")

            stage('Checkout') {
                checkout([
                        $class                           : 'GitSCM',
                        branches                         : scm.branches,
                        doGenerateSubmoduleConfigurations: scm.doGenerateSubmoduleConfigurations,
                        extensions                       : scm.extensions + [[$class: 'CleanBeforeCheckout'], [$class: 'LocalBranch']],
                        userRemoteConfigs                : scm.userRemoteConfigs
                ])
            }

            stage('Check for tag') {
                if (params.Tag_Strategy != IGNORE_TAG) {
                    sh "git fetch --tags"
                    def tags = sh(returnStdout: true, script: 'git tag -l').trim().readLines()
                    if (params.Tag_Strategy == BUILD_TAG) {
                        if (!tags.contains(version)) {
                            error("Git tag ${version} does not exist.")
                        }
                        sh "git checkout 'refs/tags/${version}'"
                    } else if (isReleaseBuild() && tags.contains(version)) {
                        error("Git tag ${version} already exists.")
                    }
                }
            }

            stage('Apply Parameters') {
                if (version != null) {
                    ecoSystem.setVersion(version)
                }
                if (isNightly()) {
                    echo 'use snapshot dependencies for nightly build'
                    docker.image('groovy:3.0.9-jdk11').inside {
                        sh 'groovy build/latestsnapshot.groovy'
                    }
                } else if (isReleaseBuild()) {
                    echo 'update dependencies for release build'
                    docker.image('groovy:3.0.9-jdk11').inside {
                        sh "groovy build/release.groovy ${releaseVersion}"
                    }

                    sh 'git add Dockerfile dogu.json'
                    commit "Release version ${releaseVersion}"

                    // fetch all remotes from origin
                    sh 'git config --replace-all "remote.origin.fetch" "+refs/heads/*:refs/remotes/origin/*"'
                    sh 'git fetch --all'

                    // checkout, reset and merge
                    sh 'git checkout master'
                    sh 'git reset --hard origin/master'
                    sh "git merge --ff-only ${env.BRANCH_NAME}"

                    tag getVersion()
                }
            }

            stage('Lint') {
                // we cannot use the Dockerfile Linter because it fails without the labels `name` and `version` which we don't use
                // lintDockerfile()
                shellCheck("./resources/pre-upgrade.sh ./resources/startup.sh ./resources/upgrade-notification.sh")
            }

            try {

                stage('Provision') {
                    ecoSystem.provision("/dogu");
                }

                stage('Setup') {
                    ecoSystem.loginBackend('cesmarvin-setup')
                    ecoSystem.setup()
                }

                stage('Wait for dependencies') {
                    timeout(15) {
                        ecoSystem.waitForDogu("cas")
                        ecoSystem.waitForDogu("usermgt")
                    }
                }

                stage('Build') {
                    ecoSystem.build("/dogu")
                }

                stage('exit') {
                    sh "exit -1"
                }

            } finally {
                stage('Clean') {
                    ecoSystem.destroy()
                    if(isReleaseBuild()) {
                        sh "git tag -d ${getVersion()}"
                    }
                }
            }
        }
        if (currentBuild.currentResult == 'FAILURE') {
            mail to: "scm-team@cloudogu.com",
                    subject: "${JOB_NAME} - Build #${BUILD_NUMBER} - ${currentBuild.currentResult}!",
                    body: "Check console output at ${BUILD_URL} to view the results."
        }
    }
}


String getVersion() {
    if (params.ScmVersion != null && !params.ScmVersion.isEmpty()) {
        return "${params.ScmVersion}-${params.DoguVersionCounter}"
    }
    return null
}

boolean containsReleasePackage(release, packageType) {
    boolean exists = false
    for (int i=0; i < release.packages.size(); i++ ) {
        if (release.packages[i].type == packageType) {
            exists = true
            break
        }
    }
    return exists
}

boolean isNightly() {
    return currentBuild.getBuildCauses('hudson.triggers.TimerTrigger$TimerTriggerCause').size() > 0
}

boolean isReleaseBuild() {
    return env.BRANCH_NAME.startsWith("release/")
}

String getReleaseVersion() {
    if (isReleaseBuild()) {
        return env.BRANCH_NAME.substring("release/".length())
    }
    return ""
}

String getScmReleaseVersion() {
    String doguVersion = getReleaseVersion()
    int separator = doguVersion.lastIndexOf('-')
    if (separator > 0) {
        return doguVersion.substring(0, separator)
    }
    return ""
}

String getDoguReleaseCounter() {
    String doguVersion = getReleaseVersion()
    int separator = doguVersion.lastIndexOf('-')
    if (separator > 0) {
        return doguVersion.substring(separator + 1)
    }
    return '1'
}

void authGit(String credentials, String command) {
  withCredentials([
    usernamePassword(credentialsId: credentials, usernameVariable: 'AUTH_USR', passwordVariable: 'AUTH_PSW')
  ]) {
    sh "git -c credential.helper=\"!f() { echo username='\$AUTH_USR'; echo password='\$AUTH_PSW'; }; f\" ${command}"
  }
}

void commit(String message) {
  sh "git -c user.name='CES Marvin' -c user.email='cesmarvin@cloudogu.com' commit -m '${message}'"
}

void tag(String version) {
  String message = "Release version ${version}"
  sh "git -c user.name='CES Marvin' -c user.email='cesmarvin@cloudogu.com' tag -m '${message}' ${version}"
}
