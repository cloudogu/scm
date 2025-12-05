// this script configures some basic settings for scm-manager to work with the
// ecosystem.

import sonia.scm.config.ScmConfiguration;
import sonia.scm.admin.ScmConfigurationStore;

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

def config = injector.getInstance(ScmConfiguration.class);

// set base url
String fqdn = ecoSystem.getGlobalConfig("fqdn");
config.setBaseUrl("https://${fqdn}/scm");

// store configuration
injector.getInstance(ScmConfigurationStore.class).store(config);
