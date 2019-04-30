package com.cloudogu.scm.e2e;

public class Config {

    public static final String CES_FQDN = getOrDefault("CES_FQDN", "192.168.42.2");
    public static final String BASE_URL = getOrDefault("BASE_URL", "https://" + CES_FQDN);
    public static final String ADMIN_USERNAME = getOrDefault("ADMIN_USERNAME", "ces-admin");
    public static final String ADMIN_PASSWORD = getOrDefault("ADMIN_PASSWORD", "ecosystem2016");
    public static final String DISPLAY_NAME = getOrDefault("DISPLAY_NAME", "admin");

    private static String getOrDefault(String key, String defaultValue) {
        return System.getenv().getOrDefault(key, defaultValue);
    }
}
