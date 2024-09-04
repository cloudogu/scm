// this script configures the redmine plugin

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

String getPreferredRedmine() {
    String preferredRedmine = ecoSystem.getDoguConfig("redmine_type")
    if (preferredRedmine == null) {
        return "EASY_REDMINE"
    }

    return preferredRedmine
}

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

    String preferredRedmine = getPreferredRedmine()
    isEasyRedmineInstalled = ecoSystem.isInstalled("easyredmine")
    isRedmineInstalled = ecoSystem.isInstalled("redmine")

    if (isEasyRedmineInstalled && isRedmineInstalled && preferredRedmine.equals("EASY_REDMINE")) {
        println "both dogus installed and easy redmine is preferred"
        configureEasyRedmine(config, fqdn, formattingClass)
    } else if (isEasyRedmineInstalled && isRedmineInstalled && preferredRedmine.equals("REDMINE")) {
        println "both dogus installed and redmine is preferred"
        configureRedmine(config, fqdn, formattingClass)
    } else if (isEasyRedmineInstalled) {
        println "only easy redmine is installed"
        configureEasyRedmine(config, fqdn, formattingClass)
    } else if (isRedmineInstalled) {
        println "only redmine is installed"
        configureRedmine(config, fqdn, formattingClass)
    } else {
        println "no redmine dogu is installed"
    }

    store.storeConfiguration(config)
} catch (ClassNotFoundException e) {
    println "redmine plugin seems not to be installed"
}
