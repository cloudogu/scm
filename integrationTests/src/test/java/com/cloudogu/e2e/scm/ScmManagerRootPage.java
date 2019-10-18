package com.cloudogu.e2e.scm;

import com.cloudogu.e2e.Page;
import com.cloudogu.e2e.Required;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class ScmManagerRootPage extends Page {

    @Required
    @FindBy(className = "footer")
    WebElement mainMenuEntries;

    @FindBy(className = "title")
    WebElement titleField;

    @FindBy(linkText = "Logout")
    WebElement logoutMenuEntry;

    @FindBy(xpath = "//a[@href='/scm/me']")
    WebElement meLink;

    public ScmManagerRootPage(WebDriver driver) {
        super(driver);
    }

    static Optional<ScmManagerRootPage> get(WebDriver driver) {
        if (driver.findElement(By.className("footer")) != null) {
            return of(new ScmManagerRootPage(driver));
        } else {
            return empty();
        }
    }

    @Override
    public boolean isDisplayed() {
        return mainMenuEntries.isDisplayed();
    }

    public String title() {
        return titleField.getText().trim();
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
