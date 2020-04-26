package com.qzero.exchange.test;

import com.qzero.exchange.core.QExchangeAction;
import com.qzero.exchange.core.QExchangeHelper;
import com.qzero.exchange.core.QExchangeRequest;
import com.qzero.exchange.core.QExchangeResponse;
import com.qzero.exchange.core.io.TCPIOSource;
import com.qzero.exchange.core.io.crypto.impl.RSAModule;
import com.qzero.exchange.core.loop.MessageLoop;
import com.qzero.exchange.core.loop.QExchangeListener;
import com.qzero.exchange.core.utils.UUIDUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.net.ServerSocket;

public class ActionUpdateTest {

    private static final Logger log=Logger.getRootLogger();

    private static final String A_SET_NAME="aSet";
    private static final String A_GET_NAME="aGet";

    private QExchangeHelper clientHelper;
    private QExchangeHelper serverHelper;
    private TestBeanA tmpA;

    @QExchangeListener(actionType = QExchangeAction.ActionType.ACTION_TYPE_RESPONSE,actionNameList = {A_SET_NAME,A_GET_NAME})
    private boolean clientProcess(QExchangeResponse response){
        String name=response.getActionName();

        if(name.equals(A_GET_NAME)){
            TestBeanA a=response.getParameterInObject(TestBeanA.class);
            log.debug("Client got a:"+a);
        }else{
            log.debug("Client got result for setting:"+response.getStatusCode());

            QExchangeRequest request=new QExchangeRequest(A_GET_NAME,null);
            clientHelper.writeAction(request);
        }

        return true;
    }

    @QExchangeListener(actionType = QExchangeAction.ActionType.ACTION_TYPE_REQUEST,actionNameList = {A_SET_NAME,A_GET_NAME})
    private boolean serverProcess(QExchangeRequest request){


        String name=request.getActionName();

        if(name.equals(A_SET_NAME)){
            TestBeanA a=request.getParameterInObject(TestBeanA.class);
            log.debug("Server got a:"+a);
            tmpA=a;

            QExchangeResponse response=new QExchangeResponse(A_SET_NAME,QExchangeResponse.STATUS_CODE_SUCCEEDED,
                    null);
            serverHelper.writeAction(response);
        }else{
            log.debug("Server got request for getting");

            if(tmpA==null){
                QExchangeResponse response=new QExchangeResponse(A_GET_NAME,QExchangeResponse.STATUS_CODE_FAILED,
                        null);
                serverHelper.writeAction(response);
                return true;
            }

            QExchangeResponse response=new QExchangeResponse(A_GET_NAME,QExchangeResponse.STATUS_CODE_SUCCEEDED,
                    tmpA);
            serverHelper.writeAction(response);
        }

        return true;
    }

    @Test
    public void server() throws Exception{
        ServerSocket serverSocket=new ServerSocket(6666);
        TCPIOSource source=TCPIOSource.buildSourceForServer(serverSocket.accept(),
                RSAModule.buildBasicRSAModule(RSAModule.TRANSFORMATION_SERVER));

        serverHelper=new QExchangeHelper(source);
        MessageLoop messageLoop=new MessageLoop();
        messageLoop.registerProcessClass(ActionUpdateTest.class,this);

        new Thread(){
            @Override
            public void run() {
                super.run();

                QExchangeAction action;
                while ((action=serverHelper.readAction())!=null){
                    messageLoop.onActionReceived(action);
                }

            }
        }.start();

        Thread.sleep(60*1000*1000);
    }

    @Test
    public void client() throws Exception{
        TCPIOSource source=TCPIOSource.buildSourceForClient("127.0.0.1",6666,
                RSAModule.buildBasicRSAModule(RSAModule.TRANSFORMATION_SERVER));
        clientHelper=new QExchangeHelper(source);

        MessageLoop messageLoop=new MessageLoop();
        messageLoop.registerProcessClass(ActionUpdateTest.class,this);


        new Thread(){
            @Override
            public void run() {
                super.run();

                QExchangeAction action;
                while ((action=clientHelper.readAction())!=null){
                    messageLoop.onActionReceived(action);
                }

            }
        }.start();


        TestBeanB b=new TestBeanB(12,"wdnmd");
        TestBeanA a=new TestBeanA(UUIDUtils.getRandomUUID(),2333,b);

        QExchangeRequest request=new QExchangeRequest(A_SET_NAME,a);
        clientHelper.writeAction(request);

        Thread.sleep(60*1000*1000);
    }

}
