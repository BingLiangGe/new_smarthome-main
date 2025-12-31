package com.lj.iot.common.aiui.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lj.iot.common.aiui.core.dto.UploadEntityDto;
import com.lj.iot.common.aiui.core.dto.UploadEntityItemDto;
import com.lj.iot.common.aiui.core.properties.AiuiProperties;
import com.lj.iot.common.aiui.core.service.IUploadEntityService;
import com.lj.iot.common.aiui.core.util.JavaMailUtils;
import com.lj.iot.common.aiui.core.util.NumberUtil;
import com.lj.iot.common.base.enums.DeviceEntityTypeEnum;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.RandomGeneratorUtils;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
@Slf4j
@Component
public class UploadEntityServiceImpl implements IUploadEntityService {

    @Autowired
    private AiuiProperties properties;

    private Integer limit = 3;


    @Override
    public Boolean uploadCustomLevelTrigger(UploadEntityDto uploadEntityDto) {

        // 四个朋友不进行上传操作
        if ("20230505113219706830568884580352".equals(uploadEntityDto.getUserId())) {
            return false;
        }
        return uploadTriggerLevel(uploadEntityDto, 1);
    }


    @Override
    public Boolean uploadCustomLevel(UploadEntityDto uploadEntityDto) {

        // 四个朋友不进行上传操作
        if ("20230505113219706830568884580352".equals(uploadEntityDto.getUserId())) {
            return false;
        }

        // 设备别名上传
        if ("dn".equals(uploadEntityDto.getDynamicEntitiesName())) {
            DeviceEntityTypeEnum titles[] = DeviceEntityTypeEnum.values();

            Map<String, String> paramMap = new HashMap<>();

            for (UploadEntityItemDto itemDto : uploadEntityDto.getEntityList()
            ) {

                if (StringUtil.isBlank(itemDto.getAlias())) {
                    continue;
                }

                boolean flag = false;
                for (DeviceEntityTypeEnum title : titles
                ) {
                    if (title.getCode().equals(itemDto.getProductType())) {
                        flag = true;
                        if (paramMap.get(title.getName()) == null) {
                            paramMap.put(title.getName(), itemDto.getAlias() + "|");
                        } else {
                            paramMap.put(title.getName(), paramMap.get(title.getName()) + itemDto.getAlias() + "|");
                        }
                    }
                }

                if (!flag) {

                    String newAlias = NumberUtil.outputArabNumberString(itemDto.getAlias());

                    if (!newAlias.equals(itemDto.getAlias())) {
                        paramMap.put(itemDto.getName(), newAlias + "|");
                    }
                    paramMap.put(itemDto.getName(), itemDto.getAlias());
                }
            }

            List<UploadEntityItemDto> entityList = Lists.newArrayList();

            for (String key : paramMap.keySet()
            ) {
                String value = paramMap.get(key);

                if (value.contains("|")) {
                    value = weightlessnessReduction(value);
                    value = value.substring(0, value.length() - 1);
                }
                entityList.add(UploadEntityItemDto.builder()
                        .name(key)
                        .alias(value).build());
            }
            uploadEntityDto.setEntityList(entityList);
        }
        return uploadCustomLevel(uploadEntityDto, 1);
    }

    public String weightlessnessReduction(String values) {
        String newValue = "";
        List<String> newItems = Arrays.asList(values.split("\\|"));
        newItems = newItems.stream().distinct().collect(Collectors.toList());

        for (String value : newItems
        ) {
            String newAlias = NumberUtil.outputArabNumberString(value);

            if (!newAlias.equals(value)) {
                newValue += newAlias + "|";
            }
            newValue += value + "|";
        }
        return newValue;
    }

    public Boolean uploadTriggerLevel(UploadEntityDto uploadEntityDto, Integer countTry) {
        if (countTry > limit) {
            return false;
        }

        try {
            //修改实体传输的post方法，之前问号后面接的方式会出现header过长的问题
            String result = httpPost(properties.getUploadUrl(), buildHeader(), buildCustomTrigger(uploadEntityDto));
            log.info("UploadEntityServiceImpl.uploadCustomLevel:{}，{}", JSON.toJSONString(uploadEntityDto), result);
            log.info("UploadEntityServiceImpl.uploadCustomLevel-BASE64:{}", buildCustomTrigger(uploadEntityDto));
            JSONObject uploadJo = JSON.parseObject(result);
            String code = uploadJo.getString("code");

            // 上传成功,进入检查操作
            if ("00000".equals(code)) {
                check(uploadJo.getJSONObject("data").getString("sid"));
            }
        } catch (Exception e) {
            log.error("UploadEntityServiceImpl.uploadCustomLevel", e);

            log.error("UploadEntityServiceImpl.uploadCustomLevel:{}", JSON.toJSONString(uploadEntityDto));
        }
        return false;
    }

    public Boolean uploadCustomLevel(UploadEntityDto uploadEntityDto, Integer countTry) {
        if (countTry > limit) {
            return false;
        }

        try {
            //修改实体传输的post方法，之前问号后面接的方式会出现header过长的问题
            String result = httpPost(properties.getUploadUrl(), buildHeader(), buildCustomUploadBody(uploadEntityDto));
            log.info("UploadEntityServiceImpl.uploadCustomLevel:{}，{}", JSON.toJSONString(uploadEntityDto), result);
            log.info("UploadEntityServiceImpl.uploadCustomLevel-BASE64:{}", buildCustomUploadBody(uploadEntityDto));
            JSONObject uploadJo = JSON.parseObject(result);
            String code = uploadJo.getString("code");

            // 上传成功,进入检查操作
            if ("00000".equals(code)) {
                check(uploadJo.getJSONObject("data").getString("sid"));
            }
        } catch (Exception e) {
            log.error("UploadEntityServiceImpl.uploadCustomLevel", e);

            log.error("UploadEntityServiceImpl.uploadCustomLevel:{}", JSON.toJSONString(uploadEntityDto));
        }
        return false;
    }

    @Override
    public Boolean uploadUserLevel(UploadEntityDto uploadEntityDto) {
        return uploadUserLevel(uploadEntityDto, 1);
    }

    public Boolean uploadUserLevel(UploadEntityDto uploadEntityDto, Integer countTry) {
        if (countTry > limit) {
            return false;
        }
        try {
            //修改实体传输的post方法，之前问号后面接的方式会出现header过长的问题
            String result = httpPost(properties.getUploadUrl(), buildHeader(), buildCustomUploadBody(uploadEntityDto));
            log.info("UploadEntityServiceImpl.uploadUserLevel:{}，{}", JSON.toJSONString(uploadEntityDto), result);
            JSONObject uploadJo = JSON.parseObject(result);
            String code = uploadJo.getString("code");
            if ("00000".equals(code)) {
                if (!check(uploadJo.getJSONObject("data").getString("sid"))) {
                    return uploadUserLevel(uploadEntityDto, ++countTry);
                }
            }
        } catch (Exception e) {
            log.error("UploadEntityServiceImpl.uploadUserLevel", e);

            log.error("UploadEntityServiceImpl.uploadUserLevel:{}", JSON.toJSONString(uploadEntityDto));
        }
        return false;
    }

    @Override
    public Boolean uploadAppLevel(UploadEntityDto uploadEntityDto) {
        return uploadAppLevel(uploadEntityDto, 1);
    }

    public Boolean uploadAppLevel(UploadEntityDto uploadEntityDto, Integer countTry) {
        if (countTry > limit) {
            return false;
        }
        try {
            //修改实体传输的post方法，之前问号后面接的方式会出现header过长的问题
            String result = httpPost(properties.getUploadUrl(), buildHeader(), buildCustomUploadBody(uploadEntityDto));
            log.info("UploadEntityServiceImpl.uploadAppLevel:{}，{}", JSON.toJSONString(uploadEntityDto), result);
            JSONObject uploadJo = JSON.parseObject(result);
            String code = uploadJo.getString("code");
            if ("00000".equals(code)) {
                if (!checkReset(uploadJo.getJSONObject("data").getString("sid"), uploadEntityDto)) {
                    return uploadAppLevel(uploadEntityDto, ++countTry);
                }
            }
        } catch (Exception e) {
            log.error("UploadEntityServiceImpl.uploadAppLevel:{}", JSON.toJSONString(uploadEntityDto));
        }
        return false;
    }


    public Boolean checkReset(String sid, UploadEntityDto uploadEntityDto) {
        try {
            //修改实体传输的post方法，之前问号后面接的方式会出现header过长的问题
            String result = httpPost(properties.getCheckUrl(), buildHeader(), buildCheckBody(sid));
            log.info("UploadEntityServiceImpl.check:{}", result);
            JSONObject uploadJo = JSON.parseObject(result);
            String code = uploadJo.getString("code");
            if ("00000".equals(code)) {
                return true;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        log.info("进入上传实体失败线程1===>");
                        for (int i = 0; i < 3; i++) {
                            // aiui 要求休眠十秒
                            Thread.sleep(1000 * 10);
                            String result = httpPost(properties.getCheckUrl(), buildHeader(), buildCheckBody(sid));
                            log.error("进入上传实体失败线程_UploadEntityServiceImpl.check1:{},sid={}", result, sid);
                            JSONObject uploadJo = JSON.parseObject(result);
                            String code = uploadJo.getString("code");
                            if ("00000".equals(code)) {
                                return;
                            }
                        }

                        JavaMailUtils.sendMial(result, "AIUI实体上传失败,sid=" + sid);

                        //修改实体传输的post方法，之前问号后面接的方式会出现header过长的问题
                        String result = httpPost(properties.getUploadUrl(), buildHeader(), buildCustomUploadBody(uploadEntityDto));
                        log.info("UploadEntityServiceImpl.uploadAppLevel重新上传:{}，{}", JSON.toJSONString(uploadEntityDto), result);
                        JSONObject uploadJo = JSON.parseObject(result);
                        String code = uploadJo.getString("code");
                        if ("00000".equals(code)) {
                            check(uploadJo.getJSONObject("data").getString("sid"));
                        }


                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        } catch (Exception e) {
            log.error("UploadEntityServiceImpl.check:{}", sid);
        }
        return false;
    }

    @Override
    public Boolean check(String sid) {
        try {
            //修改实体传输的post方法，之前问号后面接的方式会出现header过长的问题
            String result = httpPost(properties.getCheckUrl(), buildHeader(), buildCheckBody(sid));
            log.info("UploadEntityServiceImpl.check:{}", result);
            JSONObject uploadJo = JSON.parseObject(result);
            String code = uploadJo.getString("code");
            if ("00000".equals(code)) {
                return true;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        for (int i = 0; i < 3; i++) {
                            // aiui 要求休眠十秒
                            Thread.sleep(1000 * 10);
                            String result = httpPost(properties.getCheckUrl(), buildHeader(), buildCheckBody(sid));
                            log.error("进入上传实体失败线程_UploadEntityServiceImpl.check2:{},sid={}", result, sid);
                            JSONObject uploadJo = JSON.parseObject(result);
                            String code = uploadJo.getString("code");
                            if ("00000".equals(code)) {
                                return;
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        } catch (Exception e) {
            log.error("UploadEntityServiceImpl.check1:{}", sid);
        }
        return false;
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

    private static String buildCheckBody(String sid) throws UnsupportedEncodingException {
        String body = "sid=" + URLEncoder.encode(sid, "utf-8");
        return body;
    }

    private String buildAppUploadBody(UploadEntityDto uploadEntityDto) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("appid", properties.getAppId());
        map.put("res_name", properties.getNamespace() + "." + uploadEntityDto.getDynamicEntitiesName() + "_app");
        map.put("pers_param", JSON.toJSONString(Map.of("appid", properties.getAppId())));

        StringBuilder stringBuilder = new StringBuilder();
        String data = "";
        if (uploadEntityDto.getEntityList() != null && uploadEntityDto.getEntityList().size() > 0) {
            for (UploadEntityItemDto uploadEntityItemDto : uploadEntityDto.getEntityList()) {
                stringBuilder.append("\r\n");
                stringBuilder.append(JSON.toJSONString(uploadEntityItemDto));
            }
            data = stringBuilder.substring(2);
        }

        /**
         * 每条数据之间用换行符隔开
         * String data = "{\"name\":\"空调\",\"alias\":\"控跳|孔挑\",\"extra\":\"xxxxx\"}" + "\r\n"
         *                + "{\"name\":\"风扇\",\"alias\":\"电风扇\",\"extra\":\"xxxxx\"}";
         */
        StringBuilder body = new StringBuilder();
        map.put("data", new String(Base64.encodeBase64(data.getBytes(StandardCharsets.UTF_8))));
        for (String key : map.keySet()) {
            body.append("&")
                    .append(key)
                    .append("=")
                    .append(URLEncoder.encode(map.get(key), StandardCharsets.UTF_8));
        }
        return body.substring(1);
    }

    private String buildCustomTrigger(UploadEntityDto uploadEntityDto) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("appid", properties.getAppId());


        map.put("res_name", properties.getNamespace() + "." + uploadEntityDto.getDynamicEntitiesName() + "_user");
        map.put("pers_param", JSON.toJSONString(Map.of("custom_id", uploadEntityDto.getUserId())));

        String data = "";
        if (uploadEntityDto.getEntityList() != null && uploadEntityDto.getEntityList().size() > 0) {
            for (UploadEntityItemDto uploadEntityItemDto : uploadEntityDto.getEntityList()) {
                data += "{\"name\":\"" + uploadEntityItemDto.getName() + "\"}\n";
            }
        }


        /**
         * 每条数据之间用换行符隔开
         * String data = "{\"name\":\"空调\",\"alias\":\"控跳|孔挑\",\"extra\":\"xxxxx\"}" + "\r\n"
         *                + "{\"name\":\"风扇\",\"alias\":\"电风扇\",\"extra\":\"xxxxx\"}";
         */
        StringBuilder body = new StringBuilder();
        map.put("data", new String(Base64.encodeBase64(data.getBytes(StandardCharsets.UTF_8))));
        log.info("buildCustomUploadBody_BASE64={}", map.get("data"));
        log.info("buildCustomUploadBody={}", data);
        for (String key : map.keySet()) {
            body.append("&")
                    .append(key)
                    .append("=")
                    .append(URLEncoder.encode(map.get(key), StandardCharsets.UTF_8));
        }
        return body.substring(1);
    }

    private String buildCustomUploadBody(UploadEntityDto uploadEntityDto) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("appid", properties.getAppId());


        map.put("res_name", properties.getNamespace() + "." + uploadEntityDto.getDynamicEntitiesName() + "_user");
        map.put("pers_param", JSON.toJSONString(Map.of("custom_id", uploadEntityDto.getUserId())));

        StringBuilder stringBuilder = new StringBuilder();
        String data = "";
        if (uploadEntityDto.getEntityList() != null && uploadEntityDto.getEntityList().size() > 0) {
            for (UploadEntityItemDto uploadEntityItemDto : uploadEntityDto.getEntityList()) {
                stringBuilder.append("\r\n");
                stringBuilder.append(JSON.toJSONString(uploadEntityItemDto));
            }
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
        return body.substring(1);
    }

    private String buildUserUploadBody(UploadEntityDto uploadEntityDto) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("appid", properties.getAppId());
        if (uploadEntityDto.getResName() != null) {
            map.put("res_name", uploadEntityDto.getResName());
        } else {
            map.put("res_name", properties.getNamespace() + "." + uploadEntityDto.getDynamicEntitiesName() + "_user");
        }
        map.put("pers_param", JSON.toJSONString(Map.of("auth_id", uploadEntityDto.getUserId())));

        StringBuilder stringBuilder = new StringBuilder();
        String data = "";
        if (uploadEntityDto.getEntityList() != null && uploadEntityDto.getEntityList().size() > 0) {
            for (UploadEntityItemDto uploadEntityItemDto : uploadEntityDto.getEntityList()) {
                stringBuilder.append("\r\n");
                stringBuilder.append(JSON.toJSONString(uploadEntityItemDto));
            }
            data = stringBuilder.substring(2);
        }


        /**
         * 每条数据之间用换行符隔开
         * String data = "{\"name\":\"空调\",\"alias\":\"控跳|孔挑\",\"extra\":\"xxxxx\"}" + "\r\n"
         *                + "{\"name\":\"风扇\",\"alias\":\"电风扇\",\"extra\":\"xxxxx\"}";
         */
        StringBuilder body = new StringBuilder();
        map.put("data", new String(Base64.encodeBase64(data.getBytes(StandardCharsets.UTF_8))));
        for (String key : map.keySet()) {
            body.append("&")
                    .append(key)
                    .append("=")
                    .append(URLEncoder.encode(map.get(key), StandardCharsets.UTF_8));
        }
        return body.substring(1);
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


    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        JSON.toJSONString(Map.of("custom_id", ""));
        // String a = "{\"name\":\"wl\",\"location\":{\"room\":\"客厅\"},\"did\":\"light111\",\"device\":\"light\",\"alias\":\"大白\"}";
        // JSONObject jsonObject = JSON.parseObject(a);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "wl");
        jsonObject.put("alias", "物理书");
        jsonArray.add(jsonObject);
        String data = "";

        for (Object o : jsonArray) {
            stringBuilder.append("\r\n");
            stringBuilder.append(JSON.toJSONString(o));
        }

        data = stringBuilder.substring(2);
        System.out.println(data);
        data = new String(Base64.encodeBase64(data.getBytes(StandardCharsets.UTF_8)));
        System.out.println(data);


        String nonce = RandomGeneratorUtils.getCode(4);
        String curTime = System.currentTimeMillis() / 1000L + "";
        String checkSum = DigestUtils.md5Hex("0b7a6143717b4d5bb74aa89d821d07a1" + nonce + curTime);
        System.out.println(nonce);
        System.out.println(curTime);
        System.out.println(checkSum);
        Headers.Builder builder = new Headers.Builder();

        String sid = "psn1bd3f385@dx000116c50e5aa15b01";
        try {
            String body = URLEncoder.encode(sid, "utf-8");
            System.out.println(body);
        } catch (Exception e) {
        }
    }
}
