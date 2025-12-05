// this script configures some basic settings for scm-manager to work with the
// ecosystem.

import sonia.scm.config.ScmConfiguration;
import sonia.scm.admin.ScmConfigurationStore;

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

def config = injector.getInstance(ScmConfiguration.class);

String alertsUrl = ecoSystem.getDoguConfig("alerts_url");
if (alertsUrl != null) {
    if ("none".equalsIgnoreCase(alertsUrl)) {
        println("deactivating alerts");
        config.setAlertsUrl("");
    } else if (!alertsUrl.isEmpty()) {
        config.setAlertsUrl(alertsUrl);
    }
}

// store configuration
injector.getInstance(ScmConfigurationStore.class).store(config);
