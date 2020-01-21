package com.qzero.exchange.core.loop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QExchangeListener {

    Class[] target();

    /**
     * The less one will be called first
     * @return
     */
    int priority() default 1;

}
