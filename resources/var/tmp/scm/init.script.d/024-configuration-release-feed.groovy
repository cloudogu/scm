// this script configures some basic settings for scm-manager to work with the
// ecosystem.

import sonia.scm.config.ScmConfiguration;
import sonia.scm.admin.ScmConfigurationStore;

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

def config = injector.getInstance(ScmConfiguration.class);

// set release feed  url
String disableReleaseFeed = ecoSystem.getDoguConfig("disable_release_feed");
String releaseFeedUrl = ecoSystem.getDoguConfig("release_feed_url");

if (disableReleaseFeed != null && disableReleaseFeed.equalsIgnoreCase("true")) {
    config.setReleaseFeedUrl("");
} else if (releaseFeedUrl != null && !releaseFeedUrl.isEmpty()) {
    config.setReleaseFeedUrl(releaseFeedUrl);
}

// store configuration
injector.getInstance(ScmConfigurationStore.class).store(config);
