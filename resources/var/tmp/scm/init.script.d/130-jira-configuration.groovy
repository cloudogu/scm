// this script configures the jira plugin

import groovy.json.JsonSlurper;

// TODO sharing ?
def getValueFromEtcd(String key) {
    String ip = new File("/etc/ces/node_master").getText("UTF-8").trim()
    URL url = new URL("http://${ip}:4001/v2/keys/${key}")
    def json = new JsonSlurper().parseText(url.text)
    return json.node.value
}

def findClass(clazzAsString) {
    return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

def isDoguInstalled(name) {
    String ip = new File("/etc/ces/node_master").getText("UTF-8").trim();
    URL url = new URL("http://${ip}:4001/v2/keys/dogu/${name}/current");
    return url.openConnection().getResponseCode() == 200;
}

def configureJira(config, fqdn) {
    if (config.getUrl() == null) {
        // do not enabled commenting and state changes,
        // because we need a technical account on jira before
        config.setUpdateIssues(false)
    }
    config.setUrl("https://${fqdn}/jira")
}

if (isDoguInstalled("jira")) {
    try {
        def storeClass = findClass("sonia.scm.jira.config.JiraConfigurationStore")
        def store = injector.getInstance(storeClass);

        String fqdn = getValueFromEtcd("config/_global/fqdn")
        def config = store.getGlobalConfiguration()
        configureJira(config, fqdn)

        store.setGlobalConfiguration(config)
    } catch (ClassNotFoundException e) {
        println "jira plugin seems not to be installed"
    }
}
