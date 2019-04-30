package com.cloudogu.scm.e2e;

import com.thoughtworks.gauge.Gauge;
import com.thoughtworks.gauge.Step;
import driver.Driver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static com.cloudogu.scm.e2e.Config.BASE_URL;
import static org.assertj.core.api.Assertions.assertThat;

public class StepImplementation {

    private Browser browser = new Browser();

    @Step("Open the start page")
    public void openStartPage() {
        browser.openStartPage();
        Gauge.writeMessage("Page title is %s", Driver.webDriver.getTitle());
    }

    @Step("Assert at login page")
    public void assertAtLoginPage() {
        browser.assertAtPage(LoginPage.class);
    }

    @Step("Login with configured username and password")
    public void login() {
        login(Config.ADMIN_USERNAME, Config.ADMIN_PASSWORD);
    }

    @Step("Login with user <username> and password <password>")
    public void login(String username, String password) {
        browser.getPage(LoginPage.class).login(username, password);
    }

    @Step("Logout with SCM")
    public void logoutScm() {
        browser.getPage(ScmManagerRootPage.class).logout();
    }

    @Step("Logout with CAS")
    public void logoutCas() {
        Driver.webDriver.get(BASE_URL + "/cas/logout");
    }

    @Step("Logged in username equals configured display name")
    public void assertCorrectUser() {
        assertCorrectUser(Config.DISPLAY_NAME);
    }

    @Step("Logged in username is <username>")
    public void assertCorrectUser(String username) {
        String actualUsername = browser.getPage(ScmManagerRootPage.class).username();
        assertThat(actualUsername).isEqualTo(username);
    }

    @Step("Current urlPattern matches <urlPattern>")
    public void assertCurrentUrlMatches(String urlPattern) {
        assertThat(browser.getPage(Page.class).currentUrl()).matches(urlPattern);
    }

    @Step("Verify SCM open")
    public void verifyScmOpen() {
        browser.assertAtPage(ScmManagerRootPage.class);
    }

    @Step("Logout was successful")
    public void logoutSuccessful() {
        browser.assertAtPage(LogoutPage.class);
    }

    @Step("Open Me page")
    public void openMePage() {
        browser.getPage(ScmManagerRootPage.class).clickMeLink();
        browser.assertAtPage(MePage.class);
    }

    @Step("Email equals configured email")
    public void emailEqualsConfiguredMail() {
        assertThat(browser.getPage(MePage.class).eMail()).isEqualTo(Config.EMAIL);
    }

    @Step("Display name equals configured display name")
    public void displayNameEqualsConfiguredDisplayName() {
        assertThat(browser.getPage(MePage.class).displayName()).isEqualTo(Config.DISPLAY_NAME);
    }

    @Step("Groups contains configured ces admin group")
    public void groupsContainsConfiguredCesAdminGroup() {
        assertThat(browser.getPage(MePage.class).groups()).contains(Config.ADMIN_GROUP);
    }
}
