package com.lj.iot.api.job.job;

import com.lj.iot.feign.app.AppApiFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 主控状态离线定时任务
 */
@Slf4j
@Component
public class DeviceStatusJob {


    @Resource
    private AppApiFeignClient appApiFeignClient;

    @Scheduled(cron = "0/30 * * * * ?")
    public void execute() {
        log.info("主控设备状态任务={}", LocalDateTime.now());
        appApiFeignClient.masterDeviceStatus();
        log.info("主控设备状态任务结束={}", LocalDateTime.now());
    }
}
