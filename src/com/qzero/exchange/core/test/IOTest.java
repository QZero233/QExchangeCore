package com.qzero.exchange.core.test;

import com.qzero.exchange.core.QExchangeHelper;
import com.qzero.exchange.core.io.IQExchangeIOSource;
import com.qzero.exchange.core.io.TCPIOSource;
import com.qzero.exchange.core.io.crypto.impl.RSAModule;
import com.qzero.exchange.core.io.crypto.impl.ca.CAEntity;
import com.qzero.exchange.core.io.crypto.impl.ca.CAUtils;
import com.qzero.exchange.core.io.crypto.utils.RSAKeySet;
import com.qzero.exchange.core.io.crypto.utils.RSAUtils;
import com.qzero.exchange.core.io.crypto.utils.SHA256Utils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.net.ServerSocket;
import java.net.Socket;

public class IOTest {

    private static final Logger log = Logger.getLogger(IOTest.class);

    @Test
    public void testIO() throws Exception {
        //Start server
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    ServerSocket ss = new ServerSocket(8848);
                    while (true) {
                        Socket socket = ss.accept();

                        RSAKeySet keySet = RSAUtils.genRSAKeySet();
                        IQExchangeIOSource ioSource = new TCPIOSource(socket, new RSAModule(keySet, null, null));
                        final QExchangeHelper helper = new QExchangeHelper(ioSource);

                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                try {
                                    TestBean bean = helper.readObject(TestBean.class);
                                    log.debug("Server received\t" + bean);

                                    bean = new TestBean(10086, "服务器回敬");
                                    helper.writeObject(bean);
                                } catch (Exception e) {

                                }

                            }
                        }.start();

                    }
                } catch (Exception e) {
                    log.error("", e);
                }

            }
        }.start();

        RSAKeySet keySet = RSAUtils.genRSAKeySet();
        Thread.sleep(1000);
        //Start client
        IQExchangeIOSource source = new TCPIOSource("127.0.0.1", 8848, new RSAModule(keySet, null, null));
        QExchangeHelper helper = new QExchangeHelper(source);

        TestBean testBean = new TestBean(8848, "基♂ 因在染色体上----QExchange第二次测试（运用了RSA加密）大获成功！！！");
        log.debug(helper.writeObject(testBean));

        log.debug("Client received\t" + helper.readObject(TestBean.class));

        while (true) ;

    }

    @Test
    public void testHandShake() throws Exception {
        //Start server
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    ServerSocket ss = new ServerSocket(8848);
                    while (true) {
                        Socket socket = ss.accept();

                        IQExchangeIOSource ioSource = new TCPIOSource(socket, new TestCryptoModule(true));
                        final QExchangeHelper helper = new QExchangeHelper(ioSource);

                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                try {
                                    TestBean bean = helper.readObject(TestBean.class);
                                    log.debug("Server received\t" + bean);

                                    bean = new TestBean(10086, "服务器回敬");
                                    helper.writeObject(bean);
                                } catch (Exception e) {

                                }

                            }
                        }.start();

                    }
                } catch (Exception e) {
                    log.error("", e);
                }

            }
        }.start();

        Thread.sleep(1000);
        //Start client
        IQExchangeIOSource source = new TCPIOSource("127.0.0.1", 8848, new TestCryptoModule(false));
        QExchangeHelper helper = new QExchangeHelper(source);

        TestBean testBean = new TestBean(8848, "基♂ 因在染色体上----QExchange第三次测试（模拟了证书验证）大获成功！！！");
        log.debug(helper.writeObject(testBean));

        log.debug("Client received\t" + helper.readObject(TestBean.class));

        while (true) ;
    }

    @Test
    public void testCA() {
        RSAKeySet caKeySet = RSAUtils.genRSAKeySet();

        RSAKeySet keySetServer = RSAUtils.genRSAKeySet();

        CAEntity caEntity = new CAEntity("127.0.0.1", SHA256Utils.getSHA256(keySetServer.getPublicKeyImPem().getBytes()),
                System.currentTimeMillis() + 1000 * 60, null, null);
        caEntity = CAUtils.doSignature(caEntity, caKeySet);

        byte[] buf = CAUtils.CAEntityToBytes(caEntity);
        log.debug(CAUtils.verifyCA(CAUtils.bytesToCAEntity(buf), "127.0.0.1", keySetServer.getPublicKeyImPem()));
    }

    @Test
    public void testIOWithCA() throws Exception {
        RSAKeySet caKeySet = RSAUtils.genRSAKeySet();
        final RSAKeySet keySetServer = RSAUtils.genRSAKeySet();
        final RSAKeySet keySetClient = RSAUtils.genRSAKeySet();

        CAEntity serverCA = new CAEntity("127.0.0.1", SHA256Utils.getSHA256(keySetServer.getPublicKeyImPem().getBytes()), System.currentTimeMillis() + 1000 * 60, null, null);
        serverCA = CAUtils.doSignature(serverCA, caKeySet);

        final RSAModule rsaModuleServer = new RSAModule(keySetServer,  serverCA,null);
        final RSAModule rsaModuleClient = new RSAModule(keySetClient, null, "127.0.0.1");


        //Start server
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    ServerSocket ss = new ServerSocket(8848);
                    while (true) {
                        Socket socket = ss.accept();

                        IQExchangeIOSource ioSource = new TCPIOSource(socket, rsaModuleServer);
                        final QExchangeHelper helper = new QExchangeHelper(ioSource);

                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                try {
                                    TestBean bean = helper.readObject(TestBean.class);
                                    log.debug("Server received\t" + bean);

                                    bean = new TestBean(10086, "服务器回敬");
                                    helper.writeObject(bean);
                                } catch (Exception e) {

                                }

                            }
                        }.start();

                    }
                } catch (Exception e) {
                    log.error("", e);
                }

            }
        }.start();

        Thread.sleep(1000);
        //Start client
        IQExchangeIOSource source = new TCPIOSource("127.0.0.1", 8848,rsaModuleClient);
        QExchangeHelper helper = new QExchangeHelper(source);

        TestBean testBean = new TestBean(8848, "基♂ 因在染色体上----QExchange第三次测试（CA证书验证测试）大获成功！！！");
        log.debug(helper.writeObject(testBean));

        log.debug("Client received\t" + helper.readObject(TestBean.class));

        while (true) ;
    }

}

class TestBean {

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
