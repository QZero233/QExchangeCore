package com.qzero.exchange.core.loop;

/**
 * 保存有全局消息循环池的容器
 */
public class GlobalMessageLoopContainer {

    /**
     * 容器单例对象
     */
    private static GlobalMessageLoopContainer instance;

    /**
     * 全局消息循环池
     */
    private MessageLoop messageLoop;

    private GlobalMessageLoopContainer() {
        messageLoop=new MessageLoop();
    }

    public static GlobalMessageLoopContainer getInstance() {
        if(instance==null)
            instance=new GlobalMessageLoopContainer();
        return instance;
    }

    public MessageLoop getMessageLoop() {
        return messageLoop;
    }
}
