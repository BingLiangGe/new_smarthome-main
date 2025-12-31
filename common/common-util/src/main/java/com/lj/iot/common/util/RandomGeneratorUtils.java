package com.lj.iot.common.util;

import com.lj.iot.common.base.constant.CodeConstant;
import com.lj.iot.common.base.exception.CommonException;

import java.math.BigDecimal;
import java.util.Random;

/**
 * 随机码生成工具类
 */
public class RandomGeneratorUtils {
    private static final char[] pool = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    /**
     * 默认生成6位随机码
     *
     * @return
     */
    public static String getCode() {
        Random random = new Random();
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(10);
            buff.append(pool[index]);
        }
        return buff.toString().toUpperCase();
    }

    /**
     * 根据长度生成随机码
     *
     * @param length
     * @return
     */
    public static String getCode(int length) {
        if (length < 1) {
            throw CommonException.INSTANCE(CodeConstant.FAILURE, "随机验证码长度必须大于零 ！");
        }
        Random random = new Random();
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(10);
            buff.append(pool[index]);
        }
        return buff.toString().toUpperCase();
    }

    /**
     * 随机生成验证码（数字+字母）
     *
     * @param len 邀请码长度
     * @return
     * @author yongheng
     * @date 2019年03月11日 上午9:27:09
     */
    public static String generateRandomStr(int len) {
        //字符源，可以根据需要删减
        String generateSource = "23456789abcdefghgklmnpqrstuvwxyz";//去掉1和i ，0和o
        StringBuilder rtnStr = new StringBuilder();
        for (int i = 0; i < len; i++) {
            //循环随机获得当次字符，并移走选出的字符
            String nowStr = String.valueOf(generateSource.charAt((int) Math.floor(Math.random() * generateSource.length())));
            rtnStr.append(nowStr);
            generateSource = generateSource.replaceAll(nowStr, "");
        }
        return rtnStr.toString();
    }

    /**
     * 生成大于0的随机金额
     */
    public static BigDecimal randomBigDecimal(double base, int scale) {

        BigDecimal result = BigDecimal.ZERO;
        if (base == 0) {
            return result;
        }
        double number;
        BigDecimal amount;
        while (result.compareTo(BigDecimal.ZERO) <= 0) {
            number = Math.random() * base;
            amount = new BigDecimal(number);
            result = amount.setScale(scale, BigDecimal.ROUND_UP);
        }
        return result;
    }

    /**
     * 生成 0 到 bound-1   的随机数
     *
     * @param bound
     * @return
     */
    public static int random(int bound) {
        Random random = new Random();
        return random.nextInt(bound);
    }

}
