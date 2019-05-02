package com.cloudogu.e2e;

import com.thoughtworks.gauge.Step;

import static com.cloudogu.e2e.Browser.browser;
import static org.assertj.core.api.Assertions.assertThat;

public class BrowserSteps {

    @Step("Current urlPattern matches <urlPattern>")
    public void assertCurrentUrlMatches(String urlPattern) {
        assertThat(browser().getPage(Page.class).currentUrl()).matches(urlPattern);
    }
}
