package com.cloudogu.scm.e2e;

import org.assertj.core.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Function;

abstract class Page {

    protected final WebDriver driver;

    protected Page(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    protected <V> V waitUntil(Function<? super WebDriver, V> isTrue) {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        return wait.until(isTrue);
    }

    public void verify() {
        if (!waitUntil((d) -> isDisplayed())) {
            Assertions.fail("Page " + this.getClass().getName() + " not complete");
        }
    }

    public boolean isDisplayed() {
        return true;
    }
}
