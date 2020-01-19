package com.qzero.exchange.core.io.crypto;

import com.qzero.exchange.core.io.crypto.impl.NoneModule;
import com.qzero.exchange.core.io.crypto.impl.RSAModule;

public class CryptoModuleFactory {

    public static final String STANDARD_RSA_CRYPTO_MODULE="StandardRSA";
    public static final String NO_CRYPTO_MODULE="None";

    /*public static IQExchangeCryptoModule getCryptoModule(String name){
        IQExchangeCryptoModule cryptoModule=null;

        switch (name){
            case STANDARD_RSA_CRYPTO_MODULE:
                cryptoModule=new RSAModule();
                break;
            case NO_CRYPTO_MODULE:
                cryptoModule=new NoneModule();
                break;
        }

        return cryptoModule;
    }*/

}
