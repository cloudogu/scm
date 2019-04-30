package com.cloudogu.scm.e2e;

import driver.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static com.cloudogu.scm.e2e.Config.BASE_URL;

class Browser {

    private State<? extends Page> currentPage;

    <T extends Page> void assertAtPage(Class<T> pageClass) {
        try {
            Arrays.stream(pageClass.getDeclaredFields())
                    .filter(field -> field.getAnnotationsByType(Required.class).length > 0)
                    .map(field -> field.getAnnotationsByType(FindBy.class))
                    .filter(fields -> fields.length > 0)
                    .forEach(this::isVisible);

            T page = pageClass.getConstructor(WebDriver.class).newInstance(Driver.webDriver);
            page.verify();
            this.currentPage = new State<>(pageClass, page);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("could not initialize new page for class " + pageClass.getName());
        }
    }

    private void isVisible(FindBy[] findBIES) {
        ExpectedCondition[] expectedConditions = Arrays.stream(findBIES)
                .map(f -> new FindBy.FindByBuilder().buildIt(f, null))
                .map(ExpectedConditions::visibilityOfElementLocated)
                .toArray(ExpectedCondition[]::new);
        WebDriverWait wait = new WebDriverWait(Driver.webDriver, 20);
        wait.until(ExpectedConditions.and(expectedConditions));
    }

    <T extends Page> T getPage(Class<T> pageClass) {
        return currentPage.getPage(pageClass);
    }

    void openStartPage() {
        openPage(BASE_URL + "/scm");
    }

    <T extends Page> void openPage(String url) {
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
