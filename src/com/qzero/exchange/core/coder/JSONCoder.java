package com.qzero.exchange.core.coder;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Structure:
 * length(in bytes) meaning
 * 4 length of name
 * n name
 * l-n content in json
 */
public class JSONCoder implements IQExchangeCoder {

    private static final Logger log=Logger.getLogger(JSONCoder.class);

    @Override
    public Object decode(byte[] in) {
        try {
            ByteArrayInputStream inputStream=new ByteArrayInputStream(in);

            byte[] nameLengthBuf=readDataFromInputStream(inputStream,4);

            int nameLength=byteArrayToInt(nameLengthBuf);
            byte[] nameBuf=readDataFromInputStream(inputStream,nameLength);
            String name=new String(nameBuf);
            Class type;
            if((type=GlobalClassLoader.getClassByName(name))==null)
                return null;

            byte[] content=readDataFromInputStream(inputStream,in.length-4-nameLength);
            return JSON.parseObject(new String(content),type);
        }catch (Exception e){
            log.error("Error when decoding",e);
            return null;
        }

    }

    @Override
    public byte[] encode(Object bean) {
        try {
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            String name=GlobalClassLoader.getNameByType(bean.getClass());
            if(name==null)
                return null;

            outputStream.write(intToByteArray(name.length()));
            outputStream.write(name.getBytes());
            outputStream.write(JSON.toJSONString(bean).getBytes());

            return outputStream.toByteArray();
        }catch (Exception e){
            log.error("Error when encoding object\t"+bean,e);
            return null;
        }
    }

    private byte[] readDataFromInputStream(InputStream is, int length) throws Exception {
        if(length==0)
            return new byte[0];

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(length);
        byte[] buf = new byte[length];
        int len;
        while (true) {
            len = is.read(buf, 0, length);
            length -= len;
            outputStream.write(buf, 0, len);
            if (length == 0)
                break;
        }

        return outputStream.toByteArray();

    }

    public static int byteArrayToInt(byte[] buf) {
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        return buffer.getInt();
    }

    public static byte[] intToByteArray(int i) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(i);
        return buffer.array();
    }
}
