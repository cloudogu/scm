package com.cloudogu.e2e.cas;

import com.cloudogu.e2e.Page;
import com.cloudogu.e2e.Required;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LogoutPage extends Page {

    @Required
    @FindBy(id ="msg")
    WebElement messageField;

    public LogoutPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isDisplayed() {
        return messageField != null && messageField.isDisplayed() && messageField.getText().contains("Logout successful");
    }
}
