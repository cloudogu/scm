package com.cloudogu.scm.e2e;

import driver.Driver;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationTargetException;

import static com.cloudogu.scm.e2e.Config.BASE_URL;

class Browser {

    private State<? extends Page> currentPage;

    <T extends Page> void assertAtPage(Class<T> pageClass) {
        try {
            T page = pageClass.getConstructor(WebDriver.class).newInstance(Driver.webDriver);
            page.verify();
            this.currentPage = new State<>(pageClass, page);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("could not initialize new page for class " + pageClass.getName());
        }
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
