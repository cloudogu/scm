package com.cloudogu.scm.e2e;

import com.thoughtworks.gauge.Gauge;
import com.thoughtworks.gauge.Step;
import driver.Driver;

import static com.cloudogu.scm.e2e.Browser.browser;
import static com.cloudogu.scm.e2e.Config.BASE_URL;

public class LoginSteps {
    @Step("Open the start page")
    public void openStartPage() {
        browser().openStartPage();
        Gauge.writeMessage("Page title is %s", Driver.webDriver.getTitle());
    }

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

    @Step("Logout with SCM")
    public void logoutScm() {
        browser().getPage(ScmManagerRootPage.class).logout();
    }

    @Step("Logout with CAS")
    public void logoutCas() {
        browser().openPage(BASE_URL + "/cas/logout");
    }

    @Step("Logout was successful")
    public void logoutSuccessful() {
        browser().assertAtPage(LogoutPage.class);
    }
}
