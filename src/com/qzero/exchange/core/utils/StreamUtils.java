package com.qzero.exchange.core.utils;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * 流工具类
 */
public class StreamUtils {

    /**
     * 从输入流中读取数据，直到无数据可读
     * @param inputStream 输入流
     * @return 读取到的数据
     * @throws IOException 如果过程中发生IO异常将会抛出
     */
    public static byte[] readDataFromInputStream(InputStream inputStream) throws IOException {
        byte[] buf=new byte[1024];
        int len;
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        while((len=inputStream.read(buf))!=-1){
            outputStream.write(buf,0,len);
        }

        return outputStream.toByteArray();
    }

    /**
     * 向输入流中写入数据
     * @param outputStream 输入流
     * @param data 待写入的数据
     * @throws IOException 若过程中发生IO异常会抛出
     */
    public static void writeDataToOutputStream(OutputStream outputStream,byte[] data) throws IOException{
        outputStream.write(data);
    }

    /**
     * 读取一个文件内容
     * @param file 文件
     * @return 读取到的数据
     * @throws IOException 若过程中发生IO异常会抛出
     */
    public static byte[] readFile(File file) throws IOException {
        InputStream inputStream=new FileInputStream(file);
        byte[] result= readDataFromInputStream(inputStream);
        inputStream.close();
        return result;
    }

    /**
     * 将数据写入文件中
     * @param file 文件
     * @param data 数据
     * @throws IOException 若过程中发生IO异常会抛出
     */
    public static void writeFile(File file,byte[] data) throws IOException{
        OutputStream outputStream=new FileOutputStream(file);
        writeDataToOutputStream(outputStream,data);
        outputStream.close();
    }

    /**
     * 将一个长度为8字节的byte数组转为一个long类型的数字
     * @param buf byte数组
     * @return long类型的数字
     * @throws IllegalArgumentException 如果数组长度不为8会抛出
     */
    public static long byteArrayToLong(byte[] buf) {
        if(buf.length!=8)
            throw new IllegalArgumentException("错误，不能将一个长度不为8的数组转为long");
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        return buffer.getLong();
    }

    /**
     * 将一个long类型的数字转为一个8字节的数组
     * @param l long类型的数字
     * @return 8字节的数组
     */
    public static byte[] longToByteArray(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(l);
        return buffer.array();
    }

    /**
     * 将一个长度为4字节的byte数组转为一个int类型的数字
     * @param buf byte数组
     * @return int类型的数字
     * @throws IllegalArgumentException 如果数组长度不为4会抛出
     */
    public static int byteArrayToInt(byte[] buf) {
        if(buf.length!=4)
            throw new IllegalArgumentException("错误，不能将一个长度不为4的数组转为int");
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        return buffer.getInt();
    }

    /**
     * 将一个int类型的数字转为一个4字节的数组
     * @param i int类型的数字
     * @return 4字节的数组
     */
    public static byte[] intToByteArray(int i) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(i);
        return buffer.array();
    }

    /**
     * 从输入流中读取指定长度的数据
     * @param is 输入流
     * @param length 数据长度
     * @return 读取到的数据
     * @throws IOException 若过程中发生IO异常会抛出
     */
    public static byte[] readSpecifiedLengthDataFromInputStream(InputStream is, int length) throws IOException {
        if(length==0)
            return new byte[0];

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(length);
        byte[] buf = new byte[length];
        int len;
        while (true) {
            len = is.read(buf, 0, length);
            length -= len;
            outputStream.write(buf, 0, len);
            if (length == 0)
                break;
        }

        return outputStream.toByteArray();

    }

    /**
     * 从输入流中读取一个长度为4字节的数据并将其转为int类型返回
     * @param is 输入流
     * @return int类型的数字
     * @throws IOException 若过程中发生IO异常会抛出
     */
    public static int readIntWith4Bytes(InputStream is) throws IOException{
        byte[] buf=readSpecifiedLengthDataFromInputStream(is,4);
        return byteArrayToInt(buf);
    }

    /**
     * 从输入流中读取一个长度为8字节的数据并将其转为long类型返回
     * @param is 输入流
     * @return long类型的数字
     * @throws IOException 若过程中发生IO异常会抛出
     */
    public static long readLongWith8Bytes(InputStream is) throws IOException{
        byte[] buf=readSpecifiedLengthDataFromInputStream(is,8);
        return byteArrayToLong(buf);
    }

    /**
     * 将一个int类型数字变为一个4字节数组再写入输出流
     * @param os 输出流
     * @param i int类型的数字
     * @throws IOException 若过程中发生IO异常会抛出
     */
    public static void writeIntWith4Bytes(OutputStream os,int i) throws IOException{
        byte[] buf=intToByteArray(i);
        os.write(buf);
    }

    /**
     * 将一个long类型数字变为一个8字节数组再写入输出流
     * @param os 输出流
     * @param l long类型的数字
     * @throws IOException 若过程中发生IO异常会抛出
     */
    public static void writeLongWith8Bytes(OutputStream os,long l) throws IOException{
        byte[] buf=longToByteArray(l);
        os.write(buf);
    }
}
