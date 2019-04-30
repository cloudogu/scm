package com.cloudogu.scm.e2e;

import driver.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Optional;

import static com.cloudogu.scm.e2e.Config.BASE_URL;

public class LoginPage extends Page {

    private final WebDriver driver;

    @FindBy(id ="username")
    WebElement usernameField;
    @FindBy(id ="password")
    WebElement passwordField;
    @FindBy(css = "input[name='submit']")
    WebElement submitButton;

    public LoginPage() {
        this(Driver.webDriver);
    }

    public LoginPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public static ExpectedCondition present() {
        return ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[name='submit']"));
    }

    public LoginPage open() {
        driver.navigate().to(BASE_URL + "/scm");
        waitUntil(ExpectedConditions.elementToBeClickable(submitButton));
        return this;
    }

    public LoginResult login(String username, String password) {
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        submitButton.click();
        waitUntil(ExpectedConditions.or(
                LoginFailurePage.present(),
                ScmManagerRootPage.present()
        ));
        return new LoginResult(ScmManagerRootPage.get(driver));
    }

    public class LoginResult {
        private final Optional<ScmManagerRootPage> scmManagerRootPage;

        public LoginResult(Optional<ScmManagerRootPage> scmManagerRootPage) {

            this.scmManagerRootPage = scmManagerRootPage;
        }

        public boolean isSuccessful() {
            return scmManagerRootPage.isPresent();
        }

        public ScmManagerRootPage onSuccess() {
            return scmManagerRootPage.orElseThrow(() -> new IllegalStateException("Login was not successful"));
        }

        public LoginPage onFailure() {
            if (scmManagerRootPage.isPresent()) {
                throw new IllegalStateException("Login was successful");
            }
            return LoginPage.this;
        }

        public Page get() {
            if (isSuccessful()) {
                return scmManagerRootPage.get();
            } else {
                return LoginPage.this;
            }
        }
    }
}
