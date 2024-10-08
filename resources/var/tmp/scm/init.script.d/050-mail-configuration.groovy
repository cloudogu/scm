// this script configures the mail plugin of scm-manager

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

def getEmailAddress(){
    def configuredMailAddress;
    try {
      configuredMailAddress = ecoSystem.getGlobalConfig("mail_address");
    } catch (FileNotFoundException ex) {
      System.out.println "could not find mail_address configuration in registry"
    }
    if (configuredMailAddress != null && configuredMailAddress.length() > 0) {
      return configuredMailAddress;
    } else {
      return "scm@" + ecoSystem.getGlobalConfig("domain");
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
