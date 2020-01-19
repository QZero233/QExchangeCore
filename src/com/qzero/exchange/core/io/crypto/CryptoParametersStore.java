package com.qzero.exchange.core.io.crypto;

import java.util.HashMap;
import java.util.Map;

public class CryptoParametersStore {

    private static Map<String,byte[]> parameters=new HashMap<>();

    public static void add(String name,byte[] parameter){
        parameters.put(name,parameter);
    }

    public static byte[] get(String name){
        return parameters.get(name);
    }

}
