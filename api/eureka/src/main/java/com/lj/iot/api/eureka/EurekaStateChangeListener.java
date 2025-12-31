//package com.lj.iot.api.eureka;
//
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import com.lj.iot.common.util.OkHttpUtils;
//import com.netflix.appinfo.InstanceInfo;
//import org.springframework.cloud.netflix.eureka.server.event.*;
//import org.springframework.context.event.EventListener;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.LocalDateTime;
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class EurekaStateChangeListener {
//
//    private static Cache<String, String> cache = CacheBuilder.newBuilder()
//            .concurrencyLevel(1)
//            .expireAfterWrite(5, TimeUnit.SECONDS)
//            .initialCapacity(10)
//            .build();
//
//    //一个设备掉线会接到多个消息，这个地方需要做下去重操作
//    @EventListener
//    public void listen(EurekaInstanceCanceledEvent eurekaInstanceCanceledEvent) {
//        //服务断线事件
//        String serverId = eurekaInstanceCanceledEvent.getServerId();
//        if (!eurekaInstanceCanceledEvent.isReplication()) {
//            sendMsg(serverId);
//        }
//    }
//
//    private boolean sendMsg(String serverId) {
//        String key = "serverId:[" + serverId+"]";
//        if (cache.getIfPresent(serverId) != null) {
//            return true;
//        }
//        cache.put(serverId, "1");
//        String content = "服务器掉线提醒:【" + serverId+"】时间：【"+ LocalDateTime.now().toString()+"】";
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("content", content);
//        JSONObject params = new JSONObject();
//        params.put("msgtype", "text");
//        params.put("text", jsonObject);
//        try {
//            OkHttpUtils.postJson("https://oapi.dingtalk.com/robot/send?access_token=7dd69f0d564356a1ec14396e4acd6c3a62b51c1b3adffe6dc91a0238a1d147f0", params);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    @EventListener
//    public void listen(EurekaInstanceRegisteredEvent event) {
//        InstanceInfo instanceInfo = event.getInstanceInfo();
//        System.out.println(instanceInfo);
//    }
//
//    @EventListener
//    public void listen(EurekaInstanceRenewedEvent event) {
//        event.getAppName();
//        event.getServerId();
//    }
//
//    @EventListener
//    public void listen(EurekaRegistryAvailableEvent event) {
//
//    }
//
//    @EventListener
//    public void listen(EurekaServerStartedEvent event) {
//        //Server启动
//    }
//
//    public static void main(String[] args) {
//        RestTemplate restTemplate = new RestTemplate();
//
//        String content = "服务器掉线提醒:appName" + "--serverId:";
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("content", content);
//        JSONObject params = new JSONObject();
//        params.put("msgtype", "text");
//        params.put("text", jsonObject);
//
//        HttpHeaders requestHeaders = new HttpHeaders();
//        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//        //HttpEntity
//        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(params, requestHeaders);
//        restTemplate.postForObject("https://oapi.dingtalk.com/robot/send?access_token=4ba5395907362ce4ecad0510c8f2b69df04c9f1d6578e07cb4ea9b6c9aaed144", requestEntity, String.class);
//
//    }
//}
