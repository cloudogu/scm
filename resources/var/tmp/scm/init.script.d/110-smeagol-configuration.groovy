// this script configures the smeagol plugin
import sonia.scm.*;

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

def findClass(clazzAsString) {
    return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

def setSmeagolConfig() {
    String fqdn = ecoSystem.getGlobalConfig("fqdn")
    String smeagolUrl = "https://${fqdn}/smeagol"

    def smeagolConfiguration = injector.getInstance(findClass("com.cloudogu.smeagol.SmeagolConfiguration"))
    def currentConfig = smeagolConfiguration.get()
    currentConfig.setSmeagolUrl(smeagolUrl)
    currentConfig.setEnabled(true)
    smeagolConfiguration.set(currentConfig)
}

try {
    if (ecoSystem.isInstalled("smeagol")) {
        setSmeagolConfig()
    }
} catch (ClassNotFoundException e) {
    println "Smeagol plugin seems not to be installed"
}
