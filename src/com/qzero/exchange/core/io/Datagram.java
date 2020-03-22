package com.qzero.exchange.core.io;


/**
 * 数据包类，通常只用于承载序列化后的数据
 */
public class Datagram {

    /**
     * 表示动作为交换对象
     */
    public static final String ACTION_EXCHANGE_OBJECT="ObjectExchange";
    /**
     * 表示动作为发送加密参数
     */
    public static final String ACTION_SEND_CRYPTO_PARAMETER="SendCryptoParameter";
    /**
     * 表示动作为请求加密参数
     */
    public static final String ACTION_REQUEST_CRYPTO_PARAMETER="RequestCryptoParameter";
    /**
     * 表示我方加密参数交换结束
     */
    public static final String ACTION_CRYPTO_PARAMETER_OVER="CryptoParameterOver";

    /**
     * 数据包的动作
     */
    private String action;
    /**
     * 数据包的发送时间
     */
    private long sendTime;
    /**
     * 数据包的内容
     */
    private byte[] content;

    public Datagram() {
    }

    public Datagram(String action, long sendTime, byte[] content) {
        this.action = action;
        this.sendTime = sendTime;
        this.content = content;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Datagram{" +
                "action='" + action + '\'' +
                ", sendTime=" + sendTime +
                ", content=" + content +
                '}';
    }
}
