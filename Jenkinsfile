#!groovy
@Library(['github.com/cloudogu/dogu-build-lib@v1.0.0', 'github.com/cloudogu/zalenium-build-lib@3092363']) _
import com.cloudogu.ces.dogubuildlib.*

def NAMESPACES = ["testing", "itz-bund", "next", "official"]

node('vagrant') {

    timestamps {
        def props = [];
        props.add(string(defaultValue: '', description: 'Dogu Version', name: 'Version', trim: true));
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

//         stage('Lint') {
//              lintDockerfile()
//              shellCheck("./resources/pre-upgrade.sh ./resources/startup.sh ./resources/upgrade-notification.sh")
//         }

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

            stage('Push') {
                for (namespace in NAMESPACES) {
                    if (params."Push_${namespace}" != null && params."Push_${namespace}") {
                        ecoSystem.changeNamespace(namespace)
                        ecoSystem.build("/dogu")
                        ecoSystem.push("/dogu")
                    }
                }
            }

        } finally {
            stage('Clean') {
                ecoSystem.destroy()
            }
        }
    }
}
