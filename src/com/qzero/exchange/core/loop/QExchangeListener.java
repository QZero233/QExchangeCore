package com.qzero.exchange.core.loop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QExchangeListener {

    Class[] targets();

    /**
     * The less one will be called first
     * And it should be 1-10
     * @return
     */
    int priority() default GlobalMessageLoop.PRIORITY_USER_LEVEL;

}
