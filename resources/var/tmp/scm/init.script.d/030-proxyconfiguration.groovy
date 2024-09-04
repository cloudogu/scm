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

def setProxyExcludes(configuration){
	Set<String> excludes = new HashSet<String>();
	excludes.add(ecoSystem.getGlobalConfig("fqdn"));
	configuration.setProxyExcludes(excludes);
}

// store configuration
injector.getInstance(ScmConfigurationStore.class).store(configuration);
