// this script installs required plugins for scm-manager

import sonia.scm.plugin.PluginManager;

// configuration
def plugins = [
    "scm-gravatar-plugin",
    "scm-mail-plugin",
    "scm-jenkins-plugin",
    "scm-review-plugin",
    "scm-webhook-plugin",
    "scm-tagprotection-plugin",
    "scm-rest-legacy-plugin",
    "scm-issuetracker-plugin",
    "scm-jira-plugin",
    "scm-redmine-plugin",
    "scm-activity-plugin",
    "scm-statistic-plugin",
    "scm-cockpit-legacy-plugin",
    "scm-pathwp-plugin",
    "scm-branchwp-plugin",
    "scm-notify-plugin",
    "scm-authormapping-plugin",
    "scm-groupmanager-plugin",
    "scm-pushlog-plugin",
    "scm-support-plugin",
    "scm-directfilelink-plugin",
    "scm-readme-plugin",
    "scm-ssh-plugin",
    "scm-editor-plugin",
];

// methods

def isDoguInstalled(name){
	String ip = new File("/etc/ces/node_master").getText("UTF-8").trim();
	URL url = new URL("http://${ip}:4001/v2/keys/dogu/${name}/current");
	return url.openConnection().getResponseCode() == 200;
}

def isInstalled(installed, name){
   for (def plugin : installed){
       if (plugin.descriptor.information.name.equals(name)){
           return true;
       }
   }
   return false;
}

def getAvailablePlugin(available, name){
   for (def plugin : available){
       if (plugin.descriptor.information.name.equals(name)){
           return plugin.descriptor.information;
       }
   }
   return null;
}

// action

if (isDoguInstalled("redmine")){
	plugins.add("scm-redmine-plugin")
}

if (isDoguInstalled("jenkins")){
	plugins.add("scm-jenkins-plugin")
}

if (isDoguInstalled("smeagol")){
	plugins.add("scm-webhook-plugin")
	plugins.add("scm-rest-legacy-plugin")
}

def pluginManager = injector.getInstance(PluginManager.class);
def available = pluginManager.getAvailable();
def installed = pluginManager.getInstalled();

def restart = false;
for (def name : plugins){
  if (!isInstalled(installed, name)){
      def availableInformation = getAvailablePlugin(available, name);
      if (availableInformation == null) {
          println "Cannot install missing plugin ${name}. No available plugin found!";
      } else {
          println "install missing plugin ${availableInformation.name} in version ${availableInformation.version}";
          pluginManager.install(name, false);
          restart = true;
      }
  }
}

if (restart){
    println "restarting scm-manager";
//    pluginManager.restart("initial plugin installation");
    sleep(3000);
    System.exit(42);
}
