// This script configures proxy settings from ecosystem config


import lib.EcoSystem.GlobalConfig
import sonia.scm.config.ScmConfiguration;
import sonia.scm.admin.ScmConfigurationStore

def configuration = injector.getInstance(ScmConfiguration.class);
boolean isProxyEnabledInEcoSystemConfig = false;

try{
	isProxyEnabledInEcoSystemConfig = "true".equals(GlobalConfig.get("proxy/enabled"));
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
		configuration.setProxyServer(GlobalConfig.get("proxy/server"));
		configuration.setProxyPort(Integer.parseInt(GlobalConfig.get("proxy/port")));
	} catch (FileNotFoundException e){
		System.out.println("EcoSystem proxy configuration is incomplete (server or port not found).");
		disableProxy(configuration);
	}
}

def setProxyAuthenticationSettings(configuration){
	// Authentication credentials are optional
	try{
		String proxyUser = GlobalConfig.get("proxy/username");
		String proxyPassword = GlobalConfig.get("proxy/password");
		configuration.setProxyUser(proxyUser);
		configuration.setProxyPassword(proxyPassword);
	} catch (FileNotFoundException e){
		System.out.println("EcoSystem proxy authentication configuration is incomplete or not existent.");
	}
}

def setProxyExcludes(configuration){
	Set<String> excludes = new HashSet<String>();
	excludes.add(GlobalConfig.get("fqdn"));
	configuration.setProxyExcludes(excludes);
}

// store configuration
injector.getInstance(ScmConfigurationStore.class).store(configuration);
