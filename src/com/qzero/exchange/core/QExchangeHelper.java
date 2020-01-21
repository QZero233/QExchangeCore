package com.qzero.exchange.core;

import com.qzero.exchange.core.coder.IQExchangeCoder;
import com.qzero.exchange.core.coder.JSONCoder;
import com.qzero.exchange.core.io.Datagram;
import com.qzero.exchange.core.io.IQExchangeIOSource;

public class QExchangeHelper {

    private IQExchangeIOSource ioSource;
    private IQExchangeCoder coder;

    public QExchangeHelper(IQExchangeIOSource ioSource) {
        this.ioSource = ioSource;
        coder= new JSONCoder();
    }

    public QExchangeHelper(IQExchangeIOSource ioSource, IQExchangeCoder coder) {
        this.ioSource = ioSource;
        this.coder = coder;
    }

    /**
     * Write an object into io source
     * @param bean The object
     * @return Whether the action succeeded or not
     */
    public boolean writeObject(Object bean){
        if(ioSource==null || ioSource.getSourceStatus()!= IQExchangeIOSource.IOSourceStatus.STATUS_OPEN || bean==null)
            return false;

        byte[] buf=coder.encode(bean);
        if(buf==null)
            return false;

        Datagram datagram=new Datagram(Datagram.ACTION_EXCHANGE_OBJECT,System.currentTimeMillis(),buf);
        return ioSource.writeDatagram(datagram);
    }

    /**
     * Read an object from io source
     * @return The object,if failed,return null
     */
    public Object readObject(){
        if(ioSource==null || ioSource.getSourceStatus()!= IQExchangeIOSource.IOSourceStatus.STATUS_OPEN)
            return null;

        Datagram datagram=ioSource.readDatagram();
        if(datagram==null)
            return null;

        byte[] buf=datagram.getContent();
        Object bean=coder.decode(buf);
        return bean;
    }



}
