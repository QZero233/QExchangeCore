package com.qzero.exchange.core;

import com.qzero.exchange.core.coder.IQExchangeCoder;
import com.qzero.exchange.core.coder.JSONCoder;
import com.qzero.exchange.core.io.Datagram;
import com.qzero.exchange.core.io.IQExchangeIOSource;

/**
 * 读写对象的一个类
 */
public class QExchangeHelper {

    /**
     * io源
     */
    private IQExchangeIOSource ioSource;
    /**
     * 序列化器
     */
    private IQExchangeCoder coder;

    /**
     * 默认序列化器为JSON序列化器
     * @param ioSource
     */
    public QExchangeHelper(IQExchangeIOSource ioSource) {
        this.ioSource = ioSource;
        coder= new JSONCoder();
    }

    public QExchangeHelper(IQExchangeIOSource ioSource, IQExchangeCoder coder) {
        this.ioSource = ioSource;
        this.coder = coder;
    }


    /**
     * 向IO源中写入对象
     * @param packedObject 待写入的对象
     * @return 是否成功
     */
    public boolean writeObject(PackedObject packedObject){
        if(ioSource==null || ioSource.getSourceStatus()!= IQExchangeIOSource.IOSourceStatus.STATUS_OPEN || packedObject==null || packedObject.getObject()==null)
            return false;

        byte[] buf=coder.encode(packedObject);

        Datagram datagram=new Datagram(Datagram.ACTION_EXCHANGE_OBJECT,System.currentTimeMillis(),buf);
        return ioSource.writeDatagram(datagram);
    }


    /**
     * 从IO源中读取一个对象
     * @return 读取到的对象，失败会返回null
     */
    public PackedObject readObject(){
        if(ioSource==null || ioSource.getSourceStatus()!= IQExchangeIOSource.IOSourceStatus.STATUS_OPEN)
            return null;

        Datagram datagram=ioSource.readDatagram();
        if(datagram==null)
            return null;

        byte[] buf=datagram.getContent();
        return coder.decode(buf);
    }



}

