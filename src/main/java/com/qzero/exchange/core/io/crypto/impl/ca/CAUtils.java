package com.qzero.exchange.core.io.crypto.impl.ca;

import com.alibaba.fastjson.JSON;
import com.qzero.exchange.core.io.crypto.utils.RSAKeySet;
import com.qzero.exchange.core.io.crypto.utils.RSAUtils;
import com.qzero.exchange.core.io.crypto.utils.SHA256Utils;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class CAUtils {

    private static final Logger log= Logger.getLogger(CAUtils.class);

    public static byte[] CAContentToBytes(CAEntity caEntity){
        try {
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            outputStream.write(caEntity.getIdentity().getBytes());
            outputStream.write(caEntity.getRemotePublicKeyHash());
            outputStream.write(longToByteArray(caEntity.getEndTime()));

            return outputStream.toByteArray();
        }catch (Exception e){
            log.error("Error when formatting CAContent to bytes\n"+caEntity,e);
            return null;
        }
    }

    public static byte[] CAEntityToBytes(CAEntity caEntity){
        return JSON.toJSONString(caEntity).getBytes();
    }

    public static CAEntity bytesToCAEntity(byte[] buf){
        return JSON.parseObject(new String(buf), CAEntity.class);
    }


    public static CAEntity doSignature(CAEntity caEntityWithContent, RSAKeySet signatureKeySet){

        try {
            byte[] content=CAContentToBytes(caEntityWithContent);
            byte[] contentHash= SHA256Utils.getSHA256(content);

            byte[] signature= RSAUtils.privateEncrypt(contentHash,signatureKeySet.getPrivateKeyInPem());

            caEntityWithContent.setSignature(signature);
            caEntityWithContent.setSignaturePublicKey(signatureKeySet.getPublicKeyImPem());

            return caEntityWithContent;
        }catch (Exception e){
            log.error("Error when do signature\n"+caEntityWithContent+"\n"+signatureKeySet,e);
            return null;
        }
    }

    public static boolean verifyCA(CAEntity caEntity, String remoteIdentify, String remotePublicKey){
        //TODO FIRST VERIFY IF THE PUBLIC KEY IS TRUSTED
        try {

            if(!caEntity.getIdentity().equals(remoteIdentify))
                return false;

            if(System.currentTimeMillis()>caEntity.getEndTime())
                return false;

            byte[] remotePublicKeyHashComputed= SHA256Utils.getSHA256(remotePublicKey.getBytes());
            if(!Arrays.equals(remotePublicKeyHashComputed,caEntity.getRemotePublicKeyHash()))
                return false;

            String signaturePublicKey=caEntity.getSignaturePublicKey();
            byte[] signature=caEntity.getSignature();
            byte[] contentHashDecrypted= RSAUtils.publicDecrypt(signature,signaturePublicKey);
            if(contentHashDecrypted==null)
                return false;

            byte[] content=CAContentToBytes(caEntity);
            byte[] contentHashComputed= SHA256Utils.getSHA256(content);

            if(!Arrays.equals(contentHashDecrypted,contentHashComputed))
                return false;

            return true;

        }catch (Exception e){
            log.error("Error when verify ca"+caEntity,e);
            return false;
        }


    }


    public static long byteArrayToLong(byte[] buf) {
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        return buffer.getLong();
    }

    public static byte[] longToByteArray(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(l);
        return buffer.array();
    }

}
