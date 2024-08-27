import lib.EcoSystem.GlobalConfig

// this script configures the jenkins plugin

def findClass(clazzAsString) {
  return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

try {
    def jenkins = injector.getInstance(findClass("sonia.scm.jenkins.JenkinsContext"));
	def config = jenkins.getConfiguration();

	String fqdn = GlobalConfig.get("fqdn");
	config.url = "https://${fqdn}/jenkins";

    jenkins.storeConfiguration(config);
} catch( ClassNotFoundException e ) {
    println "jenkins plugin seems not to be installed"
}
