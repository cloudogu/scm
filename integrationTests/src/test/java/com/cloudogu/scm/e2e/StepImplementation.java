package com.cloudogu.scm.e2e;

import com.thoughtworks.gauge.Gauge;
import com.thoughtworks.gauge.Step;
import driver.Driver;

import static org.assertj.core.api.Assertions.assertThat;

public class StepImplementation {

    private Page currentPage;

    @Step("Open the Login Page")
    public void openLoginPage() {
        this.currentPage = new LoginPage().open();
        Gauge.writeMessage("Page title is %s", Driver.webDriver.getTitle());
    }

    @Step("Login with configured username and password")
    public void login() {
        login(Config.ADMIN_USERNAME, Config.ADMIN_PASSWORD);
    }

    @Step("Login with user <username> and password <password>")
    public void login(String username, String password) {
        LoginPage.LoginResult loginResult = ((LoginPage) currentPage).login(username, password);
        Gauge.writeMessage("Login successful: %s", "" + loginResult.isSuccessful());
        this.currentPage = loginResult.get();
    }

    @Step("Logout")
    public void logout() {
        ((ScmManagerRootPage)currentPage).logout();
    }

    @Step("Logged in username equals configured display name")
    public void assertCorrectUser() {
        assertCorrectUser(Config.DISPLAY_NAME);
    }

    @Step("Logged in username is <username>")
    public void assertCorrectUser(String username) {
        String actualUsername = ((ScmManagerRootPage) currentPage).username();
        assertThat(actualUsername).isEqualTo(username);
    }

    @Step("Current urlPattern matches <urlPattern>")
    public void assertCurrentUrlMatches(String urlPattern) {
        assertThat(currentPage.currentUrl()).matches(urlPattern);
    }
}
