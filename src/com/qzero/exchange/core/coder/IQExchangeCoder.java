package com.qzero.exchange.core.coder;

/**
 * An interface for class which can encode object into bin data or decode bin data into object
 */
public interface IQExchangeCoder {

    /**
     * Decode a bin data into object
     * @param in The bin data
     * @return The object,if action failed,it'll return null
     */
    Object decode(byte[] in);

    /**
     * Encode an object into bin data
     * @param bean The object
     * @return The bin data,if failed,it'll return null
     */
    byte[] encode(Object bean);

}
