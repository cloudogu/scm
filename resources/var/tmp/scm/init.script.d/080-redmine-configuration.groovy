// this script configures the redmine plugin

import groovy.json.JsonSlurper;

// TODO sharing ?
def getValueFromEtcd(String key){
  String ip = new File("/etc/ces/node_master").getText("UTF-8").trim()
	URL url = new URL("http://${ip}:4001/v2/keys/${key}")
	def json = new JsonSlurper().parseText(url.text)
	return json.node.value
}

def findClass(clazzAsString) {
  return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

try {
    def storeClass = findClass("sonia.scm.redmine.config.RedmineConfigStore")
    def store = injector.getInstance(storeClass);

		String fqdn = getValueFromEtcd("config/_global/fqdn")

    def config = store.getConfiguration()
    if (config.getUrl() == null) {
      // do not enabled commenting and state changes, 
      // because we need a technical account on redmine before
      config.setUpdateIssues(false)
      config.setAutoClose(false)
    }
    config.setUrl("https://${fqdn}/redmine")

    def formattingClass = findClass("sonia.scm.redmine.config.TextFormatting")
    def formatting = Enum.valueOf(formattingClass, "MARKDOWN")
    config.setTextFormatting(formatting)

    store.storeConfiguration(config)
} catch( ClassNotFoundException e ) {
    println "redmine plugin seems not to be installed"
}
