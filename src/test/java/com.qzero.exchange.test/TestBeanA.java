package com.qzero.exchange.test;

import com.qzero.exchange.core.coder.QExchangeParameterField;

public class TestBeanA{

    @QExchangeParameterField
    private String aId;
    @QExchangeParameterField
    private int aInt;
    @QExchangeParameterField
    private TestBeanB b;

    public TestBeanA() {
    }

    public TestBeanA(String aId, int aInt, TestBeanB b) {
        this.aId = aId;
        this.aInt = aInt;
        this.b = b;
    }

    public TestBeanB getB() {
        return b;
    }

    public void setB(TestBeanB b) {
        this.b = b;
    }

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }

    public int getaInt() {
        return aInt;
    }

    public void setaInt(int aInt) {
        this.aInt = aInt;
    }

    @Override
    public String toString() {
        return "TestBeanA{" +
                "aId='" + aId + '\'' +
                ", aInt=" + aInt +
                ", b=" + b +
                '}';
    }
}
