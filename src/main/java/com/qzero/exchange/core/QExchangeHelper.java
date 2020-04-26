package com.qzero.exchange.core;

import com.qzero.exchange.core.coder.ActionSerializer;
import com.qzero.exchange.core.io.Datagram;
import com.qzero.exchange.core.io.IQExchangeIOSource;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * 读写对象的一个类
 */
public class QExchangeHelper {

    private static final Logger log=Logger.getRootLogger();

    /**
     * io源
     */
    private IQExchangeIOSource ioSource;
    /**
     * 序列化器
     */
    private ActionSerializer serializer;


    public QExchangeHelper(IQExchangeIOSource ioSource) {
        this.ioSource = ioSource;
        serializer=new ActionSerializer();
    }

    /**
     * 向IO源中写入一个action
     * @param action 待写入的action
     * @return 是否成功
     */
    public boolean writeAction(QExchangeAction action){
        if(ioSource==null || ioSource.getSourceStatus()!= IQExchangeIOSource.IOSourceStatus.STATUS_OPEN || action==null)
            return false;

        byte[] buf;
        try {
            buf = serializer.serializeAction(action);
        } catch (IOException e) {
            log.error("错误，序列化action失败",e);
            return false;
        }

        Datagram datagram=new Datagram(Datagram.ACTION_EXCHANGE_ACTION,System.currentTimeMillis(),buf);
        return ioSource.writeDatagram(datagram);
    }


    /**
     * 从IO源中读取一个action
     * @return 读取到的action，失败会返回null
     */
    public QExchangeAction readAction(){
        if(ioSource==null || ioSource.getSourceStatus()!= IQExchangeIOSource.IOSourceStatus.STATUS_OPEN)
            return null;

        Datagram datagram=ioSource.readDatagram();
        if(datagram==null)
            return null;

        byte[] buf=datagram.getContent();
        try {
            return serializer.deserialize(buf);
        } catch (Exception e) {
            log.error("错误，反序列化action失败",e);
            return null;
        }
    }



}

