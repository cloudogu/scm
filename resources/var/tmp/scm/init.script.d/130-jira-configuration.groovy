// this script configures the jira plugin


import lib.EcoSystem.DoguRegistry;
import lib.EcoSystem.GlobalConfig;

def findClass(clazzAsString) {
    return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

def configureJira(config, fqdn) {
    if (config.getUrl() == null) {
        // do not enabled commenting and state changes,
        // because we need a technical account on jira before
        config.setUpdateIssues(false)
    }
    config.setUrl("https://${fqdn}/jira")
}

if (DoguRegistry.isInstalled("jira")) {
    try {
        def storeClass = findClass("sonia.scm.jira.config.JiraConfigurationStore")
        def store = injector.getInstance(storeClass);

        String fqdn = GlobalConfig.get("fqdn")
        def config = store.getGlobalConfiguration()
        configureJira(config, fqdn)

        store.setGlobalConfiguration(config)
    } catch (ClassNotFoundException e) {
        println "jira plugin seems not to be installed"
    }
}
