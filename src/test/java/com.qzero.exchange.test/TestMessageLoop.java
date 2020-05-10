package com.qzero.exchange.test;

import com.qzero.exchange.core.QExchangeAction;
import com.qzero.exchange.core.QExchangeRequest;
import com.qzero.exchange.core.loop.IQExchangeListener;
import com.qzero.exchange.core.loop.MessageLoop;
import com.qzero.exchange.core.utils.UUIDUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

public class TestMessageLoop {

    private static final Logger log=Logger.getRootLogger();

    @Test
    public void testMessageLoop(){
        MessageLoop messageLoop=new MessageLoop();

        messageLoop.registerListener(new IQExchangeListener() {

            private String id= UUIDUtils.getRandomUUID();

            @Override
            public String getId() {
                return id;
            }

            @Override
            public int getPriority() {
                return 1;
            }

            @Override
            public String getActionName() {
                return "action";
            }

            @Override
            public QExchangeAction.ActionType getActionType() {
                return QExchangeAction.ActionType.ACTION_TYPE_REQUEST;
            }

            @Override
            public boolean onObjectReceived(QExchangeAction action) {

                log.debug("开始卸载");
                messageLoop.unregisterListener(getActionName(),getId(),getPriority());

                return true;
            }
        });

        messageLoop.onActionReceived(new QExchangeRequest("action",null));
        messageLoop.onActionReceived(new QExchangeRequest("action",null));

    }

}
