//package com.lj.iot.biz.service.emqx;
//
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.lj.iot.biz.db.smart.entity.UserDevice;
//import com.lj.iot.biz.db.smart.service.IUserDeviceService;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.Credentials;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//
///**
// * emqx 操作
// *
// * @author tyj
// */
//@Slf4j
//@Component
//public class EmqxConnection {
//
//    @Value("${mqtt.server.userName}")
//    private String adminName;
//
//
//    @Value("${mqtt.server.userPwd}")
//    private String adminPwd;
//
//
//    @Value("${mqtt.client.host}")
//    private String host;
//
//
//    @Autowired
//    private IUserDeviceService userDeviceService;
//
//
//    /**
//     * 验证主控状态
//     *
//     * @return
//     */
//    public boolean checkDeviceStatus(UserDevice userDevice) {
//        try {
//
//            OkHttpClient client = new OkHttpClient();
//
//            String url = "http://" + host + ":18083/api/v5/clients/" + userDevice.getMasterDeviceId();
//            Request request = new Request.Builder()
//                    .url(url)
//                    .header("Content-Type", "application/json")
//                    .header("Authorization", Credentials.basic(adminName, adminPwd))
//                    .build();
//
//            Response response = client.newCall(request).execute();
//
//            log.info("验证主控状态,respJson={}", response);
//            JSONObject respJson = JSONObject.parseObject(response.body().string());
//
//            if (response.code() != 404) {
//
//                // 在线时不会有code返回
//                if (respJson.getString("code ") == null) {
//
//                    boolean status = respJson.getBoolean("connected");
//
//                    if (status) {
//                        return true;
//                    }
//                }
//
//                // 同步数据库状态
//                userDeviceService.update(UserDevice.builder()
//                        .deviceId(userDevice.getDeviceId())
//                        .status(false)
//                        .downTime(LocalDateTime.now()).build(), new QueryWrapper<>(UserDevice.builder()
//                        .masterDeviceId(userDevice.getMasterDeviceId())
//                        .build()));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//}
