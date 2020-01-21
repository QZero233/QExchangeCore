package com.qzero.exchange.core.test;

import com.qzero.exchange.core.io.crypto.IQExchangeCryptoModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestCryptoModule implements IQExchangeCryptoModule {

    private int myAsk= new Random().nextInt();
    private byte[] remoteAskBuf;

    private byte[] key=null;

    private boolean host;
    private boolean verified=false;

    public TestCryptoModule(boolean host) {
        this.host = host;
    }

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

        if(host){
            switch (name){
                case "ask":
                    return String.valueOf(myAsk).getBytes();
                default:
                    return null;
            }
        }else{
            switch (name){
                case "ver":
                    if(remoteAskBuf==null)
                        return null;
                    return String.valueOf(Integer.parseInt(new String(remoteAskBuf))+1).getBytes();
                case "key":
                    return new byte[]{1,2,3};
                default:
                    return null;
            }
        }

    }

    @Override
    public void fillParameter(String name, byte[] parameter) {

        if(host){
            switch (name) {
                case "ver":
                    int resp=Integer.parseInt(new String(parameter));

                    if(resp!=myAsk+1)
                        throw new IllegalArgumentException("Verify error it should be "+(myAsk+1));
                    else
                        verified=true;
                    break;
                case "key":
                    key=parameter;
                    break;
            }
        }else{
            switch (name) {
                case "ask":
                    remoteAskBuf = parameter;
                    break;
            }
        }

    }

    @Override
    public List<String> getNeededParametersList() {
        List<String> list=new ArrayList<>();

        if(host){
            if(!verified)
                list.add("ver");
            else if(key==null)
                list.add("key");
        }else{
            if(remoteAskBuf==null)
                list.add("ask");
        }


        return list;
    }
}
