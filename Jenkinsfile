#!groovy
@Library(['github.com/cloudogu/dogu-build-lib@36c827530', 'github.com/cloudogu/zalenium-build-lib@3092363']) _
import com.cloudogu.ces.dogubuildlib.*

def NAMESPACES = ["testing", "itz-bund", "next", "official"]
def IGNORE_TAG = "ignore-tag"
def CREATE_NEW_TAG = "create-new-tag"
def BUILD_TAG = "build-existing-tag"
def TAG_STRATEGIES = [IGNORE_TAG, CREATE_NEW_TAG, BUILD_TAG]

node('vagrant') {

    timestamps {
        def props = [];
        props.add(string(defaultValue: '', description: 'Dogu Version', name: 'Version', trim: true))
        props.add(choice(name: 'Tag_Strategy', choices: TAG_STRATEGIES))
        for (namespace in NAMESPACES) {
            props.add(booleanParam(defaultValue: false, description: "Push new dogu into registry with namespace '${namespace}'", name: "Push_${namespace}"))
        }
        properties([
                // Keep only the last x builds to preserve space
                buildDiscarder(logRotator(numToKeepStr: '10')),
                // Don't run concurrent builds for a branch, because they use the same workspace directory
                disableConcurrentBuilds(),
                parameters(props)
        ])

        catchError {

            EcoSystem ecoSystem = new EcoSystem(this, "gcloud-ces-operations-internal-packer", "jenkins-gcloud-ces-operations-internal")

            stage('Checkout') {
                checkout([
                        $class                           : 'GitSCM',
                        branches                         : scm.branches,
                        doGenerateSubmoduleConfigurations: scm.doGenerateSubmoduleConfigurations,
                        extensions                       : scm.extensions + [[$class: 'CleanBeforeCheckout']],
                        userRemoteConfigs                : scm.userRemoteConfigs
                ])
            }

            stage('Check for tag') {
                if (params.Tag_Strategy != IGNORE_TAG) {
                    sh "git fetch --tags"
                    def tags = sh(returnStdout: true, script: 'git tag -l').trim().readLines()
                    if (params.Tag_Strategy == BUILD_TAG) {
                        if (!tags.contains(params.Version)) {
                            error("Git tag ${params.Version} does not exist.")
                        }
                        sh "git checkout 'refs/tags/${params.Version}'"
                    } else if (params.Tag_Strategy == CREATE_NEW_TAG && tags.contains(params.Version)) {
                        error("Git tag ${params.Version} already exists.")
                    }
                }
            }

            stage('Lint') {
//             we cannot use the Dockerfile Linter because it fails without the labels `name` and `version` which we doesn't use
//             lintDockerfile()
                shellCheck("./resources/pre-upgrade.sh ./resources/startup.sh ./resources/upgrade-notification.sh")
            }

            stage('Apply Parameters') {
                if (params.Version != null && !params.Version.isEmpty()) {
                    ecoSystem.setVersion(params.Version)
                }
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

                stage('Verify') {
                    ecoSystem.verify("/dogu")
                }

                stage('e2e Tests') {
                    ecoSystem.runMavenIntegrationTests(15)
                }

                stage('Push changes to remote repository') {
                    if (params.Tag_Strategy == CREATE_NEW_TAG) {
                        // create new tag
                        sh "git -c user.name='CES_Marvin' -c user.email='cesmarvin@cloudogu.com' tag -m '${params.Version}' ${params.Version}"

                        // push changes back to remote repository
                        withCredentials([usernamePassword(credentialsId: 'cesmarvin', usernameVariable: 'GIT_AUTH_USR', passwordVariable: 'GIT_AUTH_PSW')]) {
                            sh "git -c credential.helper=\"!f() { echo username='\$GIT_AUTH_USR'; echo password='\$GIT_AUTH_PSW'; }; f\" push origin ${params.Version}"
                        }
                    }
                }

                stage('Push') {
                    // No dogu release without tag allowed
                    if (params.Tag_Strategy != IGNORE_TAG) {
                        for (namespace in NAMESPACES) {
                            if (params."Push_${namespace}" != null && params."Push_${namespace}") {
                                ecoSystem.purge("scm")
                                ecoSystem.changeNamespace(namespace, "/dogu")
                                ecoSystem.build("/dogu")
                                ecoSystem.push("/dogu")
                            }
                        }
                    }
                }

            } finally {
                stage('Clean') {
                    ecoSystem.destroy()
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
