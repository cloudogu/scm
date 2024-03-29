import groovy.json.JsonSlurper

GroovyShell shell = new GroovyShell()
def tools = shell.parse(new File('build/tools.groovy'))

class Package {

  String version;
  String url;
  String checksum;

}

def createPackage(String baseUrl, String extension) {
  int lastIndex = baseUrl.lastIndexOf('/')
  String packagePrefix = baseUrl.substring(lastIndex + 1)

  def versionMetadata = new XmlSlurper().parse(baseUrl + '/maven-metadata.xml')
  def latestVersion = versionMetadata.versioning.latest
  def snapshotMetadata = new XmlSlurper().parse("${baseUrl}/${latestVersion}/maven-metadata.xml")

  def snapshotVersion = snapshotMetadata.versioning.snapshotVersions.snapshotVersion.find { it ->
      return it.extension == extension
  }

  def packageUrl = "${baseUrl}/${latestVersion}/${packagePrefix}-${snapshotVersion.value}.${extension}"
  def checksum = new URL("${baseUrl}/${latestVersion}/${packagePrefix}-${snapshotVersion.value}.${extension}.sha256").text

  return new Package(version: snapshotVersion.value, url: packageUrl, checksum: checksum)
}

def createPluginPackage(String plugin) {
  return createPackage(
    "https://packages.scm-manager.org/repository/plugin-snapshots/sonia/scm/plugins/${plugin}",
    'smp'
  )
}

def appendPackage(env, prefix, pkg) {
  env.put(prefix + '_URL', pkg.url)
  env.put(prefix + '_SHA256', pkg.checksum)
}

def appendCasPlugin(env) {
  def pkg = createPluginPackage('scm-cas-plugin')
  appendPackage(env, 'SCM_CAS_PLUGIN', pkg)
}

def appendScriptPlugin(env) {
  def pkg = createPluginPackage('scm-script-plugin')
  appendPackage(env, 'SCM_SCRIPT_PLUGIN', pkg)
}

def appendCodeEditorPlugin(env) {
  def pkg = createPluginPackage('scm-code-editor-plugin')
  appendPackage(env, 'SCM_CODE_EDITOR_PLUGIN', pkg)
}

def appendCesPlugin(env) {
  def pkg = createPluginPackage('scm-ces-plugin')
  appendPackage(env, 'SCM_CES_PLUGIN', pkg)
}

def normalizeVersion(String version) {
  // 2.23.1-20210928.082411-10
  int index = version.indexOf("-");
  if (index > 0) {
    String snapshotPart = version.substring(index + 1)
    return version.substring(0, index) + "." + snapshotPart.replace("-", "").replace(".", "")
  }
  return version
}

def env = [:]

def corePkg = createPackage(
  'https://packages.scm-manager.org/repository/snapshots/sonia/scm/packaging/unix',
  'tar.gz'
)
appendPackage(env, 'SCM_PKG', corePkg)

appendCasPlugin(env)
appendCodeEditorPlugin(env)
appendScriptPlugin(env)
appendCesPlugin(env)

tools.updateDockerfile(env)

def parts = corePkg.version.split("-")
def newVersion = normalizeVersion(corePkg.version) + "-" + parts[parts.length -1]

tools.updateDoguJson(newVersion)
