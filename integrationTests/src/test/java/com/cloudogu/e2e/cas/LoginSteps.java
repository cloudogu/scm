package com.cloudogu.e2e.cas;

import com.cloudogu.e2e.Config;
import com.thoughtworks.gauge.Step;

import static com.cloudogu.e2e.Browser.browser;
import static com.cloudogu.e2e.Config.BASE_URL;

public class LoginSteps {

    @Step("Assert at login page")
    public void verifyAtLoginPage() {
        browser().assertAtPage(LoginPage.class);
    }

    @Step("Login with configured username and password")
    public void login() {
        login(Config.ADMIN_USERNAME, Config.ADMIN_PASSWORD);
    }

    @Step("Login with user <username> and password <password>")
    public void login(String username, String password) {
        browser().getPage(LoginPage.class).login(username, password);
    }

    @Step("Logout with CAS")
    public void logoutCas() {
        browser().openUrl(BASE_URL + "/cas/logout");
    }

    @Step("Logout was successful")
    public void logoutSuccessful() {
        browser().assertAtPage(LogoutPage.class);
    }
}
