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
      System.out.println "could not find mail_address configuration in registry"
    }
    if (configuredMailAddress != null && configuredMailAddress.length() > 0) {
      return configuredMailAddress;
    } else {
      return "scm@" + getValueFromEtcd("config/_global/domain");
    }
}

def findClass(clazzAsString) {
  return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader());
}

def findInstance(clazzAsString) {
  return injector.getInstance( findClass(clazzAsString) );
}

def findSmtpStrategy() {
  try {
    strategyClass = findClass("org.codemonkey.simplejavamail.TransportStrategy");
    System.out.println "using old SMTP strategy for version 2.x mail plugin configuration";
    return Enum.valueOf(strategyClass, "SMTP_PLAIN");
  } catch (ClassNotFoundException ignored) {
    System.out.println "could not find configuration for version 2.x mail plugin, using 3.x SMTP strategy";
  }

  strategyClass = findClass("sonia.scm.mail.api.ScmTransportStrategy");
  return Enum.valueOf(strategyClass, "SMTP");
}

try {
  def mailContext = findInstance("sonia.scm.mail.api.MailContext");

  def old = mailContext.getConfiguration();
  def from = old.getFrom();

  if (from == null || from.length() == 0){
    from = getEmailAddress();
  }

  // TODO unable to resolve class sonia.scm.mail.api.MailConfiguration
  def configClass = findClass("sonia.scm.mail.api.MailConfiguration");
  def configuration = configClass.newInstance([
      host: "postfix", // hostname
      port: 25, // port
      transportStrategy: findSmtpStrategy(),
      from: from,
      subjectPrefix: old.getSubjectPrefix()
  ]);

  mailContext.store(configuration);
} catch( ClassNotFoundException e ) {
  System.out.println "mail plugin seems not to be installed"
}
