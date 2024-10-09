// this script configures the jira plugin

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

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

if (ecoSystem.isInstalled("jira")) {
    try {
        def storeClass = findClass("sonia.scm.jira.config.JiraConfigurationStore")
        def store = injector.getInstance(storeClass);

        String fqdn = ecoSystem.getGlobalConfig("fqdn")
        def config = store.getGlobalConfiguration()
        configureJira(config, fqdn)

        store.setGlobalConfiguration(config)
    } catch (ClassNotFoundException e) {
        println "jira plugin seems not to be installed"
    }
}
