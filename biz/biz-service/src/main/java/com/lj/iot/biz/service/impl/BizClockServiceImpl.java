package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.IdStrDto;
import com.lj.iot.biz.base.dto.ScheduleParamDto;
import com.lj.iot.biz.db.smart.entity.UserClock;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserClockService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizClockService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.util.ValidUtils;
import com.lj.iot.fegin.job.JobFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BizClockServiceImpl implements BizClockService {

    @Autowired
    private MqttPushService mqttPushService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IUserClockService userClockService;

    @Autowired
    private JobFeignClient jobFeignClient;


    @Override
    public void trigger(UserClock clock) {
        UserDevice master = userDeviceService.masterStatus(clock.getMasterDeviceId());
        mqttPushService.push(master, PubTopicEnum.PUB_TRIGGER_CLOCK, null);
    }

    @Override
    public void deleteByDeviceId(String deviceId) {
        userClockService.remove(new QueryWrapper<>(UserClock.builder()
                .masterDeviceId(deviceId)
                .build()));
        jobFeignClient.deleteClockByDeviceId(IdStrDto.builder().id(deviceId).build());
    }

    @Override
    public void deleteByDeviceIdAndCron(String deviceId, String cron, String remark) {
        List<UserClock> userClockList = userClockService.listByMasterDeviceIdAndCronOrRemark(deviceId, cron, remark);
        ValidUtils.listIsEmptyThrow(userClockList,"没有找到该闹钟或者提醒");
        delete(userClockList);
    }

    @Override
    public List<UserClock> listByMasterDeviceId(String masterDeviceId) {
        return userClockService.list(new QueryWrapper<>(UserClock.builder()
                .masterDeviceId(masterDeviceId)
                .build()));
    }

    @Override
    public void delete(UserClock userClock) {
        userClockService.removeById(userClock.getId());
        jobFeignClient.deleteClockByScheduleId(ScheduleParamDto.builder()
                .scheduleId(userClock.getId())
                .deviceId(userClock.getMasterDeviceId())
                .build());
    }

    @Override
    public void delete(List<UserClock> userClockList) {
        for (UserClock userClock : userClockList) {
            delete(userClock);
        }
    }
}
