package com.qzero.exchange.core.io.crypto;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Arrays;

/**
 * 加密参数
 */
public class CryptoParameter {

    /**
     * 参数名称
     */
    @JSONField(name = "name")
    private String name;
    /**
     * 参数具体内容
     */
    @JSONField(name = "parameter")
    private byte[] parameter;

    public CryptoParameter() {
    }

    public CryptoParameter(String name, byte[] parameter) {
        this.name = name;
        this.parameter = parameter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getParameter() {
        return parameter;
    }

    public void setParameter(byte[] parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "CryptoParameter{" +
                "name='" + name + '\'' +
                ", parameter=" + Arrays.toString(parameter) +
                '}';
    }
}
