// this script configures the jenkins plugin

import sonia.scm.script.domain.*;

def findClass(clazzAsString) {
	return Class.forName(clazzAsString, true, Thread.currentThread().getContextClassLoader())
}

def removePlugin(script) {
	def scriptRepo = injector.getInstance(findClass("sonia.scm.script.domain.StorableScriptRepository"));
	println "Removing old restart script ${script}";
	scriptRepo.remove(script.id.get());
}

try {
	def scriptRepo = injector.getInstance(findClass("sonia.scm.script.domain.StorableScriptRepository"));
	scriptRepo.findAll().findAll({ it.title.get().equals("restart") }).forEach({removePlugin(it)})
} catch( ClassNotFoundException e ) {
	println "script plugin seems not to be installed";
}
