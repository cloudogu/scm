// this script configures some basic settings for scm-manager to work with the
// ecosystem.

import sonia.scm.config.ScmConfiguration;
import sonia.scm.admin.ScmConfigurationStore;

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

def config = injector.getInstance(ScmConfiguration.class);

String loginInfoUrl = ecoSystem.getDoguConfig("login_info_url");
if (loginInfoUrl != null) {
    if ("none".equalsIgnoreCase(loginInfoUrl)) {
        println("deactivating login info");
        config.setLoginInfoUrl("");
    } else if (!loginInfoUrl.isEmpty()) {
        config.setLoginInfoUrl(loginInfoUrl);
    }
}

// store configuration
injector.getInstance(ScmConfigurationStore.class).store(config);
