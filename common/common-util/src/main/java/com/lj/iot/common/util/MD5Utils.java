package com.lj.iot.common.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5通用类
 *
 * @author 浩令天下
 * @version 1.0.0_1
 * @since 2017.04.15
 */
public class MD5Utils {

    public static String DEFAULT_KEY = "adszxcvaf";

    public static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final char[] HEX_DIGITS = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

    /**
     * 通用MD5加密
     *
     * @param content
     * @return
     */
    public static String standardSign(String content) {
        if (content == null) {
            return null;
        }

        try {
            return standardSign(content.getBytes(DEFAULT_CHARSET));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String standardSign(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(input);
        byte[] buffer = messageDigest.digest();
        return bytesToStr(buffer);
    }

    private static String bytesToStr(byte[] buffer) {
        char[] resultBuffer = new char[32];
        int i = 0;
        int j = 0;
        while (true) {
            if (i >= 16)
                return new String(resultBuffer);
            int k = buffer[i];
            int m = j + 1;
            resultBuffer[j] = HEX_DIGITS[(0xF & k >>> 4)];
            j = m + 1;
            resultBuffer[m] = HEX_DIGITS[(k & 0xF)];
            i++;
        }
    }

    /**
     * MD5方法
     *
     * @param text 明文
     * @param key  密钥
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text, String key) {
        //加密后的字符串,经过两次混淆加密
        String encodeStr =standardSign(text + key);
        encodeStr = standardSign(key + encodeStr);
        return encodeStr;
    }

    public static String md5(String text) {
        return md5(text, DEFAULT_KEY);
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param md5  密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String md5) {
        //根据传入的密钥进行验证
        return verify(text, DEFAULT_KEY, md5);
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param key  密钥
     * @param md5  密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String key, String md5) {
        //根据传入的密钥进行验证
        String md5Text = md5(text, key);
        if (md5Text.equalsIgnoreCase(md5)) {
            System.out.println("MD5验证通过");
            return true;
        }

        return false;
    }

    public static void main(String[] args) throws Exception {
        String a="你妹的啊，什么鬼";
        System.out.println(MD5Utils.verify("a","v","c"));
    }
}