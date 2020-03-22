package com.qzero.exchange.core.test;

import com.qzero.exchange.core.QExchangeHelper;
import com.qzero.exchange.core.io.IQExchangeIOSource;
import com.qzero.exchange.core.io.TCPIOSource;
import com.qzero.exchange.core.io.crypto.impl.RSAModule;
import com.qzero.exchange.core.io.crypto.utils.RSAKeySet;
import com.qzero.exchange.core.io.crypto.utils.RSAUtils;
import com.qzero.exchange.core.loop.QExchangeListener;
import org.junit.Before;
import org.junit.Test;

import java.net.ServerSocket;
import java.net.Socket;

public class MessageLoopTest {

    @Before
    public void register(){
        GlobalClassLoader.registerClass("testBean",TestBean.class);
        GlobalClassLoader.registerClass("testStudent",TestStudent.class);
        GlobalMessageLoop.registerProcessClass(TestProcessClass.class,new TestProcessClass());
    }

    @Test
    public void testMessageLoop(){
        TestBean testBean=new TestBean(10086,"2333");
        TestStudent testStudent=new TestStudent(10086,"2333",100);
        GlobalMessageLoop.onObjectReceived(testBean);
        GlobalMessageLoop.onObjectReceived(testStudent);

        //GlobalMessageLoop.unregisterProcessClass(TestProcessClass.class);
        //GlobalMessageLoop.onObjectReceived(testBean);
    }

    @Test
    public void testMessageLoopWithTCP() throws Exception{
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
                                    Object bean = helper.readObject();
                                    GlobalMessageLoop.onObjectReceived(bean);
                                } catch (Exception e) {

                                }

                            }
                        }.start();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();

        RSAKeySet keySet = RSAUtils.genRSAKeySet();
        Thread.sleep(1000);
        //Start client
        IQExchangeIOSource source = new TCPIOSource("127.0.0.1", 8848, new RSAModule(keySet, null, null));
        QExchangeHelper helper = new QExchangeHelper(source);

        TestBean testBean = new TestBean(8848, "基♂ 因在染色体上----QExchange第二次测试（运用了RSA加密）大获成功！！！");
        TestStudent testStudent=new TestStudent(10086,"XiaoMing",60);
        helper.writeObject(testStudent);

        while (true) ;
    }

}

class TestProcessClass{

    @QExchangeListener(targets = {TestBean.class})
    public boolean receiveTestBean(TestBean testBean){
        System.out.println(""+testBean);
        return true;
    }

    @QExchangeListener(targets = {TestBean.class,TestStudent.class},priority = 3)
    public boolean receive(Object bean){
        if(bean instanceof TestBean){
            System.out.println("TestBean "+((TestBean) bean).getId());
        }else if(bean instanceof TestStudent){
            System.out.println("TestStudent "+((TestStudent) bean).getScore());
        }
        return true;
    }

}

class TestStudent{
    private int id;
    private String name;
    private int score;

    public TestStudent() {
    }

    public TestStudent(int id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "TestStudent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
