package com.qzero.exchange.core.io;

import com.alibaba.fastjson.JSON;
import com.qzero.exchange.core.io.crypto.CryptoParameter;
import com.qzero.exchange.core.io.crypto.IQExchangeCryptoModule;
import com.qzero.exchange.core.utils.StreamUtils;
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
 * 基于TCP的IO源
 * <p>
 * 密文数据包结构
 * length(in bytes) meaning
 * 4 密文数据包的总长度（包含这4字节）
 * n 密文数据包内容（也就是加密过的明文数据包）
 * <p>
 * 明文数据包结构
 * length(in bytes) meaning
 * 4 明文数据包总长度（包含这4字节）
 * 4 动作标识符长度
 * 4 内容长度
 * n 动作标识符
 * 8 发送时间
 * m 数据包内容
 */
public class TCPIOSource implements IQExchangeIOSource {

    private static final Logger log = Logger.getLogger(TCPIOSource.class);

    /**
     * 套接字
     */
    private Socket socket;

    /**
     * 是否为服务器
     */
    private boolean isServer;

    /**
     * 套接字的输入流
     */
    private InputStream is;
    /**
     * 套接字的输出流
     */
    private OutputStream os;

    /**
     * 对方的IP，用于打印日志
     */
    private String remoteIp;
    /**
     * 连接端口，用于打印日志
     */
    private int port;
    /**
     * 日志最后跟的东西
     * 通常是 \t(ip:port)\t
     */
    private String lEnd;

    /**
     * 是否还是初始化阶段
     */
    private boolean initStage = true;

    /**
     * 加解密模块
     */
    private IQExchangeCryptoModule cryptoModule;

    /**
     * 客户端用的构造函数
     * @param remoteIp
     * @param port
     * @param cryptoModule
     */
    private TCPIOSource(String remoteIp, int port, IQExchangeCryptoModule cryptoModule) {
        this.remoteIp = remoteIp;
        this.port = port;
        this.cryptoModule = cryptoModule;
        isServer = false;
        initSource();
    }

    /**
     * 服务端用的构造函数
     * @param isServer
     * @param socket
     * @param cryptoModule
     */
    private TCPIOSource(boolean isServer, Socket socket, IQExchangeCryptoModule cryptoModule) {
        this.isServer = isServer;
        this.socket = socket;
        this.cryptoModule = cryptoModule;
        initSource();
    }

    public static TCPIOSource buildSourceForClient(String remoteIp, int port, IQExchangeCryptoModule cryptoModule){
        return new TCPIOSource(remoteIp,port,cryptoModule);
    }

    public static TCPIOSource buildSourceForServer(Socket socket, IQExchangeCryptoModule cryptoModule){
        return new TCPIOSource(true,socket,cryptoModule);

    }

    @Override
    public boolean initSource() {
        initStage = true;

        /*
        加密参数交换规定
        服务器首先请求加密参数，然后客户端相应
        接着客户端请求加密参数，服务器相应
        如果任何一方没有需求了,它将发一个动作为ACTION_CRYPTO_PARAMETER_OVER的数据包,另一个就只需要请求并读取响应
        如果另一方也没需求了，同样需要发送一个动作为ACTION_CRYPTO_PARAMETER_OVER的数据包
        当两边都发送了一个动作为ACTION_CRYPTO_PARAMETER_OVER的数据包,代表加密参数交换结束
         */
        try {

            if (socket == null) {
                socket = new Socket(remoteIp, port);
            } else {
                remoteIp = socket.getInetAddress().getHostAddress();
                port = socket.getPort();
            }
            lEnd = String.format("\t(%s:%d)\t", remoteIp, port);

            if (isServer) {
                List<String> neededList = cryptoModule.getNeededParametersList();
                if (neededList == null || neededList.isEmpty()) {
                    sendOverSignal();
                } else {
                    sendRequest(neededList);
                }
            }

            int i = 0;
            boolean remoteOver = false;
            while (true) {
                Datagram datagram = readDatagram();
                String action = datagram.getAction();

                if (action.equals(Datagram.ACTION_SEND_CRYPTO_PARAMETER)) {
                    List<CryptoParameter> cryptoParameterList = JSON.parseArray(new String(datagram.getContent()), CryptoParameter.class);
                    if (cryptoParameterList == null)
                        continue;

                    for (CryptoParameter cryptoParameter : cryptoParameterList) {
                        cryptoModule.fillParameter(cryptoParameter.getName(), cryptoParameter.getParameter());
                    }
                    log.debug("得到加密参数，已填充进加密模块中\t" + cryptoParameterList + lEnd);
                } else if (action.equals(Datagram.ACTION_REQUEST_CRYPTO_PARAMETER)) {
                    List<String> requestList = JSON.parseArray(new String(datagram.getContent()), String.class);
                    if (requestList == null)
                        continue;

                    log.debug("收到加密参数请求，准备开始回应\t" + requestList + lEnd);
                    List<CryptoParameter> cryptoParameterList = new ArrayList<>();
                    for (String request : requestList) {
                        byte[] parameter = cryptoModule.getParameter(request);
                        if (parameter == null)
                            continue;

                        cryptoParameterList.add(new CryptoParameter(request, parameter));
                    }

                    response(cryptoParameterList);

                    List<String> neededList = cryptoModule.getNeededParametersList();
                    if (neededList == null || neededList.isEmpty()) {
                        sendOverSignal();
                        break;
                    }

                    sendRequest(neededList);
                } else if (action.equals(Datagram.ACTION_CRYPTO_PARAMETER_OVER)) {
                    log.debug("对方加密参数交换结束" + lEnd);
                    remoteOver = true;
                    break;
                } else {
                    continue;
                }

                i++;
                if (i > 1000)
                    throw new Exception();
            }

            i = 0;
            while (true) {

                if (remoteOver) {
                    //对方加密参数请求阶段结束，这时自己只需请求并读取即可
                    List<String> neededList = cryptoModule.getNeededParametersList();
                    if (neededList == null || neededList.isEmpty()) {
                        sendOverSignal();
                        break;
                    }

                    sendRequest(neededList);
                    Datagram datagram = readDatagram();
                    String action = datagram.getAction();
                    if (action.equals(Datagram.ACTION_SEND_CRYPTO_PARAMETER)) {
                        List<CryptoParameter> cryptoParameterList = JSON.parseArray(new String(datagram.getContent()), CryptoParameter.class);
                        if (cryptoParameterList == null)
                            continue;

                        for (CryptoParameter cryptoParameter : cryptoParameterList) {
                            cryptoModule.fillParameter(cryptoParameter.getName(), cryptoParameter.getParameter());
                        }
                    } else
                        continue;
                } else {
                    //自己没有需求了，只需相应即可
                    Datagram datagram = readDatagram();
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
                    } else if (action.equals(Datagram.ACTION_CRYPTO_PARAMETER_OVER)) {
                        //对方也没需求了，可以结束了
                        log.debug("对方加密参数交换结束" + lEnd);
                        break;
                    } else
                        continue;
                }

                i++;
                if (i > 1000)
                    break;
            }


            initStage = false;
            return true;
        } catch (Exception e) {
            log.error("初始化TCP源时失败" + lEnd, e);
            disconnect();
            return false;
        }

    }

    @Override
    public Datagram readDatagram() {
        if (socket == null && is == null)
            return null;

        try {
            if (is == null)
                is = socket.getInputStream();

            synchronized (is) {
                int length = StreamUtils.readIntWith4Bytes(is);
                length -= 4;

                byte[] buf = StreamUtils.readSpecifiedLengthDataFromInputStream(is, length);

                //如果是初始化阶段，也就是加密参数还没交换完成时，就不解密
                if (!initStage) {
                    byte[] decrypted = cryptoModule.decrypt(buf);
                    if (decrypted == null)
                        decrypted = buf;
                    return decodeDatagram(new ByteArrayInputStream(decrypted, 4, decrypted.length - 4));
                } else {
                    return decodeDatagram(new ByteArrayInputStream(buf));
                }

            }


        } catch (Exception e) {
            log.error("读取数据包失败"+lEnd,e);
            return null;
        }


    }

    @Override
    public boolean writeDatagram(Datagram datagram) {
        try {
            if (os == null && socket == null)
                return false;

            if (os == null)
                os = socket.getOutputStream();

            synchronized (os) {
                byte[] encoded = encodeDatagram(datagram);
                if (encoded == null)
                    return false;


                if (!initStage) {
                    byte[] encrypted = cryptoModule.encrypt(encoded);
                    if (encrypted == null)
                        encrypted = encoded;

                    StreamUtils.writeIntWith4Bytes(os,encrypted.length + 4);
                    os.write(encrypted);
                } else {
                    os.write(encoded);
                }
            }

            return true;
        } catch (Exception e) {
            log.error(String.format("写数据包时异常，数据包内容如下\n%s", datagram+"")+lEnd, e);
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

    /**
     * 反序列化数据包
     * @param is 包含明文数据包序列化数据的数据流
     * @return 反序列化的数据包，失败返回null
     */
    private Datagram decodeDatagram(InputStream is) {
        try {
            int actionLength = StreamUtils.readIntWith4Bytes(is);
            int contentLength = StreamUtils.readIntWith4Bytes(is);

            byte[] actionStringBuf = StreamUtils.readSpecifiedLengthDataFromInputStream(is,actionLength);
            long time=StreamUtils.readLongWith8Bytes(is);

            byte[] content = StreamUtils.readSpecifiedLengthDataFromInputStream(is,contentLength);

            Datagram datagram = new Datagram(new String(actionStringBuf), time, content);
            return datagram;
        } catch (Exception e) {
            log.error("反序列化数据包时失败"+lEnd, e);
            return null;
        }
    }

    /**
     * 序列化数据包
     * @param datagram 需要序列化的数据包
     * @return 序列化后的数据，失败返回null
     */
    private byte[] encodeDatagram(Datagram datagram) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] actionStringBuf = datagram.getAction().getBytes();
            byte[] content = datagram.getContent();

            int length = 4 + 4 + 4 + actionStringBuf.length + 8 + content.length;
            StreamUtils.writeIntWith4Bytes(outputStream,length);
            StreamUtils.writeIntWith4Bytes(outputStream,actionStringBuf.length);
            StreamUtils.writeIntWith4Bytes(outputStream,content.length);
            outputStream.write(actionStringBuf);
            StreamUtils.writeLongWith8Bytes(outputStream,System.currentTimeMillis());
            outputStream.write(content);

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error(String.format("序列化数据包失败，数据包如下\n%s", datagram+"") + lEnd, e);
            return null;
        }
    }

    /**
     * 手动断开连接
     */
    public void disconnect() {
        try {
            socket.close();
        } catch (Exception e) {
            log.error("断开连接时失败"+lEnd, e);
        }
    }

    /**
     * 发送我方所需加密参数完成的信号
     */
    private void sendOverSignal() {
        log.debug("我方所需加密参数已完备"+lEnd);
        Datagram datagram = new Datagram(Datagram.ACTION_CRYPTO_PARAMETER_OVER, System.currentTimeMillis(), new byte[]{0});
        writeDatagram(datagram);
    }

    /**
     * 发送加密参数需求列表
     * @param requestList 加密参数需求列表
     */
    private void sendRequest(List<String> requestList) {
        log.debug("加密参数需求已发送，需求如下\n" + requestList+"\n"+lEnd);
        String json = JSON.toJSONString(requestList);
        Datagram datagram = new Datagram(Datagram.ACTION_REQUEST_CRYPTO_PARAMETER, System.currentTimeMillis(), json.getBytes());
        writeDatagram(datagram);
    }

    /**
     * 响应加密参数需求
     * @param parameterList 加密参数
     */
    private void response(List<CryptoParameter> parameterList) {
        log.debug("加密参数已相应，参数如下\n" + parameterList+"\n"+lEnd);
        String json = JSON.toJSONString(parameterList);
        Datagram datagram = new Datagram(Datagram.ACTION_SEND_CRYPTO_PARAMETER, System.currentTimeMillis(), json.getBytes());
        writeDatagram(datagram);
    }
}
