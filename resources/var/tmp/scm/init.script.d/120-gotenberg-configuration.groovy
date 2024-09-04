// this script configures the gotenberg plugin

import sonia.scm.*;

def findClass(clazzAsString) {
    return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

def setGotenbergConfig() {
    def gotenbergConfigurationStore = injector.getInstance(findClass("com.cloudogu.scm.gotenberg.GotenbergConfigurationStore"))

    def gotenbergConfiguration = gotenbergConfigurationStore.get()

    gotenbergConfiguration.setUrl("http://gotenberg:3000/")
    gotenbergConfiguration.setEnabled(true)

    gotenbergConfigurationStore.set(gotenbergConfiguration)
}

try {
    if (ecoSystem.isInstalled("gotenberg")) {
        setGotenbergConfig()
    }
} catch (ClassNotFoundException e) {
    println "Gotenberg plugin seems not to be installed"
}
