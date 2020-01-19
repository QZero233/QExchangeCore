package com.qzero.exchange.core.coder;

/**
 * An interface for class which can encode object into bin data or decode bin data into object
 */
public interface IQExchangeCoder {

    /**
     * Decode a bin data into object
     * @param in The bin data
     * @param clazz The class of the object
     * @param <T> The type of the object
     * @return The object,if action failed,it'll return null
     */
    <T>T decode(byte[] in,Class<T> clazz);

    /**
     * Encode an object into bin data
     * @param bean The object
     * @return The bin data,if failed,it'll return null
     */
    byte[] encode(Object bean);

}
