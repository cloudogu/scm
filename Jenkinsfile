#!groovy
@Library(['github.com/cloudogu/dogu-build-lib@v1.4.1', 'github.com/cloudogu/zalenium-build-lib@3092363']) _
import com.cloudogu.ces.dogubuildlib.*

def NAMESPACES = ["testing", "itz-bund", "next", "official"]
def IGNORE_TAG = "ignore-tag"
def CREATE_NEW_TAG = "create-new-tag"
def BUILD_TAG = "build-existing-tag"
def TAG_STRATEGIES = [IGNORE_TAG, CREATE_NEW_TAG, BUILD_TAG]

node('vagrant') {

    timestamps {
        def props = [];
        props.add(string(defaultValue: '', description: 'SCM Version', name: 'ScmVersion', trim: true))
        props.add(string(defaultValue: '1', description: 'Dogu Version Counter', name: 'DoguVersionCounter', trim: true))
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
                        if (!tags.contains(version)) {
                            error("Git tag ${version} does not exist.")
                        }
                        sh "git checkout 'refs/tags/${version}'"
                    } else if (params.Tag_Strategy == CREATE_NEW_TAG && tags.contains(version)) {
                        error("Git tag ${version} already exists.")
                    }
                }
            }

            stage('Lint') {
//             we cannot use the Dockerfile Linter because it fails without the labels `name` and `version` which we don't use
//             lintDockerfile()
                shellCheck("./resources/pre-upgrade.sh ./resources/startup.sh ./resources/upgrade-notification.sh")
            }

            stage('Apply Parameters') {
                if (version != null) {
                    ecoSystem.setVersion(version)
                }
                // TODO only on nightly build
                docker.image('groovy:3.0.9-jdk11').inside {
                    sh "groovy build/latestsnapshot.groovy"
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

                    String externalIP = ecoSystem.externalIP
                    timeout(time: 15, unit: 'MINUTES') {
                        try {
                            withZalenium { zaleniumIp ->
                                dir('integrationTests') {
                                    docker.image('cloudogu/gauge-java:1.0.4')
                                            .inside("-e WEBDRIVER=remote -e CES_FQDN=${externalIP} -e SELENIUM_BROWSER=chrome -e SELENIUM_REMOTE_URL=http://${zaleniumIp}:4444/wd/hub") {
                                                sh 'mvn test'
                                            }
                                }
                            }
                        } finally {
                            // archive test results
                            junit allowEmptyResults: true, testResults: 'integrationTests/target/gauge/xml-report/result.xml'
                        }
                    }
                }

                stage('Push changes to remote repository') {
                    if (params.Tag_Strategy == CREATE_NEW_TAG) {
                        // create new tag
                        sh "git -c user.name='CES_Marvin' -c user.email='cesmarvin@cloudogu.com' tag -m '${version}' ${version}"

                        // push changes back to remote repository
                        withCredentials([usernamePassword(credentialsId: 'cesmarvin', usernameVariable: 'GIT_AUTH_USR', passwordVariable: 'GIT_AUTH_PSW')]) {
                            sh "git -c credential.helper=\"!f() { echo username='\$GIT_AUTH_USR'; echo password='\$GIT_AUTH_PSW'; }; f\" push origin ${version}"
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

                stage('Website') {
                    echo "update website for ${version}"
                    if (params.Push_official) {
                        dir('website') {
                            git branch: 'master', changelog: false, credentialsId: 'cesmarvin', poll: false, url: 'https://github.com/scm-manager/website.git'

                            String releaseFile = "content/releases/${params.ScmVersion.replace('.', '-')}.yml"

                            def release = readYaml file: releaseFile
                            if (!containsReleasePackage(release, 'ces')) {
                                release.packages.add([type: 'ces'])
                                writeYaml file: releaseFile, data: release, overwrite: true
                                sh "git add ${releaseFile}"
                                sh "git -c user.name='CES_Marvin' -c user.email='cesmarvin@cloudogu.com' commit -m 'Add ces package to release ${params.ScmVersion}' ${releaseFile}"
                                authGit 'cesmarvin', 'push origin master'

                            } else {
                                echo "release ${params.ScmVersion} contains ces package already"
                            }
                        }
                    } else {
                        echo 'we only update the website if the official dogu is pushed'
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

void authGit(String credentials, String command) {
  withCredentials([
    usernamePassword(credentialsId: credentials, usernameVariable: 'AUTH_USR', passwordVariable: 'AUTH_PSW')
  ]) {
    sh "git -c credential.helper=\"!f() { echo username='\$AUTH_USR'; echo password='\$AUTH_PSW'; }; f\" ${command}"
  }
}
