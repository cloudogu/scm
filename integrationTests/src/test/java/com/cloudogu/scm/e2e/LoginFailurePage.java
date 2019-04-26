package com.cloudogu.scm.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginFailurePage extends LoginPage {

    public LoginFailurePage(WebDriver driver) {
        super(driver);
    }

    public static ExpectedCondition present() {
        return ExpectedConditions.and(
                LoginPage.present(),
                ExpectedConditions.presenceOfElementLocated(By.className("alert-msg-credentials"))
        );
    }
}
