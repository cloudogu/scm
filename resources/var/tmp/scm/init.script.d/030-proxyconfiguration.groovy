// This script configures proxy settings from ecosystem config
import sonia.scm.config.ScmConfiguration;
import sonia.scm.admin.ScmConfigurationStore

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

def configuration = injector.getInstance(ScmConfiguration.class);
boolean isProxyEnabledInEcoSystemConfig = false;

try{
	isProxyEnabledInEcoSystemConfig = "true".equals(ecoSystem.getGlobalConfig("proxy/enabled"));
} catch (FileNotFoundException e){
	System.out.println("EcoSystem proxy configuration does not exist.");
}

if (isProxyEnabledInEcoSystemConfig){
	enableProxy(configuration);
	setProxyServerSettings(configuration);
	setProxyAuthenticationSettings(configuration);
	setProxyExcludes(configuration);
} else {
    disableProxy(configuration)
}

def enableProxy(configuration){
	configuration.setEnableProxy(true);
}

def disableProxy(configuration){
	configuration.setEnableProxy(false);
}

def setProxyServerSettings(configuration){
	try{
		configuration.setProxyServer(ecoSystem.getGlobalConfig("proxy/server"));
		configuration.setProxyPort(Integer.parseInt(ecoSystem.getGlobalConfig("proxy/port")));
	} catch (FileNotFoundException e){
		System.out.println("EcoSystem proxy configuration is incomplete (server or port not found).");
		disableProxy(configuration);
	}
}

def setProxyAuthenticationSettings(configuration){
	// Authentication credentials are optional
	try{
		String proxyUser = ecoSystem.getGlobalConfig("proxy/username");
		String proxyPassword = ecoSystem.getGlobalConfig("proxy/password");
		configuration.setProxyUser(proxyUser);
		configuration.setProxyPassword(proxyPassword);
	} catch (FileNotFoundException e){
		System.out.println("EcoSystem proxy authentication configuration is incomplete or not existent.");
	}
}

/**
 * The goal of this method is to
 * - set the proxy excludes configured globally by the ces
 * - by default add the own fqdn
 * - keep the manually configured host names
 * To do so, we keep track of the hosts we add manually in a configuration "proxy/previous_no_proxy_hosts".
 * With this, we
 * - create a set to gather the new values (<code>excludes</code>)
 * - add the fqdn
 * - remove the previously added hosts
 * - get the new configured hosts from the ces, add those and store those not already in the list in
 *   "proxy/previous_no_proxy_hosts" for the next run.
 */
def setProxyExcludes(configuration) {
    HashSet<String> excludes = new HashSet<String>()

    String fqdn = ecoSystem.getGlobalConfig("fqdn")
    excludes.add(fqdn)

    HashSet<String> configuredExcludes = configuration.getProxyExcludes()
    excludes.addAll(configuredExcludes)

    def previouslyProxyExcludes = ecoSystem.getDoguConfig("proxy/previous_no_proxy_hosts").split(",")
    if (previouslyProxyExcludes.size() > 0) {
        System.out.println("Cleaning up previously configured proxy excludes: " + previouslyProxyExcludes)
        excludes.removeAll(previouslyProxyExcludes)
    }
    boolean excludesExistsInGlobalConfig = ecoSystem.keyExists("global", "proxy/no_proxy_hosts")
    if (!excludesExistsInGlobalConfig) {
        ecoSystem.setDoguConfig("proxy/previous_no_proxy_hosts", "")
        configuration.setProxyExcludes(excludes)
        System.out.println("Proxy exclude configuration not existent in global config.")
        System.out.println("Resetting proxy exclude configuration to old values: " + excludes)
        return
    }

    def newExcludesFromGlobalConfig = ecoSystem.getGlobalConfig("proxy/no_proxy_hosts").split(",")
    def configExcludesWithoutExisting = new HashSet()
    configExcludesWithoutExisting.addAll(newExcludesFromGlobalConfig)
    configExcludesWithoutExisting.removeAll(configuredExcludes)
    excludes.addAll(newExcludesFromGlobalConfig)
    System.out.println("Adding current proxy excludes from global config: " + newExcludesFromGlobalConfig)
    configuration.setProxyExcludes(excludes)
    ecoSystem.setDoguConfig("proxy/previous_no_proxy_hosts", configExcludesWithoutExisting.join(","))
    System.out.println("Setting proxy exclude configuration to result: " + excludes)
}

// store configuration
injector.getInstance(ScmConfigurationStore.class).store(configuration);
