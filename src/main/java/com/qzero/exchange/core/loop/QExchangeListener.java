package com.qzero.exchange.core.loop;

import com.qzero.exchange.core.QExchangeAction;

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
     * 所关注的操作的名称
     */
    String[] actionNameList() default {};

    /**
     * 所关注的操作的类型
     */
    QExchangeAction.ActionType actionType();

    /**
     * 优先级
     * 优先级小的会被先调用
     * 优先级范围为[1,10]
     */
    int priority() default MessageLoop.PRIORITY_USER_LEVEL;

}
