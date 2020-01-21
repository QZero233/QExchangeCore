package com.qzero.exchange.core.loop;

public interface IQExchangeListener {

    String getId();
    int getPriority();
    boolean onObjectReceived(Object obj);

}
