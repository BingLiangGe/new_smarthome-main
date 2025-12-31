package com.lj.iot.api.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.aiui.core.dto.UploadEntityItemDto;
import com.lj.iot.common.aiui.core.properties.AiuiProperties;
import com.lj.iot.common.util.RandomGeneratorUtils;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
public class UploadEntityTest {


    @Autowired
    private AiuiProperties properties;

    @Test
    public void test() throws UnsupportedEncodingException, InterruptedException {


        String userId = "20230601140939716654633157701632";

        String namespace = "IFLYTEK";

        String entityName = DynamicEntitiesNameEnum.IFlytek_DEVICE_NAME.getCode();

        UserDevice userDevice = UserDevice.builder()
                .deviceId("13e1da5adb03P0")
                .productType("light")
                .customName("张三丰灯").build();

        Map<String, String> map = new HashMap<String, String>();
        map.put("appid", properties.getAppId());
        map.put("pers_param", JSON.toJSONString(Map.of("auth_id", userId)));

        if (StringUtil.isNotEmpty(namespace)) {
            map.put("res_name", namespace + "." + entityName);
        }


        StringBuilder stringBuilder = new StringBuilder();
        String data = "";
        if (userDevice != null) {

            JSONObject respJson = new JSONObject();

            respJson.put("did", userDevice.getDeviceId());
            respJson.put("device", userDevice.getProductType());
            respJson.put("alias", userDevice.getCustomName());

            stringBuilder.append("\r\n");
            stringBuilder.append(JSON.toJSONString(respJson));

            data = stringBuilder.substring(2);
        }


        /**
         * 每条数据之间用换行符隔开
         * String data = "{\"name\":\"空调\",\"alias\":\"控跳|孔挑\",\"extra\":\"xxxxx\"}" + "\r\n"
         *                + "{\"name\":\"风扇\",\"alias\":\"电风扇\",\"extra\":\"xxxxx\"}";
         */
        StringBuilder body = new StringBuilder();
        map.put("data", new String(Base64.encodeBase64(data.getBytes(StandardCharsets.UTF_8))));
        log.info("buildCustomUploadBody_BASE64={}", map.get("data"));
        for (String key : map.keySet()) {
            body.append("&")
                    .append(key)
                    .append("=")
                    .append(URLEncoder.encode(map.get(key), StandardCharsets.UTF_8));
        }

        String result = httpPost(properties.getUploadUrl(), buildHeader(), body.substring(1));

        log.info("result={}", result);
        log.info("data={}", data);

        JSONObject uploadJo = JSON.parseObject(result);
        String code = uploadJo.getString("code");
        if ("00000".equals(code)) {
            //修改实体传输的post方法，之前问号后面接的方式会出现header过长的问题
            Thread.sleep(1000 *10);
            String statusResult = httpPost(properties.getCheckUrl(), buildHeader(), buildCheckBody(uploadJo.getJSONObject("data").getString("sid")));
            log.info("UploadEntityServiceImpl.statusResult:{}", statusResult);
        }
    }

    private static String buildCheckBody(String sid) throws UnsupportedEncodingException {
        String body = "sid=" + URLEncoder.encode(sid, "utf-8");
        return body;
    }

    private Map<String, String> buildHeader() {
        String nonce = RandomGeneratorUtils.getCode(4);
        String curTime = System.currentTimeMillis() / 1000L + "";
        String checkSum = DigestUtils.md5Hex(properties.getAccountKey() + nonce + curTime);
        Map<String, String> builder = new HashMap<String, String>();
        builder.put("X-NameSpace", properties.getNamespace());
        builder.put("X-Nonce", nonce);
        builder.put("X-CurTime", curTime);
        builder.put("X-CheckSum", checkSum);
        return builder;
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
}
