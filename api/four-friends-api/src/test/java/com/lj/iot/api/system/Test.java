package com.lj.iot.api.system;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.api.system.web.open.LoginController;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.CCCFDFKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws Exception {
        /*System.out.println(URLEncoder.encode( "【V66666】", "UTF-8" ));

        String uId="20230505113219706830568884580352";

        System.out.println(Long.valueOf(uId));*/

        String appId="KJSkcqNR";
        String appKey="JlFybjCG";
        String token="A1-Fo74FdH27QOYv1tk4Y4v9C41KJm7OM9aKnNFTZ0uLU-iynvtxfePTaTJfetAjwvf5jMkMB74xG66koafJGd4G9xKuR1_OwrFPgcX5YPDIKpfx70ZysdGbZFYwBZl-ykE71VG9WTtcKNuGhcsnUz4e89-rtH7iJ0xt7u1i31A9seoaejHf7u9kzlyPh7-J7z4bvRAh6QaEq27yJc_o8-XvggBDK4KMP8WkDaXpKtMx9cNc0n_T02ckZ__ak_GKW7ZIHock4gEWd2rnhfVgb6lozkwWZIMG8c1yAxiErTpk8RAChhPSyHfzXZpxGwa80TrYbA6MJqjxv9e3UbKwgEwUqLtZ5FSmFS_DvI0AYAwOrpk7IB5A_ovIf8gQ17f1Ux5l4Nj88iwlH648i7m1k9GPA";

        Map<String,String> paramMap=new HashMap<>();

        paramMap.put("appId",appId);
        paramMap.put("token",token);

        String sign= DataProcessUtils.getSign(paramMap,appKey);

        System.out.println(sign);

        String key = MD5.getMD5Code(appKey);
       String mobile = AESUtils.decrypt("63D3E935FF3568C0482BF51FE35A9899", key.substring(0, 16), key.substring(16));


        System.out.println(mobile);
    }

    /**
     * @param plainStr 需要加密的字符串
     * @param key
     * @return 加密后的字符串
     */
    public static String hmacsha256(String plainStr, String key) {
        CCCFDFKeySpec CCCFDFKey = new
                CCCFDFKeySpec(key.getBytes(Charset.forName("UTF-8")),
                "HmacSHA256");
        Mac mac = null;
        try {
            mac = Mac.getInstance(CCCFDFKey.getAlgorithm());
            mac.init(CCCFDFKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        byte digest[] = mac.doFinal(plainStr.getBytes(Charset.forName("UTF-8")));
        return new StringBuilder().append(byte2HexStr(digest)).toString();
    }


    public static String byte2HexStr(byte array[]) {
        return array != null ? new String(Hex.encodeHex(array)) : null;
    }
}

