// this script configures the mail plugin of scm-manager
import groovy.json.JsonSlurper;

// TODO sharing ?
def getValueFromEtcd(String key){
  String ip = new File("/etc/ces/node_master").getText("UTF-8").trim();
	URL url = new URL("http://${ip}:4001/v2/keys/${key}");
	def json = new JsonSlurper().parseText(url.text)
	return json.node.value
}

def getEmailAddress(){
    def configuredMailAddress;
    try {
      configuredMailAddress = getValueFromEtcd("config/_global/mail_address");
    } catch (FileNotFoundException ex) {
      println "could not find mail_address configuration in registry"
    }
    if (configuredMailAddress != null && configuredMailAddress.length() > 0) {
      return configuredMailAddress;
    } else {
      return "scm@" + getValueFromEtcd("config/_global/domain");
    }
}

try {
  def mailContext = injector.getInstance(Class.forName("sonia.scm.mail.api.MailContext"));

  def old = mailContext.getConfiguration();
  def from = old.getFrom();

  if (from == null || from.length() == 0){
    from = getEmailAddress();
  }

  // TODO unable to resolve class sonia.scm.mail.api.MailConfiguration
  def configClass = Class.forName("sonia.scm.mail.api.MailConfiguration");
  def strategyClass = Class.forName("org.codemonkey.simplejavamail.TransportStrategy");
  def configuration = configClass.newInstance([
      host: "postfix", // hostname
      port: 25, // port
      transportStrategy: Enum.valueOf(strategyClass, "SMTP_PLAIN"),
      from: from,
      subjectPrefix: old.getSubjectPrefix()
  ]);

  mailContext.store(configuration);
} catch( Exception e ) {
  println "mail plugin seems not to be installed"
}
