package com.qzero.exchange.core.io.crypto.impl;

import com.qzero.exchange.core.io.crypto.IQExchangeCryptoModule;
import com.qzero.exchange.core.io.crypto.utils.RSAKeySet;
import com.qzero.exchange.core.io.crypto.utils.RSAUtils;

import java.util.ArrayList;
import java.util.List;

public class RSAModule implements IQExchangeCryptoModule {

    public static final String PARAMETER_REMOTE_PUBLIC_KEY="remotePublicKey";

    private String remotePublicKey=null;

    private RSAKeySet localKeySet;

    public RSAModule(RSAKeySet localKeySet) {
        this.localKeySet = localKeySet;
    }

    @Override
    public byte[] encrypt(byte[] in) {
        if(remotePublicKey==null)
            return null;

        try {
            RSAUtils rsaUtils=new RSAUtils(new RSAKeySet(remotePublicKey,null));
            return rsaUtils.publicEncrypt(in);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public byte[] decrypt(byte[] in) {
        try {
            RSAUtils rsaUtils=new RSAUtils(localKeySet);
            return rsaUtils.privateDecrypt(in);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public byte[] getParameter(String name) {
        switch (name){
            case PARAMETER_REMOTE_PUBLIC_KEY:
                if(localKeySet!=null)
                    return localKeySet.getPub().getBytes();
                else
                    return null;
        }

        return null;
    }

    @Override
    public void fillParameter(String name, byte[] parameter) {
        if(parameter==null)
            return;

        switch (name){
            case PARAMETER_REMOTE_PUBLIC_KEY:
                remotePublicKey=new String(parameter);
                break;
        }
    }

    @Override
    public List<String> getNeededParametersList() {
        List<String> needed=new ArrayList<>();

        if(remotePublicKey==null)
            needed.add(PARAMETER_REMOTE_PUBLIC_KEY);

        if(needed.isEmpty())
            return null;

        return needed;
    }
}
