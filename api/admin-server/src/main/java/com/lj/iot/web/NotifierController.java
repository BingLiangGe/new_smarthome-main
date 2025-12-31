package com.lj.iot.web;

import com.lj.iot.server.DingDingMessageUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/offline")
public class NotifierController {
    private static final List<String> serviceNames = Arrays.asList("MQTT" , "MYSQL","SOCKET","EMQX");

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    /**
     * 服务器预警：钉钉通知
     * @param serviceName
     * @param address 微服务部署地址
     * @return
     */
    @GetMapping("dingding_notifier")
    public void edit(String serviceName, String address) throws InterruptedException {
        if(!serviceNames.contains(serviceName)){
            return;
        }
        Thread.sleep(5000l);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                DingDingMessageUtil.sendTextMessage(DingDingMessageUtil.getTextMessage(serviceName,address,"OFFLINE",null));
            }
        });
    }
}
