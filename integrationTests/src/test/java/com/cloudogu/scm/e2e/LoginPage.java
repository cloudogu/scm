package com.cloudogu.scm.e2e;

import driver.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.cloudogu.scm.e2e.Config.BASE_URL;

public class LoginPage {

    private final WebDriver driver;

    private final By usernameLocator = By.id("username");
    private final By passwordLocator = By.id("password");
    private final By submitLocator = By.cssSelector("input[name='submit']");

    public LoginPage() {
        this(Driver.webDriver);
    }

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage open() {
        driver.navigate().to(BASE_URL);
        return this;
    }

    public LoginPage login(String username, String password) {
        driver.findElement(usernameLocator).sendKeys(username);
        driver.findElement(passwordLocator).sendKeys(password);
        driver.findElement(submitLocator).click();
        return this;
    }
}
