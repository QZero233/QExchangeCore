package com.qzero.exchange.core.coder;

import com.qzero.exchange.core.QExchangeAction;

import java.io.*;

/**
 * 将一个动作序列化，便于传输
 */
public class ActionSerializer {

    public byte[] serializeAction(QExchangeAction action) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(action);
        return byteArrayOutputStream.toByteArray();
    }

    public QExchangeAction deserialize(byte[] serializedAction) throws Exception {
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(serializedAction);
        ObjectInputStream objectInputStream=new ObjectInputStream(byteArrayInputStream);

        QExchangeAction action= (QExchangeAction) objectInputStream.readObject();
        return action;
    }

}
