package com.lj.iot.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

public class TokenDownload {

    public static String getTokens(String mac,String kfid){
        String pseed=getRandomString(3) + "kfid" + mac.substring(mac.length()-10) + kfid;
        String pseed1=getMD5String(pseed).substring(8, 24);//随机串MD4加密
        String pseed2=getRandomString(13);
        String tokens=pseed2.substring(0,7)+pseed1.substring(6,9)+pseed.charAt(2)+pseed1.substring(1,5)+pseed1.charAt(0)+pseed1.substring(13)+pseed.charAt(1)+pseed1.charAt(5)+pseed.charAt(0)+pseed1.substring(9,13)+pseed2.substring(7);
        return tokens;
    }
    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String getMD5String(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
