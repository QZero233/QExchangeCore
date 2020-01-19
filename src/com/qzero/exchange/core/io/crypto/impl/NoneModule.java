package com.qzero.exchange.core.io.crypto.impl;

import com.qzero.exchange.core.io.crypto.IQExchangeCryptoModule;

import java.util.List;

public class NoneModule implements IQExchangeCryptoModule {
    @Override
    public byte[] encrypt(byte[] in) {
        return in;
    }

    @Override
    public byte[] decrypt(byte[] in) {
        return in;
    }

    @Override
    public byte[] getParameter(String name) {
        return null;
    }

    @Override
    public void fillParameter(String name, byte[] parameter) {

    }

    @Override
    public List<String> getNeededParametersList() {
        return null;
    }
}
