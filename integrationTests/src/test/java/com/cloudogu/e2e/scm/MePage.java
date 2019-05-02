package com.cloudogu.e2e.scm;

import com.cloudogu.e2e.Required;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class MePage extends ScmManagerRootPage {

    @Required
    @FindBy(xpath = "//tr[th/text() = 'Username']/td")
    WebElement usernameField;
    @FindBy(xpath = "//tr[th/text() = 'Display Name']/td")
    WebElement displayNameField;
    @FindBy(xpath = "//tr[th/text() = 'E-Mail']/td")
    WebElement eMailField;
    @FindBy(xpath = "//tr[th/text() = 'Groups']/td/ul")
    WebElement groupList;

    public MePage(WebDriver driver) {
        super(driver);
    }

    public String username() {
        return usernameField.getText().trim();
    }

    public String displayName() {
        return displayNameField.getText().trim();
    }

    public String eMail() {
        return eMailField.getText().trim();
    }

    public Collection<String> groups() {
        return groupList
                .findElements(By.tagName("li"))
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(toList());
    }
}
