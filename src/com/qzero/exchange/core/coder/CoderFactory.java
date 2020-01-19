package com.qzero.exchange.core.coder;

public class CoderFactory {

    public static final String STANDARD_JSON_CODER="StandardJsonCoder";

    public static IQExchangeCoder getCoder(String name){
        IQExchangeCoder coder=null;

        switch (name){
            case STANDARD_JSON_CODER:
                break;
        }

        return coder;
    }

}
