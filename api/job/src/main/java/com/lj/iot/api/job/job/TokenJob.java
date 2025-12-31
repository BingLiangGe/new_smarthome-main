//package com.lj.iot.api.job.job;
//
//import com.lj.iot.common.util.OkHttpUtils;
//import com.lj.iot.feign.app.AppApiFeignClient;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.time.LocalDateTime;
//
//@Slf4j
//@Component
//public class TokenJob {
//
//
//    @Resource
//    private AppApiFeignClient appApiFeignClient;
//
//
//    @Scheduled(cron = "0 0/3 * * * ?")
//    public void execute() {
//        log.info("下发token={}", LocalDateTime.now());
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        log.info("下发token2={}", LocalDateTime.now());
//                        OkHttpUtils.get("http://localhost:7776/api/open/admin/sendTokenComment");
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }).start();
//    }
//}
