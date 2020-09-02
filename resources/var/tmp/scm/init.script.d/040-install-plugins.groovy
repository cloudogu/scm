// this script installs required plugins for scm-manager

import sonia.scm.plugin.PluginManager;
import groovy.json.JsonSlurper;

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
    "scm-landingpage-plugin"
];

def plugins = []
def pluginsFromOldInstallation = []

// methods

def addMissingDefaultPluginsFromEtcd(plugins){
    def etcdValue = getValueFromEtcd("/config/scm/additional_plugins");
    if (etcdValue != null) {
        def additionalPlugins = etcdValue.split(",");
        System.out.println("Following plugins must be installed: ${additionalPlugins}");

        for (def p : additionalPlugins) {
            def plugin = p.trim();
            if (!plugins.contains(plugin)) {
                System.out.println("add missing default plugin to installation queue: ${plugin}");
                plugins.add(plugin)
            }
        }
    }
}

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

def getValueFromEtcd(String key){
    try {
        String ip = new File("/etc/ces/node_master").getText("UTF-8").trim();
        URL url = new URL("http://${ip}:4001/v2/keys/${key}");
        def json = new JsonSlurper().parseText(url.text)
        return json.node.value
    } catch (FileNotFoundException e) {
        return null;
    }
}

def isFirstStart() {
    def defaultPluginsInstalledFlag = new File(sonia.scm.SCMContext.getContext().getBaseDirectory(), ".defaultPluginsInstalled");
    return defaultPluginsInstalledFlag.createNewFile();
}

def installPlugin(pluginManager, pluginName) {
    try {
        pluginManager.install(pluginName, false);
        return true;
    } catch (sonia.scm.NotFoundException e) {
        System.out.println("plugin or dependency for ${pluginName} not found: ${e.message}");
    } catch (sonia.scm.plugin.PluginChecksumMismatchException e) {
        System.out.println("Plugin ${pluginName} had wrong checksum: ${e.message}");
    }
    return false;
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

addMissingDefaultPluginsFromEtcd(plugins)

File pluginListFile = new File(sonia.scm.SCMContext.getContext().getBaseDirectory(), "installed_plugins_before_update.lst")
if (pluginListFile.exists()) {
    def reader = pluginListFile.newReader()
    def line
    while ((line = reader.readLine()) != null) {
        System.out.println("Add previously installed plugin '${line}'");
        plugins.add(line)
        pluginsFromOldInstallation.add(line)
    }
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
            pluginsFromOldInstallation.remove(name)
            restart |= installPlugin(pluginManager, name)
        }
    } else {
        pluginsFromOldInstallation.remove(name)
        System.out.println("plugin ${name} already installed.");
    }
}

if (Boolean.valueOf(getValueFromEtcd("/config/scm/update_plugins").toString())) {
    System.out.println("checking for updates of plugins");
    def update = false
    for (def plugin : pluginManager.updatable) {
        def information = plugin.descriptor.information
        System.out.println "found newer version for plugin ${information.name}@${information.version}"
        update = true
        restart |= installPlugin(pluginManager, information.name)
    }
    if (!update) {
        System.out.println("no plugins updated");
    }
} else {
    System.out.println("skipping plugin update step");
}

if (pluginListFile.exists()) {
    if (pluginsFromOldInstallation.isEmpty()) {
        println "Deleting file with plugins from old installation; all plugins have been installed again."
        pluginListFile.delete()
    } else {
        println "Not all plugins from old installation could be installed; keeping list to try again next time."
    }
}

if (restart){
    System.out.println("restarting scm-manager");
    pluginManager.executePendingAndRestart();
} else {
    System.out.println("no new plugins installed");
}
