package com.lj.iot.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttpUtils {

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            .readTimeout(10000, TimeUnit.MILLISECONDS)
            .build();

    public static String get(String utl) throws IOException {
        return okHttpClient.newCall(new Request.Builder()
                        .url(utl)
                        .get()//默认就是GET请求，可以不写
                        .build())
                .execute()
                .body()
                .string();
    }

    public static String get(String utl, Headers headers) throws IOException {
        return okHttpClient.newCall(new Request.Builder()
                        .url(utl)
                        .get()//默认就是GET请求，可以不写
                        .headers(headers)
                        .build())
                .execute()
                .body()
                .string();
    }

    public static String get(String utl, Map<String, String> params) throws IOException {

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(utl)).newBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        return okHttpClient.newCall(new Request.Builder()
                        .url(urlBuilder.build())
                        .get()//默认就是GET请求，可以不写
                        .build())
                .execute()
                .body()
                .string();
    }

    public static String post(String url, Headers headers) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        return okHttpClient.newCall(new Request.Builder()
                        .url(url)
                        .post(builder.build())
                        .headers(headers)
                        .build())
                .execute()
                .body()
                .string();
    }

    public static String post(String url, Headers headers, Map<String, String> params) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        //MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        // RequestBody requestBody=  RequestBody.create(mediaType, JSON.toJSONString(params));
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        return okHttpClient.newCall(new Request.Builder()
                        .url(url)
                        .post(builder.build())
                        .headers(headers)
                        .build())
                .execute()
                .body()
                .string();
    }

    public static String post(String url, Map<String, String> params) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        //MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        // RequestBody requestBody=  RequestBody.create(mediaType, JSON.toJSONString(params));
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }


        String string = okHttpClient.newCall(new Request.Builder()
                        .url(url)
                        .post(builder.build())
                        .build())
                .execute()
                .body()
                .string();
        log.info("OkHttpUtils.post{}", JSON.toJSONString(url));
        log.info("OkHttpUtils.post{}", JSON.toJSONString(params) + "=======result:" + string);
        return string;
    }

    public static String postJson(String url, JSONObject params) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(mediaType, JSON.toJSONString(params));
        String string = okHttpClient.newCall(new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build())
                .execute()
                .body()
                .string();
        return string;
    }

    public static String postJson(String url, String json) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(mediaType, json);
        String string = okHttpClient.newCall(new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build())
                .execute()
                .body()
                .string();
        log.info("OkHttpUtils.post{}", JSON.toJSONString(url));
        log.info("OkHttpUtils.post{}", json + "=======result:" + string);
        return string;
    }

    /**
     * 将通知参数转化为字符串
     *
     * @param request
     * @return
     */
    public static String readData(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            StringBuilder result = new StringBuilder();
            br = request.getReader();
            for (String line; (line = br.readLine()) != null; ) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
