package com.cloudogu.e2e;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface LoadTimeout {

    int DEFAULT_TIMEOUT_IN_SECONDS = 3;

    int timeoutInSeconds() default DEFAULT_TIMEOUT_IN_SECONDS;
}
