package com.qzero.exchange.core.io.crypto.impl;

import com.qzero.exchange.core.io.crypto.CryptoParametersStore;
import com.qzero.exchange.core.io.crypto.IQExchangeCryptoModule;
import com.qzero.exchange.core.io.crypto.utils.RSAKeySet;
import com.qzero.exchange.core.io.crypto.utils.RSAUtils;

import java.util.ArrayList;
import java.util.List;

public class RSAModule implements IQExchangeCryptoModule {

    public static final String PARAMETER_LOCAL_PUBLIC_KEY="localRSAPublicKey";
    public static final String PARAMETER_LOCAL_PRIVATE_KEY="localRSAPrivateKey";
    public static final String PARAMETER_REMOTE_PUBLIC_KEY="remotePublicKey";

    private String remotePublicKey=null;

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
        byte[] privateKey=CryptoParametersStore.get(PARAMETER_LOCAL_PRIVATE_KEY);
        if(privateKey==null)
            return null;

        try {
            RSAUtils rsaUtils=new RSAUtils(new RSAKeySet(null,new String(privateKey)));
            return rsaUtils.privateDecrypt(in);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public byte[] getParameter(String name) {
        switch (name){
            case PARAMETER_REMOTE_PUBLIC_KEY:
                if(remotePublicKey!=null)
                    return CryptoParametersStore.get(PARAMETER_LOCAL_PUBLIC_KEY);
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
