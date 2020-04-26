package com.qzero.exchange.test;

import com.qzero.exchange.core.coder.QExchangeParameterField;

import java.io.Serializable;

public class TestBeanB implements Serializable{

    @QExchangeParameterField
    private int bId;
    @QExchangeParameterField
    private String bName;

    public TestBeanB() {
    }

    public TestBeanB(int bId, String bName) {
        this.bId = bId;
        this.bName = bName;
    }

    public int getbId() {
        return bId;
    }

    public void setbId(int bId) {
        this.bId = bId;
    }

    public String getbName() {
        return bName;
    }

    public void setbName(String bName) {
        this.bName = bName;
    }

    @Override
    public String toString() {
        return "TestBeanB{" +
                "bId=" + bId +
                ", bName='" + bName + '\'' +
                '}';
    }
}
