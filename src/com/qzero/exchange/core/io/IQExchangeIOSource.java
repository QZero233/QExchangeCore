package com.qzero.exchange.core.io;

public interface IQExchangeIOSource {

    /**
     * IO源的状态
     */
    enum IOSourceStatus{
        /**
         * 表示当前源开放，可以进行读写
         */
        STATUS_OPEN,
        /**
         * 表示当前源异常，不能进行读写
         */
        STATUS_CLOSED
    }

    /**
     * 初始化源
     * @return 是否成功
     */
    boolean initSource();

    /**
     * 从源中读一个数据包
     * @return 读到的数据包，失败返回null
     */
    Datagram readDatagram();

    /**
     * 向源中写入一个数据包
     * @param datagram 即将写入的数据包
     * @return 写入是否成功
     */
    boolean writeDatagram(Datagram datagram);

    /**
     * 获取当前源的状态
     * @return 当前源的状态，以枚举形式返回
     */
    IOSourceStatus getSourceStatus();
}
