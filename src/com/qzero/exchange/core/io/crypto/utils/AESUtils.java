package com.qzero.exchange.core.io.crypto.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

    public static byte[] aesEncrypt(byte[] clear,byte[] keyBuf){
        try{
            SecretKeySpec key = new SecretKeySpec(keyBuf, "AES/CBC/PKCS5PADDING");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(clear);
        }catch (Exception e){
            return null;
        }
    }
    public static byte[] aesDecrypt(byte[] encrypted,byte[] keyBuf){
        try{
            SecretKeySpec key = new SecretKeySpec(keyBuf, "AES/CBC/PKCS5PADDING");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encrypted);
        }catch (Exception e){
            return null;
        }
    }

}
