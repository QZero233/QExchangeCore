package com.qzero.exchange.core.coder;

import com.alibaba.fastjson.JSON;

public class JSONCoder implements IQExchangeCoder {

    @Override
    public <T> T decode(byte[] in, Class<T> clazz) {
        return JSON.parseObject(new String(in),clazz);
    }

    @Override
    public byte[] encode(Object bean) {
        return JSON.toJSONString(bean).getBytes();
    }
}
