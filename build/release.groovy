import groovy.json.JsonSlurper

GroovyShell shell = new GroovyShell()
def tools = shell.parse(new File('build/tools.groovy'))

// push release release/2.23.1-1
// take scm version from branch: 2.23.1
// get latest plugins from plugin-center for version 2.23.1
// update dockerfile and dogu.json
// commit, push, tag, bla borrow from scm-manager release
// publish dogu to registry

if (args.length <= 0) {
    System.err.println("usage groovy build/release.groovy doguVersion")
    System.exit(1)
}

String doguVersion = args[0]
int separator = doguVersion.lastIndexOf('-')
if (separator <= 0) {
    System.err.println("invalid dogu version, missing hyphen in version")
    System.exit(1)
}

String scmVersion = doguVersion.substring(0, separator)
String packageUrl = "https://packages.scm-manager.org/repository/releases/sonia/scm/packaging/unix/${scmVersion}/unix-${scmVersion}.tar.gz"
String checksum = new URL(packageUrl + ".sha256").text

def env = ["SCM_PKG_URL": packageUrl]
env.put("SCM_PKG_SHA256", checksum)

def requiredPlugins = ["scm-cas-plugin", "scm-script-plugin", "scm-code-editor-plugin", "scm-ces-plugin"]

String pluginCenterUrl = "https://plugin-center-api.scm-manager.org/api/v1/plugins/${scmVersion}?os=Linux&arch=64"

def jsonSlurper = new JsonSlurper()
def pluginCenter = jsonSlurper.parseText(new URL(pluginCenterUrl).text)
def plugins = pluginCenter._embedded.plugins.findAll { plugin ->
    return requiredPlugins.contains(plugin.name)
}

plugins.each { plugin -> 
    String name = plugin.name
    String prefix = name.toUpperCase(Locale.ENGLISH).replace("-", "_")

    String version = plugin.version

    // we create url by our self, because we do not want to increase download count in prometheus
    String url = "https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/${name}/${version}/${name}-${version}.smp"

    env.put(prefix + "_URL", url)
    env.put(prefix + "_SHA256", plugin.sha256sum)
}

tools.updateDockerfile(env)
tools.updateDoguJson(doguVersion)