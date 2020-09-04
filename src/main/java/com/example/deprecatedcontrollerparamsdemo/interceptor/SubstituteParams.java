package com.example.deprecatedcontrollerparamsdemo.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubstituteParams {

    Param[] defaultParams() default {@Param(key = "deprecatedParam", value = "actualParam"), @Param(key = "oldParam", value = "newParam"), @Param(key = "build", value = "featureset")};

    Param[] customParams() default {};

    Class<?>[] defaultTarget() default {String.class};

    Class<?>[] customTarget() default {};

    @interface Param {

        String key();

        String value();
    }
}
