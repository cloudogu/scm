package com.cloudogu.e2e;

import com.thoughtworks.gauge.AfterStep;
import com.thoughtworks.gauge.ExecutionContext;
import org.openqa.selenium.Cookie;

/**
 * Hooks for zalenium integration.
 */
public class ZaleniumStepHooks {

    /**
     * Sets the zalenium test cookie to mark the test as failed, but only if the step is marked as failed.
     *
     * @param context gauge execution context
     */
    @AfterStep
    public void handle(ExecutionContext context) {
        if (context.getCurrentStep().getIsFailing()) {
            Cookie cookie = new Cookie("zaleniumTestPassed", "false");
            Driver.webDriver.manage().addCookie(cookie);
        }
    }

}
