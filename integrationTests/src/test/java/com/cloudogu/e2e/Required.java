package com.cloudogu.e2e;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field on a page that is also annotated with {@link org.openqa.selenium.support.FindBy} as a <em>required</em>
 * field, meaning that we assume we are currently <em>not</em> at this specific page when the element is missing.
 * This can be seen as a prerequisite before the corresponding pages {@link Page#verify()} method may be called.
 * The presence of the field will be verified by {@link Browser#assertAtPage(Class)} method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
}
