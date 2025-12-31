package com.lj.iot.feign.app;

import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.SceneIdDto;
import com.lj.iot.biz.base.dto.SceneJobTriggerDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "app-api")
public interface AppApiFeignClient {

    @PostMapping(value = "/nner/app_api/device/removeOperationLog", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> sendTokenComment();


    @PostMapping(value = "inner/app_api/device/removeOperationLog", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> removeOperationLog();

    @PostMapping(value = "inner/app_api/device/masterDeviceStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> masterDeviceStatus();

    @PostMapping(value = "inner/app_api/device/watchDeviceStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> watchDeviceStatus();


    @PostMapping(value = "inner/app_api/device/watchDeviceLocation", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> watchDeviceLocation();



    @PostMapping(value = "inner/app_api/device/trigger", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo triggerDevice();

    /**
     * 执行场景
     *
     * @param sceneIdDto
     * @return
     */
    @PostMapping(value = "inner/app_api/scene/trigger", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> trigger(@RequestBody SceneJobTriggerDto sceneIdDto);

    /**
     * 执行闹钟
     *
     * @param
     * @return
     */
    @PostMapping(value = "inner/app_api/clock/trigger", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> triggerClock(@RequestBody IdDto idDto);

    /**
     * 执行设备调度
     *
     * @param
     * @return
     */
    @PostMapping(value = "inner/app_api/schedule/trigger", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> triggerSchedule(@RequestBody IdDto idDto);

    /**
     * 酒店账号过期
     *
     * @param
     * @return
     */
    @PostMapping(value = "inner/app_api/sub_account/expires", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> subAccountExpires();
}
