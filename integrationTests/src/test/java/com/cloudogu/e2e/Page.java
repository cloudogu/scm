package com.cloudogu.e2e;

import org.assertj.core.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Function;

@LoadTimeout
public abstract class Page {

    private final WebDriver driver;

    protected Page(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    protected <V> V waitUntil(Function<? super WebDriver, V> isTrue) {
        int timeOutInSeconds = loadTimeoutForPage();
        return waitUntil(isTrue, timeOutInSeconds);
    }

    protected  <V> V waitUntil(Function<? super WebDriver, V> isTrue, int timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        return wait.until(isTrue);
    }

    private int loadTimeoutForPage() {
        return getClass().getAnnotationsByType(LoadTimeout.class)[0].timeoutInSeconds();
    }

    /**
     * Wait until {@link #isDisplayed()} returns <code>true</code> to assert that the page is displayed.
     * Calls {@link Assertions#fail(String)} if this is not be the case after
     */
    void verify() {
        if (!waitUntil((d) -> isDisplayed())) {
            Assertions.fail("Page " + this.getClass().getName() + " not complete");
        }
    }

    public boolean isDisplayed() {
        return true;
    }
}
