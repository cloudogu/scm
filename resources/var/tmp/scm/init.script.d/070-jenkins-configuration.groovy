// this script configures the jenkins plugin

import groovy.json.JsonSlurper;

// TODO sharing ?
def getValueFromEtcd(String key){
  String ip = new File("/etc/ces/node_master").getText("UTF-8").trim();
	URL url = new URL("http://${ip}:4001/v2/keys/${key}");
	def json = new JsonSlurper().parseText(url.text)
	return json.node.value
}

def findClass(clazzAsString) {
  return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

try {
    def jenkins = injector.getInstance(findClass("sonia.scm.jenkins.JenkinsContext"));
		def config = jenkins.getConfiguration();

		String fqdn = getValueFromEtcd("config/_global/fqdn");
		config.url = "https://${fqdn}/jenkins";

    jenkins.storeConfiguration(config);
} catch( ClassNotFoundException e ) {
    println "jenkins plugin seems not to be installed"
}
