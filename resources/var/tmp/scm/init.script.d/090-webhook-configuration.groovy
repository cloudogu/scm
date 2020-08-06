// this script configures the webhook plugin

import groovy.json.JsonSlurper;
import javax.xml.bind.*;
import sonia.scm.*;

// TODO sharing ?
def getValueFromEtcd(String key){
  String ip = new File("/etc/ces/node_master").getText("UTF-8").trim();
	URL url = new URL("http://${ip}:4001/v2/keys/${key}");
	def json = new JsonSlurper().parseText(url.text)
	return json.node.value
}

// TODO sharing ?
def findClass(clazzAsString) {
  return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

def addSmeagolNotifyEntry(globalConfig){
  String fqdn = getValueFromEtcd("config/_global/fqdn");
  String url = "https://${fqdn}/smeagol/rest/api/v1/notify?id=\${repository.id}";

  boolean executeOnEveryCommit = false;
  boolean sendCommitData = false;

  def httpMethod = Enum.valueOf(findClass("sonia.scm.webhook.HttpMethod"), "GET");
  
  Set webhooks = new HashSet();
  def webhookClass = findClass("sonia.scm.webhook.WebHook");
  webhooks.add(webhookClass.newInstance(url, executeOnEveryCommit, sendCommitData, httpMethod));

  def webHookConfigurationClass = findClass("sonia.scm.webhook.WebHookConfiguration");
  def config = webHookConfigurationClass.newInstance(webhooks);
  return globalConfig.merge(config);
}

try {

    def webHookContext = injector.getInstance(findClass("sonia.scm.webhook.WebHookContext"));
    def globalConfig = webHookContext.getGlobalConfiguration();

    def newGlobalConfig = addSmeagolNotifyEntry(globalConfig);
    webHookContext.setGlobalConfiguration(newGlobalConfig);

} catch( ClassNotFoundException e ) {
    println "webhook plugin seems not to be installed"
}
