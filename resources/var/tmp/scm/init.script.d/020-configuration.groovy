// this script configures some basic settings for scm-manager to work with the
// ecosystem.

import sonia.scm.config.ScmConfiguration;
import sonia.scm.util.ScmConfigurationUtil;
import groovy.json.JsonSlurper;
import sonia.scm.group.Group;
import sonia.scm.group.GroupManager;
import sonia.scm.security.PermissionAssigner;
import sonia.scm.security.PermissionDescriptor;
import sonia.scm.SCMContextProvider;

// TODO sharing ???
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

def config = injector.getInstance(ScmConfiguration.class);
config.setNamespaceStrategy("CustomNamespaceStrategy");
// set base url
String fqdn = getValueFromEtcd("config/_global/fqdn");
config.setBaseUrl("https://${fqdn}/scm");

def context = injector.getInstance(SCMContextProvider.class);

// set plugin center url
String pluginCenterUrl = getValueFromEtcd("config/scm/plugin_center_url");
if (pluginCenterUrl != null && !pluginCenterUrl.isEmpty()) {
  config.setPluginUrl(pluginCenterUrl);
} else if (context.version.contains("SNAPSHOT")) {
  config.setPluginUrl("https://oss.cloudogu.com/jenkins/job/scm-manager-github/job/ci-plugin-snapshot/job/master/lastSuccessfulBuild/artifact/plugins/plugin-center.json");
}

// set release feed  url
String disableReleaseFeed = getValueFromEtcd("config/scm/disable_release_feed");
String releaseFeedUrl = getValueFromEtcd("config/scm/release_feed_url");

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
String adminGroup = getValueFromEtcd("config/_global/admin_group");
GroupManager groupManager = injector.getInstance(GroupManager.class);

Group group = groupManager.get(adminGroup);
if (group == null) {
  group = new Group("cas", adminGroup);
  group.setExternal(true);

  group = groupManager.create(group);
}

PermissionAssigner permissionAssigner = injector.getInstance(PermissionAssigner.class);
PermissionDescriptor descriptor = new PermissionDescriptor("*");
permissionAssigner.setPermissionsForGroup(adminGroup, Collections.singleton(descriptor));
