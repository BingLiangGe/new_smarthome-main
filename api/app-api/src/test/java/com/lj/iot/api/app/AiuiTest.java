package com.lj.iot.api.app;

import com.alibaba.fastjson.JSONObject;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.RandomGeneratorUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AiuiTest {

    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {

        String data ="20 00 00 00 00 0f 38 82 67 02 23 82 67 06 9a c1 23 28 C2 00 11 94 c3 00 20 80 7F 40 BF C2 00 99 84 c1 23 a2 c2 00 12 3d";

        String datas[]= data.split(" ");

        String newData="";
        for (String dataItem: datas
             ) {
            newData+=dataItem+",";
        }

        newData=newData.substring(0,newData.length()-1);

        System.out.println(newData);

      /*  Long begin = new Date().getTime();
        Thread.sleep(1000);
        System.out.println((new Date().getTime() - begin));*/
        /* String key = "OTA升级超时";
        String content = "OTA升级次数超时提醒:【" + 1234 + "】时间：【" + LocalDateTime.now().toString() + "】";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content", content);
        JSONObject params = new JSONObject();
        params.put("msgtype", "text");
        params.put("text", jsonObject);
        try {
            OkHttpUtils.postJson("https://oapi.dingtalk.com/robot/send?access_token=7dd69f0d564356a1ec14396e4acd6c3a62b51c1b3adffe6dc91a0238a1d147f0", params);
        } catch (Exception e) {
        }*/
    }

    private static String buildCheckBody(String sid) throws UnsupportedEncodingException {
        String body = "sid=" + URLEncoder.encode(sid, "utf-8");
        return body;
    }

    private static String httpPost(String url, Map<String, String> header, String body) {
        String result = "";
        BufferedReader in = null;
        OutputStreamWriter out = null;
        try {
            URL realUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            for (String key : header.keySet()) {
                connection.setRequestProperty(key, header.get(key));
            }
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            //connection.setConnectTimeout(20000);
            //connection.setReadTimeout(20000);
            try {
                out = new OutputStreamWriter(connection.getOutputStream());
                out.write(body);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Map<String, String> buildHeader() {
        String nonce = RandomGeneratorUtils.getCode(4);
        String curTime = System.currentTimeMillis() / 1000L + "";
        String checkSum = DigestUtils.md5Hex("d5c3e424d8274da0b6054f4fa0b311f7" + nonce + curTime);
        Map<String, String> builder = new HashMap<String, String>();
        builder.put("X-NameSpace", "OS8784213619");
        builder.put("X-Nonce", nonce);
        builder.put("X-CurTime", curTime);
        builder.put("X-CheckSum", checkSum);
        return builder;
    }
}
