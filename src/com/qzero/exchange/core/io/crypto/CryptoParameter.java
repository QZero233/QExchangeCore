package com.qzero.exchange.core.io.crypto;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Arrays;

public class CryptoParameter {

    @JSONField(name = "name")
    private String name;
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
