package lib

class EcoSystem {
    static class GlobalConfig {
        static def get(String key) {
            def value = sh("doguctl config --global --default DEFAULT_VALUE ${key}")
            println "reading global config value: '${key}' -> '${value}'"
            return value == "DEFAULT_VALUE" ? null : value
        }
    }


    static class DoguConfig {
        static def get(String key) {
            def value = sh("doguctl config --default DEFAULT_VALUE ${key}")
            println "reading dogu config value: '${key}' -> '${value}'"
            return value == "DEFAULT_VALUE" ? null : value
        }

        static def set(String key, String value) {
            try {
                println "setting dogu config value '${key}' to '${value}'"
                sh("doguctl config  ${ key} ${value}")
                println "value set successfully"
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    private static def sh(String cmd) {
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
}