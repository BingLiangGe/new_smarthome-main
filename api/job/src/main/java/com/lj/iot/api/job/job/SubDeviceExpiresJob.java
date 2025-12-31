package com.lj.iot.api.job.job;

import com.lj.iot.feign.app.AppApiFeignClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 酒店
 *
 * @author mz
 * @Date 2022/8/12
 * @since 1.0.0
 */
@Component
public class SubDeviceExpiresJob {

    @Resource
    private AppApiFeignClient appApiFeignClient;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void execute() {
        appApiFeignClient.subAccountExpires();
    }
}
