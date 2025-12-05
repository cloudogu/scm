// this script configures some basic settings for scm-manager to work with the
// ecosystem.

import sonia.scm.config.ScmConfiguration;
import sonia.scm.admin.ScmConfigurationStore;

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

def config = injector.getInstance(ScmConfiguration.class);

String pluginCenterUrl = ecoSystem.getDoguConfig("plugin_center_url");
if (pluginCenterUrl != null && !pluginCenterUrl.isEmpty()) {
  config.setPluginUrl(pluginCenterUrl);
}

// store configuration
injector.getInstance(ScmConfigurationStore.class).store(config);
