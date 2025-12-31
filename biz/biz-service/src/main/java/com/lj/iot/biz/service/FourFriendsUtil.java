package com.lj.iot.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.lj.iot.common.util.OkHttpUtils;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class FourFriendsUtil {

    @Value("${api.url}")
    private String url;


    /**
     * 发送设备
     *
     * @param productId
     * @param deviceId
     * @param masterDeviceId
     * @param code
     */
    public  void sendDevice(String productId, String deviceId, String masterDeviceId, Integer code) {
        JSONObject paramJson = new JSONObject();

        paramJson.put("productId", productId);
        paramJson.put("deviceId", deviceId);
        paramJson.put("masterDeviceId", masterDeviceId);
        paramJson.put("code", code);

        CompletableFuture.runAsync(() -> sendDeviceDataAsync(paramJson))
                .exceptionally(ex -> {
                    log.error("推送Device出现异常", ex);
                    return null; // 忽略异常，不中断业务逻辑
                });
    }

    private  void sendDeviceDataAsync(JSONObject paramJson) {
        try {
            log.info("推送四个朋友绑定设备,paramJson={},url={}",paramJson,url);
            OkHttpUtils.postJson(url, paramJson);
            log.info("推送Device,参数={}", paramJson);
        } catch (IOException e) {
            log.error("推送三方内容失败，参数={},错误={}", paramJson,e);
            // 异常处理：可以记录日志、进行重试等
        }
    }
}
