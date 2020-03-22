package com.qzero.exchange.core.io.crypto.utils;

import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.*;
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

    private static final Logger log= Logger.getLogger(RSAUtils.class);

    public static PublicKey loadPublicKey(String publicKeyInPem){
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyInPem));
            PublicKey publicKey =  keyFactory.generatePublic(x509EncodedKeySpec);
            return publicKey;
        }catch (Exception e){
            log.error("Error when loading public key with following key\n"+publicKeyInPem,e);
            return null;
        }
    }

    public static PrivateKey loadPrivateKey(String privateKeyInPem){
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyInPem));
            PrivateKey privateKey =  keyFactory.generatePrivate(pKCS8EncodedKeySpec);
            return privateKey;
        }catch (Exception e){
            log.error("Error when loading private key with following key\n"+privateKeyInPem,e);
            return null;
        }
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

    private static byte[] publicEncryptMini(byte[] clearText,String publicKeyInPem) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE,loadPublicKey(publicKeyInPem));
        byte[] output = cipher.doFinal(clearText);
        return output;
    }

    private static byte[] privateEncryptMini(byte[] clearText,String privateKeyInPem) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, loadPrivateKey(privateKeyInPem));
        byte[] output = cipher.doFinal(clearText);
        return output;
    }

    private static byte[] privateDecryptMini(byte[] cipherText,String privateKeyInPem) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey(privateKeyInPem));
        byte[] output = cipher.doFinal(cipherText);
        return output;
    }

    private static byte[] publicDecryptMini(byte[] cipherText,String publicKeyInPem) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, loadPublicKey(publicKeyInPem));
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
    public static byte[] publicEncrypt(byte[] clearText,String publicKeyInPem) throws Exception {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        byte[] buf=clearText;
        ByteArrayInputStream inputStream=new ByteArrayInputStream(buf);

        int currentIndex = 0;
        int bufLength = buf.length;
        while (currentIndex < bufLength) {
            int endIndex = (currentIndex + CLEAR_MAX_SIZE) > bufLength ? bufLength : currentIndex + CLEAR_MAX_SIZE;

            byte[] tmpBuf=new byte[endIndex-currentIndex];
            inputStream.read(tmpBuf);

            outputStream.write(publicEncryptMini(tmpBuf,publicKeyInPem));
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
    public static byte[] privateEncrypt(byte[] clearText,String privateKeyInPem) throws Exception {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        byte[] buf=clearText;
        ByteArrayInputStream inputStream=new ByteArrayInputStream(buf);

        int currentIndex = 0;
        int bufLength = buf.length;
        while (currentIndex < bufLength) {
            int endIndex = (currentIndex + CLEAR_MAX_SIZE) > bufLength ? bufLength : currentIndex + CLEAR_MAX_SIZE;

            byte[] tmpBuf=new byte[endIndex-currentIndex];
            inputStream.read(tmpBuf);

            outputStream.write(privateEncryptMini(tmpBuf,privateKeyInPem));
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
    public static byte[] privateDecrypt(byte[] cipherText,String privateKeyInPem) throws Exception {
        byte[] buf=cipherText;
        ByteArrayInputStream inputStream=new ByteArrayInputStream(buf);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        int currentIndex = 0;
        int bufLength = buf.length;
        while (currentIndex < bufLength) {
            int endIndex = (currentIndex + CIPHER_MAX_SIZE) > bufLength ? bufLength : currentIndex + CIPHER_MAX_SIZE;

            byte[] tmpBuf=new byte[endIndex-currentIndex];
            inputStream.read(tmpBuf);

            outputStream.write(privateDecryptMini(tmpBuf,privateKeyInPem));
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
    public static byte[] publicDecrypt(byte[] cipherText,String publicKeyInPem) throws Exception {
        byte[] buf=cipherText;
        ByteArrayInputStream inputStream=new ByteArrayInputStream(buf);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        int currentIndex = 0;
        int bufLength = buf.length;
        while (currentIndex < bufLength) {
            int endIndex = (currentIndex + CIPHER_MAX_SIZE) > bufLength ? bufLength : currentIndex + CIPHER_MAX_SIZE;

            byte[] tmpBuf=new byte[endIndex-currentIndex];
            inputStream.read(tmpBuf);

            outputStream.write(publicDecryptMini(tmpBuf,publicKeyInPem));
            currentIndex += CIPHER_MAX_SIZE;
        }

        return outputStream.toByteArray();
    }

    public static String getPurePublicKey(String origin){
        origin="+"+origin+"+";
        String[] tmp=origin.split("-----BEGIN RSA PUBLIC KEY-----");
        if(tmp.length!=2)
            return "";

        tmp=tmp[1].split("-----END RSA PUBLIC KEY-----");
        if(tmp.length!=2)
            return "";
        return tmp[0];
    }

}
