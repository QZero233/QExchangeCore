package com.qzero.exchange.core.io;

public interface IQExchangeIOSource {

    /**
     * The status of the source
     */
    enum IOSourceStatus{
        /**
         * Which means it allow you to write and read
         */
        STATUS_OPEN,
        /**
         * Which means read and write is disabled
         */
        STATUS_CLOSED
    }

    /**
     * Read a datagram from source
     * @return The datagram,if read failed,it'll return null
     */
    Datagram readDatagram();

    /**
     * Write a datagram into source
     * @param datagram The datagram you want to write
     * @return Whether wrote successfully or not
     */
    boolean writeDatagram(Datagram datagram);

    /**
     * Get the status of the source
     * @return The status of the source
     */
    IOSourceStatus getSourceStatus();

    /**
     * Init source
     * @return
     */
    boolean initSource();

}
