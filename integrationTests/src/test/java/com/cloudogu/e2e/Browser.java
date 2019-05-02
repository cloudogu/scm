package com.cloudogu.e2e;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.fail;

public class Browser {

    private static final ThreadLocal<Browser> INSTANCES = ThreadLocal.withInitial(Browser::new);

    private State<? extends Page> currentPage;

    private Browser() {
    }

    public static Browser browser() {
        return INSTANCES.get();
    }

    public <T extends Page> void assertAtPage(Class<T> pageClass) {
        try {
            waitForRequiredFields(pageClass);
            T page = pageClass.getConstructor(WebDriver.class).newInstance(Driver.webDriver);
            page.verify();
            this.currentPage = new State<>(pageClass, page);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            fail("could not initialize new page for class " + pageClass.getName());
        }
    }

    private <T extends Page> void waitForRequiredFields(Class<T> pageClass) {
        Arrays.stream(pageClass.getDeclaredFields())
                .filter(field -> field.getAnnotationsByType(Required.class).length > 0)
                .map(field -> field.getAnnotationsByType(FindBy.class))
                .filter(fields -> fields.length > 0)
                .forEach(this::waitUntilVisible);
    }

    private void waitUntilVisible(FindBy... findBys) {
        WebDriverWait wait = new WebDriverWait(Driver.webDriver, 20);
        wait.until(ExpectedConditions.and(createVisibleConditions(findBys)));
    }

    private ExpectedCondition[] createVisibleConditions(FindBy[] findBys) {
        return Arrays.stream(findBys)
                    .map(f -> new FindBy.FindByBuilder().buildIt(f, null))
                    .map(ExpectedConditions::visibilityOfElementLocated)
                    .toArray(ExpectedCondition[]::new);
    }

    public <T extends Page> T getPage(Class<T> pageClass) {
        return currentPage.getPage(pageClass);
    }

    public void openUrl(String url) {
        Driver.webDriver.get(url);
    }

    private static class State<T extends Page> {
        private final Class<T> pageClass;
        private final T page;

        private State(Class<T> pageClass, T page) {
            this.pageClass = pageClass;
            this.page = page;
        }

        private <V> V getPage(Class<V> targetPage) {
            if (targetPage.isAssignableFrom(pageClass)) {
                return (V) page;
            }
            throw new IllegalStateException(String.format("current page %s does not equals expected page %s", pageClass.getName(), targetPage.getName()));
        }
    }
}
