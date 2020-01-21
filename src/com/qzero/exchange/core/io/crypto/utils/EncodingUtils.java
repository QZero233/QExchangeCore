package com.qzero.exchange.core.io.crypto.utils;

import java.util.Base64;

public class EncodingUtils {

    public static String base64encode(byte[] buf){
        return Base64.getEncoder().encodeToString(buf);
    }
    public static byte[] base64decode(String base64){
        return Base64.getDecoder().decode(base64);
    }

}
