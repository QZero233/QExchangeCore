package com.qzero.exchange.core.io.crypto.utils;

import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtils {


    public static final String RSA = "RSA";
    public static final int KEY_SIZE = 2048;
    public static final int CLEAR_MAX_SIZE = (KEY_SIZE/8)-11;
    public static final int CIPHER_MAX_SIZE = 256;

    private static final Logger log=Logger.getLogger(RSAUtils.class);

    private KeyFactory keyFactory;
    private RSAPublicKey publicKey = null;
    private RSAPrivateKey privateKey = null;
    private RSAKeySet keySet;

    public RSAUtils(){
        try {
            keyFactory = KeyFactory.getInstance(RSA);
        }catch (Exception e){
            log.error("初始化RSAUtils对象时失败",e);
        }
    }

    public RSAUtils(RSAKeySet keySet){
        this();
        loadKeySet(keySet);
    }

    public void loadKeySet(RSAKeySet keySet){
        this.keySet=keySet;
        if(keySet==null)
            return;

        try{
            if(keySet.getPub()!=null && !keySet.getPub().equals("")){
                X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(keySet.getPub()));
                publicKey = (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
            }
            if(keySet.getPri()!=null && !keySet.getPri().equals("")){
                PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keySet.getPri()));
                privateKey = (RSAPrivateKey) keyFactory.generatePrivate(pKCS8EncodedKeySpec);
            }
        }catch (Exception e){
            log.error("装载密钥对时错误",e);
        }

    }

    public RSAKeySet getKeySet(){
        return keySet;
    }

    public static RSAKeySet genRSAKeySet(){
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA);
            keyPairGen.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            String pub = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            String pri = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            return new RSAKeySet(pub,pri);
        }catch (Exception e){
            log.error("生成RSA密钥对时失败",e);
            return null;
        }

    }

    /**
     * 公钥加密过程, 明文长度小于 (公钥长度 / 8) - 11
     *
     * @param clearText 明文数据
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public byte[] publicEncryptMini(byte[] clearText) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);

        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] output = cipher.doFinal(clearText);

        return output;
    }

    /**
     * 私钥加密过程, 明文长度小于 (私钥长度 / 8) - 11
     *
     * @param clearText 明文数据
     * @return 密文，base64编码
     * @throws Exception 加密过程中的异常信息
     */
    public byte[] privateEncryptMini(byte[] clearText) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);

        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        byte[] output = cipher.doFinal(clearText);

        return output;


    }

    /**
     * 私钥解密过程
     *
     * @param cipherText 密文
     * @return 明文
     * @throws Exception
     */
    public byte[] privateDecryptMini(byte[] cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] output = cipher.doFinal(cipherText);
        return output;
    }

    /**
     * 公钥解密过程
     *
     * @param cipherText 密文
     * @return 明文
     * @throws Exception
     */
    public byte[] publicDecryptMini(byte[] cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] output = cipher.doFinal(cipherText);
        return output;
    }

    /**
     * 公钥加密 适用于明文过长
     *
     * @param clearText 明文数据
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public byte[] publicEncrypt(byte[] clearText) throws Exception {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        byte[] buf=clearText;
        ByteArrayInputStream inputStream=new ByteArrayInputStream(buf);

        int currentIndex = 0;
        int bufLength = buf.length;
        while (currentIndex < bufLength) {
            int endIndex = (currentIndex + CLEAR_MAX_SIZE) > bufLength ? bufLength : currentIndex + CLEAR_MAX_SIZE;

            byte[] tmpBuf=new byte[endIndex-currentIndex];
            inputStream.read(tmpBuf);

            outputStream.write(publicEncryptMini(tmpBuf));
            currentIndex += CLEAR_MAX_SIZE;
        }
        return outputStream.toByteArray();
    }

    /**
     * 私钥加密 适用于明文过长
     *
     * @param clearText 明文数据
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public byte[] privateEncrypt(byte[] clearText) throws Exception {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        byte[] buf=clearText;
        ByteArrayInputStream inputStream=new ByteArrayInputStream(buf);

        int currentIndex = 0;
        int bufLength = buf.length;
        while (currentIndex < bufLength) {
            int endIndex = (currentIndex + CLEAR_MAX_SIZE) > bufLength ? bufLength : currentIndex + CLEAR_MAX_SIZE;

            byte[] tmpBuf=new byte[endIndex-currentIndex];
            inputStream.read(tmpBuf);

            outputStream.write(privateEncryptMini(tmpBuf));
            currentIndex += CLEAR_MAX_SIZE;
        }
        return outputStream.toByteArray();
    }

    /**
     * 私钥解密 适用于密文过长
     *
     * @param cipherText 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public byte[] privateDecrypt(byte[] cipherText) throws Exception {
        byte[] buf=cipherText;
        ByteArrayInputStream inputStream=new ByteArrayInputStream(buf);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        int currentIndex = 0;
        int bufLength = buf.length;
        while (currentIndex < bufLength) {
            int endIndex = (currentIndex + CIPHER_MAX_SIZE) > bufLength ? bufLength : currentIndex + CIPHER_MAX_SIZE;

            byte[] tmpBuf=new byte[endIndex-currentIndex];
            inputStream.read(tmpBuf);

            outputStream.write(privateDecryptMini(tmpBuf));
            currentIndex += CIPHER_MAX_SIZE;
        }

        return outputStream.toByteArray();
    }

    /**
     * 公钥解密 适用于密文过长
     *
     * @param cipherText 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public byte[] publicDecrypt(byte[] cipherText) throws Exception {
        byte[] buf=cipherText;
        ByteArrayInputStream inputStream=new ByteArrayInputStream(buf);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        int currentIndex = 0;
        int bufLength = buf.length;
        while (currentIndex < bufLength) {
            int endIndex = (currentIndex + CIPHER_MAX_SIZE) > bufLength ? bufLength : currentIndex + CIPHER_MAX_SIZE;

            byte[] tmpBuf=new byte[endIndex-currentIndex];
            inputStream.read(tmpBuf);

            outputStream.write(publicDecryptMini(tmpBuf));
            currentIndex += CIPHER_MAX_SIZE;
        }

        return outputStream.toByteArray();
    }

}
