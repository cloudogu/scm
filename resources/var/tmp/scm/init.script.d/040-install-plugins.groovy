// this script installs required plugins for scm-manager

import sonia.scm.plugin.PluginManager;

// configuration
def defaultPlugins = [
    "scm-gravatar-plugin",
    "scm-mail-plugin",
    "scm-review-plugin",
    "scm-tagprotection-plugin",
    "scm-jira-plugin",
    "scm-activity-plugin",
    "scm-statistic-plugin",
    "scm-pathwp-plugin",
    "scm-branchwp-plugin",
    "scm-notify-plugin",
    "scm-authormapping-plugin",
    "scm-groupmanager-plugin",
    "scm-pushlog-plugin",
    "scm-support-plugin",
    "scm-directfilelink-plugin",
    "scm-readme-plugin",
    "scm-editor-plugin",
];

def plugins = [];

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

def isFirstStart() {
    def defaultPluginsInstalledFlag = new File(sonia.scm.SCMContext.getContext().getBaseDirectory(), ".defaultPluginsInstalled");
    return defaultPluginsInstalledFlag.createNewFile();
}

// action

if (isDoguInstalled("redmine")) {
	plugins.add("scm-redmine-plugin")
}

if (isDoguInstalled("jenkins")) {
	plugins.add("scm-jenkins-plugin")
	plugins.add("scm-ci-plugin")
}

if (isDoguInstalled("smeagol")) {
	plugins.add("scm-webhook-plugin")
	plugins.add("scm-rest-legacy-plugin")
}

if (isDoguInstalled("cockpit")) {
    plugins.add("scm-cockpit-legacy-plugin")
}

if (isFirstStart()) {
    System.out.println("First start detected; installing default plugins.");
    plugins.addAll(defaultPlugins)
}

def pluginManager = injector.getInstance(PluginManager.class);
def available = pluginManager.getAvailable();
def installed = pluginManager.getInstalled();

def restart = false;
for (def name : plugins) {
    if (!isInstalled(installed, name)){
        def availableInformation = getAvailablePlugin(available, name);
        if (availableInformation == null) {
            System.out.println("Cannot install missing plugin ${name}. No available plugin found!");
        } else {
            System.out.println("install missing plugin ${availableInformation.name} in version ${availableInformation.version}");
            pluginManager.install(name, false);
            restart = true;
        }
    } else {
        System.out.println("plugin ${name} already installed.");
    }
}

if (restart){
    System.out.println("restarting scm-manager");
//    pluginManager.restart("initial plugin installation");
    sleep(3000);
    System.exit(42);
} else {
    System.out.println("no new plugins installed");
}
