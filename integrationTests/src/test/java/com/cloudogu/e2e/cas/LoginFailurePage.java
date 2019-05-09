package com.cloudogu.e2e.cas;

import com.cloudogu.e2e.Required;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class LoginFailurePage extends LoginPage {

    @Required
    @FindBy(className = "alert-msg-credentials")
    By alertMessage;

    public LoginFailurePage(WebDriver driver) {
        super(driver);
    }
}
