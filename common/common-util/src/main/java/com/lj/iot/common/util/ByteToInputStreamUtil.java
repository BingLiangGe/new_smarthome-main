package com.lj.iot.common.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 输入流与字节转换
 *
 * @author zrj
 * @since 2021/12/27
 **/
public class ByteToInputStreamUtil {
    /**
     * 字节转输入流
     *
     * @param buf
     * @return java.io.InputStream
     */
    public static InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    /**
     * 输入流转字节
     *
     * @param inStream
     * @return byte[]
     */
    public static byte[] input2byte(InputStream inStream) throws Exception {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }
}
