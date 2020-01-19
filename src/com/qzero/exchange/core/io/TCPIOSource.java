package com.qzero.exchange.core.io;

import com.alibaba.fastjson.JSON;
import com.qzero.exchange.core.io.crypto.CryptoParameter;
import com.qzero.exchange.core.io.crypto.IQExchangeCryptoModule;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Datagram structure in tcp(Clear text)
 * length(in bytes) meaning
 * 4 The length of the whole datagram
 * 4 The length of action string in bytes
 * 4 The length of content
 * n Action string
 * 8 Send time
 * m content
 */

/**
 * Datagram structure in tcp(Cipher text)
 * length(in bytes) meaning
 * 4 The length of the whole encrypted datagram
 * n encrypted datagram
 */
public class TCPIOSource implements IQExchangeIOSource {

    private static final Logger log = Logger.getLogger(TCPIOSource.class);

    public static final String PARAMETER_TCP_REMOTE_IP = "tcpRemoteIp";

    public static final String PARAMETER_TCP_PORT = "tcpPort";

    private Socket socket;
    private boolean isServer;

    private InputStream is;
    private OutputStream os;

    private String remoteIp;
    private int port;

    private String role;

    private boolean initStage=true;

    private IQExchangeCryptoModule cryptoModule;

    public TCPIOSource(String remoteIp, int port, IQExchangeCryptoModule cryptoModule) {
        this.remoteIp = remoteIp;
        this.port = port;
        this.cryptoModule = cryptoModule;
        isServer=false;
        initSource();
    }

    public TCPIOSource(Socket socket, IQExchangeCryptoModule cryptoModule) {
        this.socket = socket;
        this.cryptoModule = cryptoModule;
        isServer=true;
        initSource();
    }

    private Datagram decodeDatagram(InputStream is) {
        try {
            byte[] intBuf = readDataFromInputStream(is, 4);
            int actionLength = byteArrayToInt(intBuf);

            intBuf = readDataFromInputStream(is, 4);
            int contentLength = byteArrayToInt(intBuf);

            byte[] actionStringBuf = readDataFromInputStream(is, actionLength);
            byte[] timeBuf = readDataFromInputStream(is, 8);

            byte[] content = readDataFromInputStream(is, contentLength);

            Datagram datagram = new Datagram(new String(actionStringBuf), byteArrayToLong(timeBuf), content);
            return datagram;
        } catch (Exception e) {
            log.error("Error when decoding datagram",e);
            return null;
        }
    }

    private byte[] encodeDatagram(Datagram datagram) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] actionStringBuf = datagram.getAction().getBytes();
            byte[] content = datagram.getContent();

            int length = 4 + 4 + 4 + actionStringBuf.length + 8 + content.length;
            outputStream.write(intToByteArray(length));
            outputStream.write(intToByteArray(actionStringBuf.length));
            outputStream.write(intToByteArray(content.length));
            outputStream.write(actionStringBuf);
            outputStream.write(longToByteArray(System.currentTimeMillis()));
            outputStream.write(content);

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error when encoding datagram " + datagram, e);
            return null;
        }


    }

    @Override
    public synchronized Datagram readDatagram() {
        if (socket == null && is == null)
            return null;

        try {
            if (is == null)
                is = socket.getInputStream();

            byte[] intBuf = readDataFromInputStream(is, 4);
            int length = byteArrayToInt(intBuf);
            length-=4;

            byte[] buf = readDataFromInputStream(is, length);

            if(!initStage){
                byte[] decrypted = cryptoModule.decrypt(buf);
                if (decrypted == null)
                    decrypted = buf;
                return decodeDatagram(new ByteArrayInputStream(decrypted,4,decrypted.length-4));
            }else{
                return decodeDatagram(new ByteArrayInputStream(buf));
            }




        } catch (Exception e) {
            log.error(role+" Error when reading datagram");
            return null;
        }
    }

    @Override
    public synchronized boolean writeDatagram(Datagram datagram) {
        try {

            if (os == null && socket == null)
                return false;

            if (os == null)
                os = socket.getOutputStream();

            byte[] encoded = encodeDatagram(datagram);
            if (encoded == null)
                return false;



            if(!initStage){
                byte[] encrypted = cryptoModule.encrypt(encoded);
                if (encrypted == null)
                    encrypted = encoded;

                os.write(intToByteArray(encrypted.length+4));
                os.write(encrypted);
            }else{
                os.write(encoded);
            }




            return true;
        } catch (Exception e) {
            log.error("Error when writing datagram " + datagram, e);
            return false;
        }
    }

    @Override
    public IOSourceStatus getSourceStatus() {
        boolean alive = false;
        if (socket != null && !socket.isClosed())
            alive = true;

        if (alive)
            return IOSourceStatus.STATUS_OPEN;
        else
            return IOSourceStatus.STATUS_CLOSED;
    }

    @Override
    public boolean initSource() {
        initStage=true;
        role=isServer?"Server":"Client";

        /*
        Exchange crypto parameters:
        Server send request first,client response
        Then client send request,server response
        If any one has nothing to request,it should send a datagram with action ACTION_CRYPTO_PARAMETER_OVER,the other one just need to request
        When both have sent a datagram with action ACTION_CRYPTO_PARAMETER_OVER,which means crypto parameters exchange over
         */
        try {

            if (!isServer) {
                socket = new Socket(remoteIp, port);
            }

            if (isServer) {
                List<String> neededList = cryptoModule.getNeededParametersList();
                if (neededList == null || neededList.isEmpty()){
                    sendOverSignal();
                }else{
                    sendRequest(neededList);
                }
            }

            int i = 0;
            boolean remoteOver=false;
            while (true) {
                Datagram datagram = readDatagram();
                String action = datagram.getAction();

                if (action.equals(Datagram.ACTION_SEND_CRYPTO_PARAMETER)) {
                    List<CryptoParameter> cryptoParameterList = JSON.parseArray(new String(datagram.getContent()), CryptoParameter.class);
                    if (cryptoParameterList == null)
                        continue;

                    log.debug(role+":"+"Got crypto parameter\t"+cryptoParameterList);
                    for (CryptoParameter cryptoParameter : cryptoParameterList) {
                        cryptoModule.fillParameter(cryptoParameter.getName(), cryptoParameter.getParameter());
                    }
                } else if (action.equals(Datagram.ACTION_REQUEST_CRYPTO_PARAMETER)) {
                    List<String> requestList = JSON.parseArray(new String(datagram.getContent()), String.class);
                    if (requestList == null)
                        continue;

                    log.debug(role+":"+"Got crypto parameter request\t"+requestList);
                    List<CryptoParameter> cryptoParameterList = new ArrayList<>();
                    for (String request : requestList) {
                        byte[] parameter = cryptoModule.getParameter(request);
                        if (parameter == null)
                            continue;

                        cryptoParameterList.add(new CryptoParameter(request, parameter));
                    }

                    response(cryptoParameterList);

                    List<String> neededList = cryptoModule.getNeededParametersList();
                    if (neededList == null || neededList.isEmpty()){
                        sendOverSignal();
                        break;
                    }

                    sendRequest(neededList);

                } else if (action.equals(Datagram.ACTION_CRYPTO_PARAMETER_OVER)) {
                    log.debug(role+":Remote exchange over");
                    remoteOver=true;
                    break;
                } else {
                    continue;
                }

                i++;
                if (i > 1000)
                    throw new Exception();
            }

            i=0;
            while(true){

                if(remoteOver){
                    //Which means remote has nothing to ask,you can just ask and receive answer
                    List<String> neededList = cryptoModule.getNeededParametersList();
                    if (neededList == null || neededList.isEmpty()){
                        sendOverSignal();
                        break;
                    }

                    sendRequest(neededList);
                    Datagram datagram=readDatagram();
                    String action = datagram.getAction();
                    if (action.equals(Datagram.ACTION_SEND_CRYPTO_PARAMETER)) {
                        List<CryptoParameter> cryptoParameterList = JSON.parseArray(new String(datagram.getContent()), CryptoParameter.class);
                        if (cryptoParameterList == null)
                            continue;

                        for (CryptoParameter cryptoParameter : cryptoParameterList) {
                            cryptoModule.fillParameter(cryptoParameter.getName(), cryptoParameter.getParameter());
                        }
                    }else
                        continue;
                }else{
                    //Which means you have nothing to ask,just answer remote's question
                    Datagram datagram=readDatagram();
                    String action = datagram.getAction();
                    if (action.equals(Datagram.ACTION_REQUEST_CRYPTO_PARAMETER)) {
                        List<String> requestList = JSON.parseArray(new String(datagram.getContent()), String.class);
                        if (requestList == null)
                            continue;

                        List<CryptoParameter> cryptoParameterList = new ArrayList<>();
                        for (String request : requestList) {
                            byte[] parameter = cryptoModule.getParameter(request);
                            if (parameter == null)
                                continue;

                            cryptoParameterList.add(new CryptoParameter(request, parameter));
                        }

                        response(cryptoParameterList);
                    }else if (action.equals(Datagram.ACTION_CRYPTO_PARAMETER_OVER)) {
                        //Remote has nothing to ask,just break
                        log.debug(role+":Remote exchange over");
                        break;
                    }else
                        continue;
                }

                i++;
                if(i>1000)
                    break;
            }


            initStage=false;
            return true;
        } catch (Exception e) {
            log.error("Error when init tcp io source", e);
            return false;
        }

    }

    private void sendOverSignal(){
        log.debug(role+":Exchange over");
        Datagram datagram=new Datagram(Datagram.ACTION_CRYPTO_PARAMETER_OVER,System.currentTimeMillis(),new byte[]{0});
        writeDatagram(datagram);
    }

    private void sendRequest(List<String> requestList) {
        log.debug(role+":Sent request\t"+requestList);
        String json = JSON.toJSONString(requestList);
        Datagram datagram = new Datagram(Datagram.ACTION_REQUEST_CRYPTO_PARAMETER, System.currentTimeMillis(), json.getBytes());
        writeDatagram(datagram);
    }

    private void response(List<CryptoParameter> parameterList) {
        log.debug(role+":Response\t"+parameterList);
        String json = JSON.toJSONString(parameterList);
        Datagram datagram = new Datagram(Datagram.ACTION_SEND_CRYPTO_PARAMETER, System.currentTimeMillis(), json.getBytes());
        writeDatagram(datagram);
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

    public static long byteArrayToLong(byte[] buf) {
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        return buffer.getLong();
    }

    public static byte[] longToByteArray(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(l);
        return buffer.array();
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
