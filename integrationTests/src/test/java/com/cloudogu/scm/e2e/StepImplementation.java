package com.cloudogu.scm.e2e;

import com.thoughtworks.gauge.Gauge;
import com.thoughtworks.gauge.Step;
import driver.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

public class StepImplementation {

    private LoginPage loginPage;

    @Step("Open the Login Page")
    public void openLoginPage() {
        this.loginPage = new LoginPage().open();
        Gauge.writeMessage("Page title is %s", Driver.webDriver.getTitle());
    }

    @Step("Login with user <username> and password <password>")
    public void login(String username, String password) {
        loginPage.login(username, password);
    }

    @Step("Go to Gauge Get Started Page")
    public void gotoGetStartedPage() {
        WebElement getStartedButton = Driver.webDriver.findElement(By.xpath("//a[@href='getting-started-guide/we-start/']"));
        getStartedButton.click();

        Gauge.writeMessage("Page title is %s", Driver.webDriver.getTitle());
    }

    @Step("Ensure installation instructions are available")
    public void ensureInstallationInstructionsAreAvailable() {
        WebElement instructions = Driver.webDriver.findElement(By.xpath("//a[@href='/getting-started-guide/quick-install']"));
        assertThat(instructions).isNotNull();
    }

    @Step("Open the Gauge homepage")
    public void implementation1() {
        String app_url = System.getenv("APP_URL");
        Driver.webDriver.get(app_url + "/");
        assertThat(Driver.webDriver.getTitle()).contains("Gauge");
    }
}
