package com.qzero.exchange.core.io.crypto.impl;

import com.qzero.exchange.core.io.crypto.IQExchangeCryptoModule;
import com.qzero.exchange.core.io.crypto.impl.ca.CAEntity;
import com.qzero.exchange.core.io.crypto.impl.ca.CAUtils;
import com.qzero.exchange.core.io.crypto.utils.RSAKeySet;
import com.qzero.exchange.core.io.crypto.utils.RSAUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 进行RSA操作的加密模块
 */
public class RSAModule implements IQExchangeCryptoModule {

    public static final String TRANSFORMATION_SERVER=RSAUtils.RSA;
    public static final String TRANSFORMATION_ANDROID="RSA/ECB/PKCS1Padding";

    public static final String PARAMETER_REMOTE_PUBLIC_KEY="remotePublicKey";
    public static final String PARAMETER_REMOTE_CA="remoteCA";

    private String remotePublicKey=null;

    private RSAKeySet localKeySet;
    private boolean needCA;
    private CAEntity localCA;
    private String remoteIdentity;

    private boolean isRemoteCAVerified=false;

    private RSAUtils rsaUtils;
    private CAUtils caUtils;

    public static RSAModule buildBasicRSAModule(String transformation){
        return new RSAModule(RSAUtils.genRSAKeySet(),null,null,transformation);
    }

    public RSAModule(RSAKeySet localKeySet, CAEntity localCA, String remoteIdentity,String transformation) {
        this.localKeySet = localKeySet;
        this.localCA = localCA;
        this.remoteIdentity = remoteIdentity;

        if(remoteIdentity==null)
            needCA=false;
        else
            needCA=true;

        rsaUtils=new RSAUtils(transformation);
        caUtils=new CAUtils(rsaUtils);
    }


    @Override
    public byte[] encrypt(byte[] in) {
        if(remotePublicKey==null)
            return null;

        try {
            return rsaUtils.publicEncrypt(in,remotePublicKey);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public byte[] decrypt(byte[] in) {
        try {
            return rsaUtils.privateDecrypt(in,localKeySet.getPrivateKeyInPem());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] getParameter(String name) {
        switch (name){
            case PARAMETER_REMOTE_PUBLIC_KEY:
                if(localKeySet!=null)
                    return localKeySet.getPublicKeyImPem().getBytes();
                else
                    return null;
            case PARAMETER_REMOTE_CA:
                if(localCA==null)
                    return null;
                return CAUtils.CAEntityToBytes(localCA);
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
            case PARAMETER_REMOTE_CA:
                CAEntity caEntity= CAUtils.bytesToCAEntity(parameter);
                if(!caUtils.verifyCA(caEntity,remoteIdentity,remotePublicKey))
                    throw new IllegalArgumentException("Illegal CA");
                else
                    isRemoteCAVerified=true;

        }
    }

    @Override
    public List<String> getNeededParametersList() {
        List<String> needed=new ArrayList<>();

        if(remotePublicKey==null)
            needed.add(PARAMETER_REMOTE_PUBLIC_KEY);

        if(needCA && !isRemoteCAVerified)
            needed.add(PARAMETER_REMOTE_CA);

        return needed;
    }
}
