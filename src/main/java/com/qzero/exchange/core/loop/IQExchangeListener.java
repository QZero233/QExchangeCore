package com.qzero.exchange.core.loop;

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
     * 当对象到达时会调用
     * @param obj
     * @return 当前操作是否成功，如果返回false将停止调用下一个监听器
     */
    boolean onObjectReceived(Object obj);

}
