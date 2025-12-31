package com.lj.iot.api.job.job;

import com.lj.iot.feign.app.AppApiFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 操作记录定时任务
 */
@Slf4j
@Component
public class operationLogRemoveJob {

    @Resource
    private AppApiFeignClient appApiFeignClient;

    @Scheduled(cron = "0 0/30 * *  * ?")
    public void execute() {
        log.info("操作记录定时任务={}", LocalDateTime.now());
        //appApiFeignClient.removeOperationLog();
        log.info("操作记录定时任务={}", LocalDateTime.now());
    }
}