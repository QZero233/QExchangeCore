package com.qzero.exchange.core.coder;

import com.alibaba.fastjson.JSON;
import com.qzero.exchange.core.GlobalClassLoader;
import com.qzero.exchange.core.PackedObject;
import com.qzero.exchange.core.utils.StreamUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * JSON序列化器
 * 序列化后的数据结构:
 * 长度(in bytes) 意义
 * 4 类名称的长度
 * n 类名称
 * l-n json的内容
 */
public class JSONCoder implements IQExchangeCoder {

    private static final Logger log= Logger.getLogger(JSONCoder.class);

    @Override
    public PackedObject decode(byte[] in) {
        try {
            ByteArrayInputStream inputStream=new ByteArrayInputStream(in);

            int nameLength=StreamUtils.readIntWith4Bytes(inputStream);
            byte[] nameBuf=StreamUtils.readSpecifiedLengthDataFromInputStream(inputStream,nameLength);
            String name=new String(nameBuf);
            Class type;
            if((type= GlobalClassLoader.getClassByName(name))==null)
                return null;

            byte[] content=StreamUtils.readSpecifiedLengthDataFromInputStream(inputStream,in.length-4-nameLength);

            Object obj= JSON.parseObject(new String(content),type);
            return new PackedObject(name,obj);
        }catch (Exception e){
            log.error(String.format("JSON反序列化对象时失败，输入的数据为%s\n", Arrays.toString(in)),e);
            return null;
        }

    }

    @Override
    public byte[] encode(PackedObject packedObject) {
        if(packedObject==null)
            return null;

        try {
            String name=packedObject.getName();
            Object bean=packedObject.getObject();
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

            byte[] nameBuf=name.getBytes();
            StreamUtils.writeIntWith4Bytes(outputStream,nameBuf.length);
            outputStream.write(nameBuf);
            outputStream.write(JSON.toJSONString(bean).getBytes());

            return outputStream.toByteArray();
        }catch (Exception e){
            log.error(String.format("JSON序列化对象时失败，输入的对象为%s\n", packedObject+""),e);
            return null;
        }
    }

}
