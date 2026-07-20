// this script configures the gotenberg plugin

import sonia.scm.*;

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

def findClass(clazzAsString) {
    return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

def setGotenbergConfig() {
    def gotenbergConfigurationStore = injector.getInstance(findClass("com.cloudogu.scm.gotenberg.GotenbergConfigurationStore"))

    def gotenbergConfiguration = gotenbergConfigurationStore.get()

    def gotenbergServiceName = System.getenv("GOTENBERG_SERVICE_NAME") ?: "gotenberg"
    gotenbergConfiguration.setUrl("http://${gotenbergServiceName}:3000/")
    gotenbergConfiguration.setEnabled(true)

    gotenbergConfigurationStore.set(gotenbergConfiguration)
}

try {

    def isEnabled = System.getenv("GOTENBERG_PLUGIN_ENABLED") != "false"
    if (isEnabled || ecoSystem.isInstalled("gotenberg")) {
        System.out.println  "Gotenberg is installed, configuring gotenberg plugin"
        setGotenbergConfig()
    }
} catch (ClassNotFoundException e) {
    println "Gotenberg plugin seems not to be installed"
}
