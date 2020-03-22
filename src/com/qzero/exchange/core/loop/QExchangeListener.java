package com.qzero.exchange.core.loop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个方法是对象监听方法的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QExchangeListener {

    /**
     * 获取关注对象的类
     * 如果名称不为空，那么这一项会被忽略
     */
    Class[] targetsClass() default {};

    /**
     * 获取关注对象的名称
     */
    String[] targetsName() default {};

    /**
     * 优先级
     * 优先级小的会被先调用
     * 优先级范围为[1,10]
     */
    int priority() default MessageLoop.PRIORITY_USER_LEVEL;

}
