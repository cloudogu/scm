package com.cloudogu.scm.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class ScmManagerRootPage extends Page {

    @FindBy(className = "hero-foot")
    WebElement mainMenuEntries;
    @FindBy(linkText = "Logout")
    WebElement logoutMenuEntry;
    @FindBy(xpath = "//a[@href='/scm/me']")
    WebElement meLink;

    public ScmManagerRootPage(WebDriver driver) {
        super(driver);
    }

    static Optional<ScmManagerRootPage> get(WebDriver driver) {
        if (driver.findElement(By.className("hero-foot")) != null) {
            return of(new ScmManagerRootPage(driver));
        } else {
            return empty();
        }
    }

    @Override
    public boolean isDisplayed() {
        return mainMenuEntries.isDisplayed();
    }

    void logout() {
        logoutMenuEntry.click();
    }

    String username() {
        return meLink.getText();
    }

     void clickMeLink() {
        meLink.click();
    }
}
