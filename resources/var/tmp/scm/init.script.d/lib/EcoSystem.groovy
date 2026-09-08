String getGlobalConfig(String key) {
    try {
        def value = sh("doguctl config --global --default DEFAULT_VALUE ${key}")
        println "reading global config value: '${key}' -> '${value}'"
        return value == "DEFAULT_VALUE" ? null : value
    } catch (Exception e) {
        e.printStackTrace()
    }
}

String getDoguConfig(String key) {
    try {
        def value = sh("doguctl config --default DEFAULT_VALUE ${key}")
        println "reading dogu config value: '${key}' -> '${value}'"
        return value == "DEFAULT_VALUE" ? null : value
    } catch (Exception e) {
        e.printStackTrace()
    }
}

void setDoguConfig(String key, String value) {
    try {
        println "setting dogu config value '${key}' to '${value}'"
        sh("doguctl config ${key} ${value}")
        println "value set successfully"
    } catch (Exception e) {
        e.printStackTrace()
    }
}

boolean isInstalled(String doguName) {
    System.out.println "check if ${doguName} is installed"
    return isMultinode() ? isInstalledMN(doguName) : isInstalledClassic(doguName)
}

private static boolean isInstalledMN(String doguName) {
    String doguRegistryDir = System.getenv("DOGU_REGISTRY_DIR") ?: "/etc/ces/dogu_json"
    String path = "${doguRegistryDir}/${doguName}/current"
    boolean exists = (new File(path)).exists()

    //println "isInstalledMN: doguName=${doguName}, registryDir=${doguRegistryDir}, path=${path}, exists=${exists}"

    return exists
}

private static boolean isInstalledClassic(String doguName) {
    String ip = new File("/etc/ces/node_master").getText("UTF-8").trim();
    URL url = new URL("http://${ip}:4001/v2/keys/dogu/${doguName}/current");
    return url.openConnection().getResponseCode() == 200;
}

private boolean isMultinode() {
    return "true" == System.getenv("ECOSYSTEM_MULTINODE")
}

boolean keyExists(String scope, String key) {
    String value
    if (scope == "global") {
        value = getGlobalConfig(key)
    } else if (scope == "dogu") {
        value = getDoguConfig(key)
    }

    return value != null && value != ""
}

private String sh(String cmd) {
    try {
        def proc = cmd.execute()
        proc.out.close()
        proc.waitForOrKill(10000)
        return proc.text.trim()
    } catch (Exception e) {
        e.printStackTrace()
        return null;
    }
}