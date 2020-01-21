package com.qzero.exchange.core.io.crypto;

import com.qzero.exchange.core.io.crypto.impl.NoneModule;
import com.qzero.exchange.core.io.crypto.impl.RSAModule;
import com.qzero.exchange.core.io.crypto.utils.RSAKeySet;

public class CryptoModuleFactory {



    public static NoneModule getNoneModule(){
        return new NoneModule();
    }

}
