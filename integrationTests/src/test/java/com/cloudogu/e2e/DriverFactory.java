package com.cloudogu.e2e;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

class DriverFactory {

    static WebDriver getDriver(String testName) throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setAcceptInsecureCerts(true);
        capabilities.setCapability(CapabilityType.BROWSER_NAME, BrowserType.CHROME);
        capabilities.setCapability(CapabilityType.PLATFORM_NAME, Platform.LINUX);
        capabilities.setCapability("name", testName);
        return new RemoteWebDriver(new URL(System.getenv("SELENIUM_REMOTE_URL")), capabilities);
    }
}
