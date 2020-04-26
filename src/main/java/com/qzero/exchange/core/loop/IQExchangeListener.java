package com.qzero.exchange.core.loop;

import com.qzero.exchange.core.QExchangeAction;

/**
 * 数据包监听器接口
 * 在消息循环池中注册
 * 当符合条件的对象到达会按照优先级调用
 */
public interface IQExchangeListener {

    /**
     * @return 该监听器的ID
     */
    String getId();

    /**
     * @return 监听器的优先级，越低越先调用
     */
    int getPriority();

    /**
     * @return 监听的动作的名称
     */
    String getActionName();

    /**
     * @return 监听的动作的类型，如果为空则表示全监听
     */
    QExchangeAction.ActionType getActionType();

    /**
     * 当对象到达时会调用
     * @param action
     * @return 当前操作是否成功，如果返回false将停止调用下一个监听器
     */
    boolean onObjectReceived(QExchangeAction action);

}
