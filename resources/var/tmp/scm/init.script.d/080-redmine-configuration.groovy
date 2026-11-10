// this script configures the redmine plugin

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

def findClass(clazzAsString) {
    return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

def configureEasyRedmine(config, fqdn, formattingClass) {
    def formatting

    try {
        formatting = Enum.valueOf(formattingClass, "HTML")
    } catch (IllegalArgumentException ex) {
        println "Could not resolve HTML formatting. Set MARKDOWN instead."
        formatting = Enum.valueOf(formattingClass, "MARKDOWN")
    }

    config.setTextFormatting(formatting)
    config.setUrl("https://${fqdn}/easyredmine")
}

def configureRedmine(config, fqdn, formattingClass) {
    def formatting = Enum.valueOf(formattingClass, "MARKDOWN")
    config.setTextFormatting(formatting)
    config.setUrl("https://${fqdn}/redmine")
}

try {
    def storeClass = findClass("sonia.scm.redmine.config.RedmineConfigStore")
    def store = injector.getInstance(storeClass);

    String fqdn = ecoSystem.getGlobalConfig("fqdn")

    def config = store.getConfiguration()
    if (config.getUrl() == null) {
        // do not enabled commenting and state changes,
        // because we need a technical account on redmine before
        config.setUpdateIssues(false)
        config.setAutoClose(false)
    }

    def formattingClass = findClass("sonia.scm.redmine.config.TextFormatting")

    String redmineIntegration = ecoSystem.getRedmineIntegration()

    if (redmineIntegration == "EASY_REDMINE") {
        println "configuring redmine plugin for easy redmine"
        configureEasyRedmine(config, fqdn, formattingClass)
    } else if (redmineIntegration == "REDMINE") {
        println "configuring redmine plugin for redmine"
        configureRedmine(config, fqdn, formattingClass)
    } else {
        println "redmine integration is not active"
    }

    store.storeConfiguration(config)
} catch (ClassNotFoundException e) {
    println "redmine plugin seems not to be installed"
}
