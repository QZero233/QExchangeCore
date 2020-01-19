package com.qzero.exchange.core.test;

import com.qzero.exchange.core.GlobalConfiguration;
import com.qzero.exchange.core.QExchangeHelper;
import com.qzero.exchange.core.io.IQExchangeIOSource;
import com.qzero.exchange.core.io.TCPIOSource;
import com.qzero.exchange.core.io.crypto.CryptoModuleFactory;
import com.qzero.exchange.core.io.crypto.impl.RSAModule;
import com.qzero.exchange.core.io.crypto.utils.RSAKeySet;
import com.qzero.exchange.core.io.crypto.utils.RSAUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.net.ServerSocket;
import java.net.Socket;

public class IOTest {

    private static final Logger log=Logger.getLogger(IOTest.class);

    @Test
    public void testIO() throws Exception{
        GlobalConfiguration.doDefault();

        //Start server
        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    ServerSocket ss=new ServerSocket(8848);
                    while (true){
                        Socket socket=ss.accept();

                        RSAKeySet keySet= RSAUtils.genRSAKeySet();
                        IQExchangeIOSource ioSource=new TCPIOSource(socket, new RSAModule(keySet));
                        final QExchangeHelper helper=new QExchangeHelper(ioSource);

                        new Thread(){
                            @Override
                            public void run() {
                                super.run();

                                try {
                                    Object bean=helper.readObject(TestBean.class);
                                    log.debug("Server received\n"+bean);
                                }catch (Exception e){

                                }

                            }
                        }.start();

                    }
                }catch (Exception e){
                    log.error("",e);
                }

            }
        }.start();

        RSAKeySet keySet= RSAUtils.genRSAKeySet();
        Thread.sleep(1000);
        //Start client
        IQExchangeIOSource source=new TCPIOSource("127.0.0.1",8848,new RSAModule(keySet));
        QExchangeHelper helper=new QExchangeHelper(source);

        TestBean testBean=new TestBean(8848,"基♂ 因在染色体上----QExchange第二次测试（运用了RSA加密）大获成功！！！");
        log.debug(helper.writeObject(testBean));

        while (true);

    }

}

class TestBean{

    private int id;
    private String name;

    public TestBean() {
    }

    public TestBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
