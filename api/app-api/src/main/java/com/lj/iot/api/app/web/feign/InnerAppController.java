package com.lj.iot.api.app.web.feign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.SceneJobTriggerDto;
import com.lj.iot.biz.base.dto.WatchMsgDto;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.*;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.feign.app.AppApiFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 内部接口场景相关
 */
@Slf4j
@RestController
public class InnerAppController implements AppApiFeignClient {

    @Autowired
    private BizWatchPublishService watchPublishService;

    @Resource
    BizSceneService bizSceneService;

    @Autowired
    private BizClockService bizClockService;

    @Autowired
    private BizUserDeviceScheduleService bizUserDeviceScheduleService;

    @Autowired
    private IUserAccountService userAccountService;
    @Autowired
    private BizUserAccountService bizUserAccountService;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private IUserClockService userClockService;

    @Autowired
    private IUserDeviceScheduleService userDeviceScheduleService;

    @Autowired
    private ISceneScheduleService sceneScheduleService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private BizWsPublishService bizWsPublishService;

    @Autowired
    private IApiConfigService apiConfigService;

    @Autowired
    private IOperationLogService operationLogService;

    ExecutorService executorService = Executors.newFixedThreadPool(1);


    @Override
    public CommonResultVo<String> sendTokenComment() {
        return bizUserAccountService.sendTokenComment();
    }

    @Override
    public CommonResultVo<String> removeOperationLog() {
        operationLogService.deleteOperationLogTask();
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> watchDeviceStatus() {
        List<String> deviceIds = userDeviceService.findWatchDeviceDownStatus();
        deviceIds.forEach(deviceId -> {

            userDeviceService.update(
                    UserDevice.builder()
                            .status(false)
                            .downTime(LocalDateTime.now())
                            .build(),
                    new QueryWrapper<>(UserDevice.builder()
                            .deviceId(deviceId)
                            .build()));
            log.info("手表设备下线->device_id={}", deviceId);
        });


        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> watchDeviceLocation() {

        List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .status(true)
                .productType("smart_watch").build()));

        list.forEach(userDevice -> {

            watchPublishService.publish(WatchMsgDto.builder()
                    .deviceId(userDevice.getDeviceId())
                    .data("DW*" + userDevice.getDeviceId() + "*0002*CR").build());
        });
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> masterDeviceStatus() {
        List<String> deviceIds = userDeviceService.findMasterDeviceDownStatus();
        deviceIds.forEach(deviceId -> {

            UserDevice userDevice = userDeviceService.getById(deviceId);

            userDeviceService.update(
                    UserDevice.builder()
                            .status(false)
                            .downTime(LocalDateTime.now())
                            .build(),
                    new QueryWrapper<>(UserDevice.builder()
                            .masterDeviceId(deviceId)
                            .build()));
            log.info("主控设备下线->device_id={}", deviceId);

            // 1.3旗云推送
            if ("1.3".equals(userDevice.getHardWareVersion())) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<UserDevice> masterDevice = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                                .masterDeviceId(deviceId).build()));
                        for (UserDevice device : masterDevice
                        ) {
                            JSONObject params = new JSONObject();
                            params.put("device_id", device.getDeviceId());
                            params.put("code", "0");

                            apiConfigService.sendApiConfigData(params, "/device/push/device/status");
                        }
                    }
                });
            }
        });
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo triggerDevice() {
        List<String> deviceIds = userDeviceService.findUserDeviceChirStatus();
        if (!deviceIds.isEmpty()) {
            userDeviceService.updateUserDeviceStatusBatch(deviceIds);
        }
        deviceIds.forEach(deviceId -> {
            UserDevice userDevice = userDeviceService.getById(deviceId);

            if (userDevice != null) {
                String[] deviceIdOfflines = {deviceId};
                bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_OFFLINE, userDevice.getHomeId(), deviceIdOfflines);
            }
            log.info("子设备下线->device_id={}", deviceId);
        });
        return CommonResultVo.SUCCESS();
    }

    /**
     * 触发场景
     *
     * @param sceneJobTriggerDto
     * @return
     */
    @Override
    public CommonResultVo trigger(@Valid SceneJobTriggerDto sceneJobTriggerDto) {
        log.info("InnerAppController.SceneJobTriggerDto{}", JSON.toJSONString(sceneJobTriggerDto));

        if (sceneService.getById(sceneJobTriggerDto.getSceneId()) == null) {
            return CommonResultVo.NOT_EXIST();
        }

        SceneSchedule sceneSchedule = sceneScheduleService.getById(sceneJobTriggerDto.getScheduleId());
        if (sceneSchedule != null && sceneSchedule.getEnable()) {
            bizSceneService.trigger(sceneJobTriggerDto.getSceneId(), OperationEnum.Q_S_C);
        }

        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> triggerClock(IdDto idDto) {
        UserClock userClock = userClockService.getById(idDto.getId());
        log.info("triggerClock:idDto={},userClock={}",idDto,userClock);
        if (userClock == null) {
            return CommonResultVo.NOT_EXIST();
        }
        bizClockService.trigger(userClock);
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> triggerSchedule(IdDto idDto) {
        UserDeviceSchedule userDeviceSchedule = userDeviceScheduleService.getById(idDto.getId());
        if (userDeviceSchedule == null) {
            return CommonResultVo.NOT_EXIST();
        }
        bizUserDeviceScheduleService.trigger(userDeviceSchedule);
        return CommonResultVo.SUCCESS();
    }

    @Override
    public CommonResultVo<String> subAccountExpires() {
        List<UserAccount> userAccountList = userAccountService.list(new QueryWrapper<>(UserAccount.builder()
                .type(AccountTypeEnum.HOTEL_SUB_TEMP.getCode())
                .build()).lt("expires", LocalDateTime.now()));

        for (UserAccount userAccount : userAccountList) {

            LoginUtils.logout(UserDto.builder()
                    .uId(userAccount.getId())
                    .account(userAccount.getMobile())
                    .build());

            bizUserAccountService.cancellation(userAccount.getId());
            LoginUtils.logout(UserDto.builder()
                    .account(userAccount.getMobile())
                    .uId(userAccount.getId())
                    .build());
        }

        return CommonResultVo.SUCCESS();
    }
}
