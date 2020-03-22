package com.qzero.exchange.test;

import com.qzero.exchange.core.GlobalClassLoader;
import com.qzero.exchange.core.PackedObject;
import com.qzero.exchange.core.QExchangeHelper;
import com.qzero.exchange.core.io.TCPIOSource;
import com.qzero.exchange.core.io.crypto.impl.RSAModule;
import com.qzero.exchange.core.io.crypto.utils.RSAUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.net.ServerSocket;
import java.net.Socket;

public class TCPInitTest {

    private static final Logger log=Logger.getRootLogger();

    @Before
    public void regClasses(){
        GlobalClassLoader.registerClassWithFields(TestBean.class);
    }

    @Test
    public void server() throws Exception{
        System.out.println("server");

        ServerSocket serverSocket=new ServerSocket(8848);
        Socket socket=serverSocket.accept();

        RSAModule rsaModule=new RSAModule(RSAUtils.genRSAKeySet(),null,null,RSAModule.TRANSFORMATION_SERVER);
        TCPIOSource ioSource=new TCPIOSource(true,socket,rsaModule);

        QExchangeHelper helper=new QExchangeHelper(ioSource);
        TestBean testBean=new TestBean(1,"ServerBean");
        helper.writeObject(new PackedObject(testBean));

        PackedObject packedObject=helper.readObject();
        log.info("Server:"+packedObject);

        Thread.sleep(1000*60*60*60);
    }

    @Test
    public void client() throws Exception{
        RSAModule rsaModule=new RSAModule(RSAUtils.genRSAKeySet(),null,null,RSAModule.TRANSFORMATION_SERVER);
        TCPIOSource ioSource=new TCPIOSource("127.0.0.1",8848,rsaModule);
        QExchangeHelper helper=new QExchangeHelper(ioSource);

        PackedObject packedObject=helper.readObject();
        log.info("Client:"+packedObject);

        TestBean testBean=new TestBean(2,"ClientBean");
        helper.writeObject(new PackedObject(testBean));

        Thread.sleep(1000*60*60*60);
    }

}
