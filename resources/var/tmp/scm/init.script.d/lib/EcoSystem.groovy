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

/**
 * Returns whether the integration with the given dogu (e.g. "jenkins", "cockpit") should be active,
 * i.e. whether the corresponding plugins should be installed and configured.
 *
 * If the integration is configured explicitly via the environment variable
 * SCM_INTEGRATION_<NAME>_ENABLED (Dogu v3, set by the Helm chart), that value is used.
 * Otherwise (classic and multinode with Dogu v2) the integration is active if the dogu is installed.
 */
boolean isIntegrationEnabled(String doguName) {
    Boolean explicit = getExplicitIntegrationState(doguName)
    return explicit != null ? explicit : isInstalled(doguName)
}

/**
 * Returns the explicitly configured state of the integration, or null if it is not configured explicitly.
 */
private Boolean getExplicitIntegrationState(String doguName) {
    String explicit = getIntegrationEnv(doguName, "ENABLED")
    if (explicit == null) {
        return null
    }
    boolean enabled = "true".equalsIgnoreCase(explicit.trim())
    println "integration ${doguName} is explicitly ${enabled ? 'enabled' : 'disabled'}"
    return enabled
}

/**
 * Returns which redmine the redmine integration should use: "REDMINE", "EASY_REDMINE",
 * or null if the redmine integration is not active.
 *
 * Explicit configuration (Dogu v3): SCM_INTEGRATION_REDMINE_ENABLED and SCM_INTEGRATION_REDMINE_TYPE.
 * Otherwise derived from the installed dogus; if both are installed, the dogu config key
 * redmine_type decides.
 */
String getRedmineIntegration() {
    String explicit = getIntegrationEnv("redmine", "ENABLED")
    if (explicit != null) {
        if (!"true".equalsIgnoreCase(explicit.trim())) {
            println "redmine integration is explicitly disabled"
            return null
        }
        String type = getIntegrationEnv("redmine", "TYPE") == "REDMINE" ? "REDMINE" : "EASY_REDMINE"
        println "redmine integration is explicitly enabled with type ${type}"
        return type
    }

    boolean easyRedmineInstalled = isInstalled("easyredmine")
    boolean redmineInstalled = isInstalled("redmine")
    if (easyRedmineInstalled && redmineInstalled) {
        return getDoguConfig("redmine_type") == "REDMINE" ? "REDMINE" : "EASY_REDMINE"
    } else if (easyRedmineInstalled) {
        return "EASY_REDMINE"
    } else if (redmineInstalled) {
        return "REDMINE"
    }
    return null
}

private static String getIntegrationEnv(String doguName, String suffix) {
    return System.getenv("SCM_INTEGRATION_${doguName.toUpperCase()}_${suffix}".toString())
}

boolean isInstalled(String doguName) {
    System.out.println "check if ${doguName} is installed"
    return isMultinode() ? isInstalledMN(doguName) : isInstalledClassic(doguName)
}

private static boolean isInstalledMN(String doguName) {
    String doguRegistryDir = System.getenv("DOGU_REGISTRY_DIR") ?: "/etc/ces/dogu_json"
    String path = "${doguRegistryDir}/${doguName}/current"
    return (new File(path)).exists()
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