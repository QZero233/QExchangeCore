package com.qzero.exchange.core;

import com.qzero.exchange.core.coder.CoderFactory;
import com.qzero.exchange.core.coder.IQExchangeCoder;
import com.qzero.exchange.core.io.IOSourceFactory;
import com.qzero.exchange.core.io.IQExchangeIOSource;
import com.qzero.exchange.core.io.crypto.CryptoModuleFactory;

public class GlobalConfiguration {

    private static final String DEFAULT_CODER_NAME= CoderFactory.STANDARD_JSON_CODER;
    private static final String DEFAULT_CRYPTO_MODULE_NAME= CryptoModuleFactory.STANDARD_RSA_CRYPTO_MODULE;
    private static final String DEFAULT_IO_SOURCE_NAME= IOSourceFactory.STANDARD_TCP_IPP_IO;

    private static IQExchangeCoder coder;
    private static IQExchangeIOSource ioSource;

    public static IQExchangeCoder getCoder() {
        return coder;
    }

    public static void setCoder(IQExchangeCoder coder) {
        GlobalConfiguration.coder = coder;
    }

    public static IQExchangeIOSource getIoSource() {
        return ioSource;
    }

    public static void setIoSource(IQExchangeIOSource ioSource) {
        GlobalConfiguration.ioSource = ioSource;
    }

    public static void doDefault(){
        coder=CoderFactory.getCoder(DEFAULT_CODER_NAME);
        ioSource=IOSourceFactory.getIOSource(DEFAULT_IO_SOURCE_NAME,DEFAULT_CRYPTO_MODULE_NAME);
    }
}