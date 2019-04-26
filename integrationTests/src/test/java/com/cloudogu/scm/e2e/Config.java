package com.cloudogu.scm.e2e;

public class Config {

    public static final String CES_FQDN = System.getenv().getOrDefault("CES_FQDN", "192.168.42.2");
    public static final String BASE_URL = "https://" + CES_FQDN;
    public static final String USERNAME = "ces-admin";
    public static final String PASSWORD = "ecosystem2016";
}
