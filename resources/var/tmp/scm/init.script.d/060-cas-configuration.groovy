// this script configures the cas plugin

import groovy.json.JsonSlurper;

// TODO sharing ?
def getValueFromEtcd(String key){
  String ip = new File("/etc/ces/node_master").getText("UTF-8").trim();
	URL url = new URL("http://${ip}:4001/v2/keys/${key}");
	def json = new JsonSlurper().parseText(url.text)
	return json.node.value
}

try {
    def cas = injector.getInstance( Class.forName("com.cloudogu.scm.cas.CasContext", true, Thread.currentThread().getContextClassLoader()) );
	def config = cas.get();

	String fqdn = getValueFromEtcd("config/_global/fqdn");
	config.setCasUrl("https://${fqdn}/cas");
	config.setEnabled(true);

    cas.set(config);
} catch (ClassNotFoundException ex) {
	 println "cas plugin seems not to be installed"
}
