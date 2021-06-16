package com.cloudogu.e2e.cas;

import com.cloudogu.e2e.Page;
import com.cloudogu.e2e.Required;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends Page {

    private final WebDriver driver;

    @Required
    @FindBy(id ="username")
    WebElement usernameField;
    @Required
    @FindBy(id ="password")
    WebElement passwordField;
    @Required
    @FindBy(css = "button[name='submit']")
    WebElement submitButton;
    @FindBy(className = " warp-onboarding")
    WebElement tooltip;

    public LoginPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @Override
    public boolean isDisplayed() {
        return submitButton.isDisplayed();
    }

    public void login(String username, String password) {
        tooltip.click();
        WebDriverWait wait = new WebDriverWait(driver,5);
        wait.until(ExpectedConditions.invisibilityOf(tooltip));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        submitButton.click();
    }
}
