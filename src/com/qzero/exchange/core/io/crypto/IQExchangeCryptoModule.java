package com.qzero.exchange.core.io.crypto;

import java.util.List;

/**
 * An interface for class which can do crypto action
 */
public interface IQExchangeCryptoModule {

    /**
     * Encrypt data
     * @param in
     * @return Encrypted data,if action failed it'll return null
     */
    byte[] encrypt(byte[] in);

    /**
     * Decrypt data
     * @param in
     * @return Decrypted data,if action failed it'll return null
     */
    byte[] decrypt(byte[] in);

    /**
     * Get a parameter that remote asks for
     * @param name The name of the parameter
     * @return The parameter(may be null if it doesn't exist)
     */
    byte[] getParameter(String name);

    /**
     * Fill a parameter that remote send to us
     * @param name The name of the parameter
     * @param parameter The parameter
     */
    void fillParameter(String name,byte[] parameter);

    /**
     * Get a parameter list that we need
     * @return The list,if there isn't,it'll be null
     */
    List<String> getNeededParametersList();

}
