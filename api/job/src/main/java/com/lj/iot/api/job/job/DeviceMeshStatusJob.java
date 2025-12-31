package com.lj.iot.api.job.job;

import com.lj.iot.biz.base.vo.UserDeviceVo;
import com.lj.iot.feign.app.AppApiFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 蓝牙设备状态任务
 *
 * @author tyj
 * @date 2023-6-27 18:26:05
 */
@Slf4j
@Component
public class DeviceMeshStatusJob {


    @Resource
    private AppApiFeignClient appApiFeignClient;

    /*@Scheduled(cron = "0/15 * * * * ?")
    public void execute() {
        log.info("蓝牙设备状态任务={}", LocalDateTime.now());
        //appApiFeignClient.triggerDevice();
        log.info("蓝牙设备状态任务结束={}", LocalDateTime.now());
    }*/
}