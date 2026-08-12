import groovy.json.JsonSlurper

GroovyShell shell = new GroovyShell()
def tools = shell.parse(new File('build/tools.groovy'))

class Package {

  String version;
  String url;
  String checksum;

}

def createXmlSlurper() {
  try {
    return Class.forName('groovy.xml.XmlSlurper').getDeclaredConstructor().newInstance()
  } catch (ClassNotFoundException ignored) {
    return Class.forName('groovy.util.XmlSlurper').getDeclaredConstructor().newInstance()
  }
}

int compareVersionParts(List<Integer> left, List<Integer> right) {
  for (int index = 0; index < left.size(); index++) {
    int comparison = left[index] <=> right[index]
    if (comparison != 0) {
      return comparison
    }
  }
  return 0
}

String selectLatestSnapshotVersion(versionMetadata, int majorVersion) {
  def versionPattern = ~"^${majorVersion}\\.(\\d+)\\.(\\d+)-SNAPSHOT\$"
  def candidates = versionMetadata.versioning.versions.version.findResults { versionNode ->
    String version = versionNode.text()
    def matcher = version =~ versionPattern
    if (!matcher.matches()) {
      return null
    }

    return [
      version: version,
      parts: [majorVersion, matcher.group(1).toInteger(), matcher.group(2).toInteger()]
    ]
  }

  if (candidates.isEmpty()) {
    throw new IllegalStateException("No ${majorVersion}.x snapshot version found in metadata")
  }

  return candidates.max { left, right ->
    compareVersionParts(left.parts, right.parts)
  }.version
}

String selectSnapshotArtifactVersion(snapshotMetadata, String version, String extension) {
  def snapshotVersions = snapshotMetadata.versioning.snapshotVersions.snapshotVersion.findAll { snapshotVersion ->
    snapshotVersion.extension.text() == extension
  }

  if (snapshotVersions.isEmpty()) {
    throw new IllegalStateException("No snapshot artifact with extension '${extension}' found for ${version}")
  }

  return snapshotVersions.first().value.text()
}

def createPackage(String baseUrl, String extension, int majorVersion) {
  int lastIndex = baseUrl.lastIndexOf('/')
  String packagePrefix = baseUrl.substring(lastIndex + 1)

  def versionMetadata = createXmlSlurper().parse(baseUrl + '/maven-metadata.xml')
  String latestVersion = selectLatestSnapshotVersion(versionMetadata, majorVersion)
  def snapshotMetadata = createXmlSlurper().parse("${baseUrl}/${latestVersion}/maven-metadata.xml")

  String snapshotVersion = selectSnapshotArtifactVersion(snapshotMetadata, latestVersion, extension)

  def packageUrl = "${baseUrl}/${latestVersion}/${packagePrefix}-${snapshotVersion}.${extension}"
  def checksum = new URL("${baseUrl}/${latestVersion}/${packagePrefix}-${snapshotVersion}.${extension}.sha256").text

  return new Package(version: snapshotVersion, url: packageUrl, checksum: checksum)
}

def createPluginPackage(String plugin, int majorVersion) {
  return createPackage(
    "https://packages.scm-manager.org/repository/plugin-snapshots/sonia/scm/plugins/${plugin}",
    'smp',
    majorVersion
  )
}

def appendPackage(env, prefix, pkg) {
  env.put(prefix + '_URL', pkg.url)
  env.put(prefix + '_SHA256', pkg.checksum)
}

def appendCasPlugin(env, int majorVersion) {
  def pkg = createPluginPackage('scm-cas-plugin', majorVersion)
  appendPackage(env, 'SCM_CAS_PLUGIN', pkg)
}

def appendScriptPlugin(env, int majorVersion) {
  def pkg = createPluginPackage('scm-script-plugin', majorVersion)
  appendPackage(env, 'SCM_SCRIPT_PLUGIN', pkg)
}

def appendCodeEditorPlugin(env, int majorVersion) {
  def pkg = createPluginPackage('scm-code-editor-plugin', majorVersion)
  appendPackage(env, 'SCM_CODE_EDITOR_PLUGIN', pkg)
}

def appendCesPlugin(env, int majorVersion) {
  def pkg = createPluginPackage('scm-ces-plugin', majorVersion)
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
final int SNAPSHOT_MAJOR_VERSION = 3

def corePkg = createPackage(
  'https://packages.scm-manager.org/repository/snapshots/sonia/scm/packaging/unix',
  'tar.gz',
  SNAPSHOT_MAJOR_VERSION
)
appendPackage(env, 'SCM_PKG', corePkg)

appendCasPlugin(env, SNAPSHOT_MAJOR_VERSION)
appendCodeEditorPlugin(env, SNAPSHOT_MAJOR_VERSION)
appendScriptPlugin(env, SNAPSHOT_MAJOR_VERSION)
appendCesPlugin(env, SNAPSHOT_MAJOR_VERSION)

tools.updateDockerfile(env)

def parts = corePkg.version.split("-")
def newVersion = normalizeVersion(corePkg.version) + "-" + parts[parts.length -1]

tools.updateDoguJson(newVersion)
