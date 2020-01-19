package com.qzero.exchange.core.io;


public class Datagram {

    public static final String ACTION_EXCHANGE_OBJECT="ObjectExchange";
    public static final String ACTION_SEND_CRYPTO_PARAMETER="SendCryptoParameter";
    public static final String ACTION_REQUEST_CRYPTO_PARAMETER="RequestCryptoParameter";
    public static final String ACTION_CRYPTO_PARAMETER_OVER="CryptoParameterOver";

    /**
     * The action of the datagram
     */
    private String action;
    /**
     * The time when it was sent
     */
    private long sendTime;
    /**
     * The content of the datagram
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
