// this script configures the cas plugin

import java.util.regex.Pattern;

// Load EcoSystem library
File sourceFile = new File("/opt/scm-server/init.script.d/lib/EcoSystem.groovy");
Class groovyClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile);
ecoSystem = (GroovyObject) groovyClass.newInstance();

try {
    def cas = injector.getInstance(Class.forName("com.cloudogu.scm.cas.CasContext", true, Thread.currentThread().getContextClassLoader()));
    def config = cas.get();

    String fqdn = ecoSystem.getGlobalConfig("fqdn")
    config.setCasUrl("https://${fqdn}/cas")
    config.setEnabled(true)
    String escapedFqdn = Pattern.quote(fqdn)
    config.setAllowedProxyChains("^https://${escapedFqdn}/.*\$")

    cas.set(config);
} catch (ClassNotFoundException ex) {
    println "cas plugin seems not to be installed"
}
