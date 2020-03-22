package com.qzero.exchange.core.coder;

import com.qzero.exchange.core.PackedObject;

/**
 * 一个将PackedObject序列化以及反序列化的接口
 */
public interface IQExchangeCoder {

    /**
     * 反序列化对象
     * @param in 序列化的数据
     * @return 反序列化后的对象，如果失败会返回null
     */
     PackedObject decode(byte[] in);

    /**
     * 序列化对象
     * @param packedObject 需要序列化的对象
     * @return 序列化后的数据，如果失败返回null
     */
    byte[] encode(PackedObject packedObject);


}
