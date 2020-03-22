package com.qzero.exchange.core.io.crypto;

import java.util.List;

/**
 * 进行加解密操作的接口
 */
public interface IQExchangeCryptoModule {

    /**
     * 加密数据
     * @param in 待加密的数据
     * @return 已加密的数据，失败返回null
     */
    byte[] encrypt(byte[] in);

    /**
     * 解密数据
     * @param in 待解密的数据
     * @return 已解密的数据，失败返回null
     */
    byte[] decrypt(byte[] in);

    /**
     * 获取我方已有的加密参数，用于响应对方发送的请求
     * @param name 加密参数的名称
     * @return 加密参数，如果不存在会返回null
     */
    byte[] getParameter(String name);

    /**
     * 填充对方返回的加密参数
     * @param name 加密参数名称
     * @param parameter 参数
     */
    void fillParameter(String name, byte[] parameter);

    /**
     * 获取我方还需要的加密参数列表
     * @return 加密参数列表，如果没有需要的了会返回null
     */
    List<String> getNeededParametersList();

}
