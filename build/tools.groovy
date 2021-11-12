import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def updateLine(env, line) {
  int index = line.indexOf('=')
  if (index > 0) {
    String left = line.substring(0, index)
    def key = left.trim()
    if (key.startsWith("ARG")) {
      key = key.substring(3).trim()
    }
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

def updateDoguJson(String newVersion) {
  def jsonSlurper = new JsonSlurper()
  def file = new File('dogu.json')

  def content = file.text

  def doguJson = jsonSlurper.parseText(content)

  def oldVersion = doguJson.Version

  content = content.replace("\"${oldVersion}\"", "\"${newVersion}\"")
  
  file.write JsonOutput.prettyPrint(content)
}