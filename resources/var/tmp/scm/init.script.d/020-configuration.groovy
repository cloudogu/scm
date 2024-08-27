// this script configures some basic settings for scm-manager to work with the
// ecosystem.


import lib.EcoSystem.DoguConfig;
import lib.EcoSystem.GlobalConfig;
import sonia.scm.config.ScmConfiguration;
import sonia.scm.admin.ScmConfigurationStore;
import sonia.scm.group.Group;
import sonia.scm.group.GroupManager;
import sonia.scm.security.PermissionAssigner;
import sonia.scm.security.PermissionDescriptor;
import sonia.scm.SCMContextProvider;

def config = injector.getInstance(ScmConfiguration.class);
config.setNamespaceStrategy("CustomNamespaceStrategy");
// set base url
String fqdn = GlobalConfig.get("fqdn");
config.setBaseUrl("https://${fqdn}/scm");

def context = injector.getInstance(SCMContextProvider.class);

// set plugin center url
String pluginCenterUrl = DoguConfig.get("plugin_center_url");
if (pluginCenterUrl != null && !pluginCenterUrl.isEmpty()) {
  config.setPluginUrl(pluginCenterUrl);
}

String pluginCenterAuthenticationUrl = DoguConfig.get("plugin_center_authentication_url");
if (pluginCenterAuthenticationUrl != null) {
  if ("none".equalsIgnoreCase(pluginCenterAuthenticationUrl)) {
    println("deactivating plugin center authentication");
    config.setPluginAuthUrl("");
  } else if (!pluginCenterAuthenticationUrl.isEmpty()) {
    config.setPluginAuthUrl(pluginCenterAuthenticationUrl);
  }
}

String loginInfoUrl = DoguConfig.get("login_info_url");
if (loginInfoUrl != null) {
    if ("none".equalsIgnoreCase(loginInfoUrl)) {
        println("deactivating login info");
        config.setLoginInfoUrl("");
    } else if (!loginInfoUrl.isEmpty()) {
        config.setLoginInfoUrl(loginInfoUrl);
    }
}

String alertsUrl = DoguConfig.get("alerts_url");
if (alertsUrl != null) {
    if ("none".equalsIgnoreCase(alertsUrl)) {
        println("deactivating alerts");
        config.setAlertsUrl("");
    } else if (!alertsUrl.isEmpty()) {
        config.setAlertsUrl(alertsUrl);
    }
}

// set release feed  url
String disableReleaseFeed = DoguConfig.get("disable_release_feed");
String releaseFeedUrl = DoguConfig.get("release_feed_url");

if (disableReleaseFeed != null && disableReleaseFeed.equalsIgnoreCase("true")) {
  config.setReleaseFeedUrl("");
} else if (releaseFeedUrl != null && !releaseFeedUrl.isEmpty()) {
  config.setReleaseFeedUrl(releaseFeedUrl);
}

// enable automatic user converter
config.setEnabledUserConverter(true)

// store configuration
injector.getInstance(ScmConfigurationStore.class).store(config);

// set admin group
String adminGroup = GlobalConfig.get("admin_group");
String currentAdminGroup = DoguConfig.get("admin_group");

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

DoguConfig.set("admin_group", adminGroup)
