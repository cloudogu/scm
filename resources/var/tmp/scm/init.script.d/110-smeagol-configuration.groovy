// this script configures the smeagol plugin

import groovy.json.JsonSlurper
import sonia.scm.*;

def findClass(clazzAsString) {
  return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

def getValueFromEtcd(String key){
    String ip = new File("/etc/ces/node_master").getText("UTF-8").trim()
    URL url = new URL("http://${ip}:4001/v2/keys/${key}")
    def json = new JsonSlurper().parseText(url.text)
    return json.node.value
}

def setSmeagolConfig(){
  String fqdn = getValueFromEtcd("config/_global/fqdn")
  String smeagolUrl = "https://${fqdn}/smeagol"

  def smeagolConfiguration = injector.getInstance(findClass("com.cloudogu.smeagol.SmeagolConfiguration"))
  def currentConfig = smeagolConfiguration.get()
  currentConfig.setSmeagolUrl(smeagolUrl)
  currentConfig.setEnabled(true)
  smeagolConfiguration.set(currentConfig)
}

try {
    setSmeagolConfig()
} catch( ClassNotFoundException e ) {
    println "Smeagol plugin seems not to be installed"
}
