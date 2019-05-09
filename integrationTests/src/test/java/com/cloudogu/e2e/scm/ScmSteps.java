package com.cloudogu.e2e.scm;

import com.cloudogu.e2e.Config;
import com.thoughtworks.gauge.Step;

import static com.cloudogu.e2e.Browser.browser;
import static com.cloudogu.e2e.Config.BASE_URL;
import static org.assertj.core.api.Assertions.assertThat;

public class ScmSteps {

    @Step("Open the SCM start page")
    public void openScmStartPage() {
        browser().openUrl(BASE_URL + "/scm");
    }

    @Step("Footer contains configured display name")
    public void verifyCorrectUser() {
        verifyCorrectUser(Config.DISPLAY_NAME);
    }

    @Step("Logged in username is <username>")
    public void verifyCorrectUser(String username) {
        String actualUsername = browser().getPage(ScmManagerRootPage.class).username();
        assertThat(actualUsername).isEqualTo(username);
    }

    @Step("Verify SCM open")
    public void verifyScmOpen() {
        browser().assertAtPage(ScmManagerRootPage.class);
    }

    @Step("Open Me page")
    public void openMePage() {
        browser().getPage(ScmManagerRootPage.class).clickMeLink();
        browser().assertAtPage(MePage.class);
    }

    @Step("Email equals configured email")
    public void emailEqualsConfiguredMail() {
        assertThat(browser().getPage(MePage.class).eMail()).isEqualTo(Config.EMAIL);
    }

    @Step("Display name equals configured display name")
    public void displayNameEqualsConfiguredDisplayName() {
        assertThat(browser().getPage(MePage.class).displayName()).isEqualTo(Config.DISPLAY_NAME);
    }

    @Step("Groups contains configured ces admin group")
    public void groupsContainsConfiguredCesAdminGroup() {
        assertThat(browser().getPage(MePage.class).groups()).contains(Config.ADMIN_GROUP);
    }

    @Step("Logout with SCM")
    public void logoutScm() {
        browser().getPage(ScmManagerRootPage.class).logout();
    }
}
