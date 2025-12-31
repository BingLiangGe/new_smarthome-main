package com.lj.iot.api.job.job;

import com.lj.iot.feign.app.AppApiFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;


/**
 * 手表定位指令
 */
@Slf4j
@Component
public class WatchLocationJob {

    @Resource
    private AppApiFeignClient appApiFeignClient;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void execute() {
        log.info("手表定位指令start={}", LocalDateTime.now());
        appApiFeignClient.watchDeviceLocation();
        log.info("手表定位指令end={}", LocalDateTime.now());
    }
}
