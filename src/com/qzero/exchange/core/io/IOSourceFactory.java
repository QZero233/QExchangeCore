package com.qzero.exchange.core.io;

import com.qzero.exchange.core.io.crypto.CryptoModuleFactory;
import com.qzero.exchange.core.io.crypto.IQExchangeCryptoModule;

import java.net.Socket;

public class IOSourceFactory {

    public static final String STANDARD_TCP_IPP_IO="Standard_TCP_IPP";

    /*public static IQExchangeIOSource getIOSource(String name){
        return getIOSource(name,CryptoModuleFactory.NO_CRYPTO_MODULE);
    }

    public static IQExchangeIOSource getIOSource(String name,String cryptoModuleName){
        return getIOSource(name, CryptoModuleFactory.getCryptoModule(cryptoModuleName));
    }

    public static IQExchangeIOSource getIOSource(String name, IQExchangeCryptoModule cryptoModule){
        IQExchangeIOSource ioSource=null;

        switch (name){
            case STANDARD_TCP_IPP_IO:
                ioSource=new TCPIOSource(cryptoModule);
                break;
        }

        return ioSource;
    }*/

}
