package com.qzero.exchange.test;

import com.qzero.exchange.core.QExchangeRequest;
import com.qzero.exchange.core.coder.ActionSerializer;
import com.qzero.exchange.core.coder.ParameterCoder;
import com.qzero.exchange.core.utils.UUIDUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

public class SerializeTest {

    private static final Logger log=Logger.getRootLogger();

    @Test
    public void testSerialize() throws Exception{
        TestBeanB b=new TestBeanB(2,"Ming");
        TestBeanA a=new TestBeanA(UUIDUtils.getRandomUUID(),25,b);
        a.ae= TestBeanA.AE.AE_B;

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        ObjectOutputStream outputStream=new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(a);

        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream inputStream=new ObjectInputStream(byteArrayInputStream);
        a= (TestBeanA) inputStream.readObject();
        log.debug(a);
        log.debug(Arrays.toString(byteArrayOutputStream.toByteArray()));
    }

    @Test
    public void testCoder(){
        TestBeanB b=new TestBeanB(2,"Ming");
        TestBeanA a=new TestBeanA(UUIDUtils.getRandomUUID(),25,b);
        a.ae= TestBeanA.AE.AE_B;
        a.is=new int[]{1,2,3,4};

        ParameterCoder coder=new ParameterCoder();
        Map map= coder.encodeParameter(a);
        log.debug(map);

        TestBeanA a2=coder.decodeParameter(map,TestBeanA.class);
        log.debug(a2);
    }

    @Test
    public void testActionSerializer() throws Exception{
        TestBeanB b=new TestBeanB(2,"Ming");
        TestBeanA a=new TestBeanA(UUIDUtils.getRandomUUID(),25,b);

        ActionSerializer serializer=new ActionSerializer();

        QExchangeRequest request=new QExchangeRequest("login",a);
        byte[] buf=serializer.serializeAction(request);
        log.debug(Arrays.toString(buf));

        request= (QExchangeRequest) serializer.deserialize(buf);
        log.debug(request);
        log.debug(request.getParameterInObject(TestBeanA.class));
    }

    @Test
    public void testArray() throws Exception{
        int[] a={1,2,3};
        Type[] types=a.getClass().getGenericInterfaces();
        for(Type type:types){
            log.debug(type.getTypeName());
        }

        /*ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        ObjectOutputStream outputStream=new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(a);

        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream inputStream=new ObjectInputStream(byteArrayInputStream);
        a= (int[]) inputStream.readObject();
        log.debug(Arrays.toString(a));
        log.debug(Arrays.toString(byteArrayOutputStream.toByteArray()));*/

    }


}
