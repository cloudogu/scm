import groovy.json.JsonSlurper

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

def updateLine(env, line) {
  int index = line.indexOf('=')
  if (index > 0) {
    String left = line.substring(0, index)
    def key = left.trim()
    if (env.containsKey(key)) {
      String value = env.get(key)
      String right = line.substring(index + 1)
      int space = right.indexOf("\s")
      if (space > 0) {
        return "${left}=${value}${right.substring(space)}"
      } else {
        return "${left}=${value}"
      }
    }
  }
  return line
}

def updateDockerfile(env) {
  def lines = []
  def file = new File('Dockerfile')
  file.eachLine { line ->
    lines.add(updateLine(env, line))
  }
  if (!lines.last().empty) {
    lines.add("")
  }
  file.write lines.join("\n")
}

def updateDoguJson(String version) {
  def jsonSlurper = new JsonSlurper()
  def file = new File('dogu.json')

  def content = file.text

  def doguJson = jsonSlurper.parseText(content)

  def oldVersion = doguJson.Version
  def parts = oldVersion.split("-")
  def newVersion = version.replace("-", "_") + "-" + parts[parts.length -1]
  
  file.write content.replace("\"${oldVersion}\"", "\"${newVersion}\"")
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

updateDockerfile(env)
updateDoguJson(corePkg.version)
