// this script configures some basic settings for scm-manager to work with the
// ecosystem.

import sonia.scm.config.ScmConfiguration;
import sonia.scm.util.ScmConfigurationUtil;
import sonia.scm.group.Group;
import sonia.scm.group.GroupManager;
import sonia.scm.security.PermissionAssigner;
import sonia.scm.security.PermissionDescriptor;
import sonia.scm.SCMContextProvider;

def sh(String cmd) {
  try {
    proc = cmd.execute()
    proc.out.close()
    proc.waitForOrKill(10000)
    return proc.text.trim()
  } catch (Exception e) {
    e.printStackTrace()
    return null;
  }
}

def getGlobalValueFromEtcd(String key) {
  value = sh("doguctl config --global --default DEFAULT_VALUE ${key}")
  println "reading global etcd value: '${key}' -> '${value}'"
  return value == "DEFAULT_VALUE" ? null : value
}

def getValueFromEtcd(String key) {
  value = sh("doguctl config --default DEFAULT_VALUE ${key}")
  println "reading etcd value: '${key}' -> '${value}'"
  return value == "DEFAULT_VALUE" ? null : value
}

def setEtcdValue(String key, String value) {
  try {
    println "setting etcd value '${key}' to '${value}'"
    sh( "doguctl config ${key} ${value}")
    println "value set successfully"
  } catch (Exception e) {
    e.printStackTrace()
  }
}

def config = injector.getInstance(ScmConfiguration.class);
config.setNamespaceStrategy("CustomNamespaceStrategy");
// set base url
String fqdn = getGlobalValueFromEtcd("fqdn");
config.setBaseUrl("https://${fqdn}/scm");

def context = injector.getInstance(SCMContextProvider.class);

// set plugin center url
String pluginCenterUrl = getValueFromEtcd("plugin_center_url");
if (pluginCenterUrl != null && !pluginCenterUrl.isEmpty()) {
  config.setPluginUrl(pluginCenterUrl);
} else if (context.version.contains("SNAPSHOT")) {
  config.setPluginUrl("https://oss.cloudogu.com/jenkins/job/scm-manager-github/job/ci-plugin-snapshot/job/master/lastSuccessfulBuild/artifact/plugins/plugin-center.json");
  config.setPluginAuthUrl("");
}

String pluginCenterAuthenticationUrl = getValueFromEtcd("plugin_center_authentication_url");
if (pluginCenterAuthenticationUrl != null) {
  if ("none".equalsIgnoreCase(pluginCenterAuthenticationUrl)) {
    println("deactivating plugin center authentication")
    config.setPluginAuthUrl("");
  } else if (!pluginCenterAuthenticationUrl.isEmpty()) {
    config.setPluginAuthUrl(pluginCenterAuthenticationUrl);
  }
}

// set release feed  url
String disableReleaseFeed = getValueFromEtcd("disable_release_feed");
String releaseFeedUrl = getValueFromEtcd("release_feed_url");

if (disableReleaseFeed != null && disableReleaseFeed.equalsIgnoreCase("true")) {
  config.setReleaseFeedUrl("");
} else if (releaseFeedUrl != null && !releaseFeedUrl.isEmpty()) {
  config.setReleaseFeedUrl(releaseFeedUrl);
}

// enable automatic user converter
config.setEnabledUserConverter(true)

// store configuration
ScmConfigurationUtil.getInstance().store(config);

// set admin group
String adminGroup = getGlobalValueFromEtcd("admin_group");
String currentAdminGroup = getValueFromEtcd("admin_group");

GroupManager groupManager = injector.getInstance(GroupManager.class);
if (adminGroup != currentAdminGroup) {
  if (currentAdminGroup != null) {
    println("configured admin group '${adminGroup}' differs from current admin group '${currentAdminGroup}'")

    Group oldGroup = groupManager.get(currentAdminGroup);
    if (oldGroup != null) {
      println("deleting current admin group '${currentAdminGroup}'")
      groupManager.delete(oldGroup);
    }
  }
}

println("checking configured admin group '${adminGroup}'")
Group group = groupManager.get(adminGroup);
if (group == null) {
  println("admin group '${adminGroup}' does not exist; will be created")
  group = new Group("cas", adminGroup);
  group.setExternal(true);

  group = groupManager.create(group);
}

println("setting permission for '${adminGroup}'")
PermissionAssigner permissionAssigner = injector.getInstance(PermissionAssigner.class);
PermissionDescriptor descriptor = new PermissionDescriptor("*");
permissionAssigner.setPermissionsForGroup(adminGroup, Collections.singleton(descriptor));

setEtcdValue("admin_group", adminGroup)
