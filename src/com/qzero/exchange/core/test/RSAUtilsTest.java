package com.qzero.exchange.core.test;

import com.qzero.exchange.core.io.crypto.utils.RSAKeySet;
import com.qzero.exchange.core.io.crypto.utils.RSAUtils;
import org.junit.Test;

import java.util.Arrays;

public class RSAUtilsTest {

    @Test
    public void testRSAUtils() throws Exception{
        byte[] msg={1,2,3};
        RSAKeySet keySet= RSAUtils.genRSAKeySet();
        byte[] en=RSAUtils.publicEncrypt(msg,keySet.getPublicKeyImPem());
        byte[] de=RSAUtils.privateDecrypt(en,keySet.getPrivateKeyInPem());
        System.out.println(Arrays.toString(de));
    }

}
