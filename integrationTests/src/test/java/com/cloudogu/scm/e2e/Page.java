package com.cloudogu.scm.e2e;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Function;

public class Page {

    private final WebDriver driver;

    protected Page(WebDriver driver) {
        this.driver = driver;
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    protected <V> V waitUntil(Function<? super WebDriver, V> isTrue) {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        return wait.until(isTrue);
    }
}
