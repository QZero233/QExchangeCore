package com.qzero.exchange.core;

import com.qzero.exchange.core.coder.CoderFactory;
import com.qzero.exchange.core.io.IOSourceFactory;
import com.qzero.exchange.core.io.TCPIOSource;
import com.qzero.exchange.core.io.crypto.CryptoModuleFactory;

public class GlobalConfiguration {

    private static final String DEFAULT_CODER_NAME= CoderFactory.STANDARD_JSON_CODER;
    private static final String DEFAULT_CRYPTO_MODULE_NAME= CryptoModuleFactory.STANDARD_RSA_CRYPTO_MODULE;
    private static final String DEFAULT_IO_SOURCE_NAME= IOSourceFactory.STANDARD_TCP_IPP_IO;

    public static final int DEFAULT_TCP_PORT=8848;
    public static final String DEFAULT_TCP_IP="127.0.0.1";


    public static void doDefault(){
    }
}