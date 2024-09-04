// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

// this script configures the jenkins plugin

def findClass(clazzAsString) {
    return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

try {
    if (ecoSystem.isInstalled("jenkins")) {
        def jenkins = injector.getInstance(findClass("sonia.scm.jenkins.JenkinsContext"));
        def config = jenkins.getConfiguration();

        String fqdn = ecoSystem.getGlobalConfig("fqdn");
        config.url = "https://${fqdn}/jenkins";

        jenkins.storeConfiguration(config);
    }
} catch (ClassNotFoundException e) {
    println "jenkins plugin seems not to be installed"
}
