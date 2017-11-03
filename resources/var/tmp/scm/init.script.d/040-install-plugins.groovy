// this script installs required plugins for scm-manager

import sonia.scm.plugin.PluginManager;
import sonia.scm.ces.WebAppRestartHook;

// configuration
def plugins = [
    "sonia.scm.plugins:scm-support-plugin",
    "de.triology.scm.plugins:scm-cas-plugin",
    "sonia.scm.plugins:scm-mail-plugin",
    "sonia.scm.plugins:scm-gravatar-plugin",
    "sonia.scm.plugins:scm-statistic-plugin"
];

// methods

def isDoguInstalled(name){
	String ip = new File("/etc/ces/node_master").getText("UTF-8").trim();
	URL url = new URL("http://${ip}:4001/v2/keys/dogu/" + name + "/current");
	return url.openConnection().getResponseCode() == 200;
}

def isInstalled(installed, id){
   for (def ip : installed){
       if (ip.getId(false).equals(id)){
           return true;
       }
   }
   return false;
}

def getLatestIdWithVersion(available, id){
   for (def ip : available){
       if (ip.getId(false).equals(id)){
           return ip.getId(true);
       }
   }
   return null;
}

// action

if (isDoguInstalled("redmine")){
	plugins.add("sonia.scm.plugins:scm-redmine-plugin")
}

if (isDoguInstalled("jenkins")){
	plugins.add("sonia.scm.plugins:scm-jenkins-plugin")
}

if (isDoguInstalled("smeagol")){
	plugins.add("sonia.scm.plugins:scm-webhook-plugin")
}

def pluginManager = injector.getInstance(PluginManager.class);
def available = pluginManager.getAvailable();
def installed = pluginManager.getInstalled();

def restart = false;
for (def id : plugins){
  if (!isInstalled(installed, id)){
      def iwv = getLatestIdWithVersion(available, id);
      println "install missing plugin " + iwv;
      pluginManager.install(iwv);
      restart = true;
  }
}

if (restart){
    println "restarting scm-manager";
    WebAppRestartHook.restart("scm", "0");
    sleep(3000);
}
