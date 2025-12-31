package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.ScheduleParamDto;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserDeviceSchedule;
import com.lj.iot.biz.db.smart.service.IUserDeviceScheduleService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizUserDeviceScheduleService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.common.util.ValidUtils;
import com.lj.iot.fegin.job.JobFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BizUserDeviceScheduleServiceImpl implements BizUserDeviceScheduleService {

    @Autowired
    private IUserDeviceScheduleService userDeviceScheduleService;

    @Autowired
    private BizUserDeviceService bizUserDeviceService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private JobFeignClient jobFeignClient;


    @Override
    public void trigger(UserDeviceSchedule userDeviceSchedule) {

        UserDevice userDevice = userDeviceService.getById(userDeviceSchedule.getDeviceId());
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        bizUserDeviceService.handle(userDevice, userDeviceSchedule.getThingModel(), userDeviceSchedule.getKeyCode()
        , OperationEnum.APP_C);
    }

    @Override
    public List<UserDeviceSchedule> listByMasterDeviceId(String masterDeviceId) {
        return userDeviceScheduleService.list(new QueryWrapper<>(UserDeviceSchedule.builder()
                .masterDeviceId(masterDeviceId)
                .build()));
    }

    @Override
    public List<UserDeviceSchedule> listByDeviceId(String deviceId) {
        return userDeviceScheduleService.list(new QueryWrapper<>(UserDeviceSchedule.builder()
                .deviceId(deviceId)
                .build()));
    }

    @Override
    public void deleteByDeviceId(String deviceId) {

        //删除主控数据
        List<UserDeviceSchedule> userDeviceScheduleList = listByMasterDeviceId(deviceId);

        delete(userDeviceScheduleList);

        //删除特定设备
        userDeviceScheduleList = listByDeviceId(deviceId);

        delete(userDeviceScheduleList);
    }

    @Override
    public void delete(UserDeviceSchedule userDeviceSchedule) {
        userDeviceScheduleService.removeById(userDeviceSchedule.getId());
        jobFeignClient.deleteScheduleByScheduleId(ScheduleParamDto.builder()
                .scheduleId(userDeviceSchedule.getId())
                .deviceId(userDeviceSchedule.getDeviceId()).build());
    }

    @Override
    public void delete(List<UserDeviceSchedule> userDeviceScheduleList) {
        for (UserDeviceSchedule userDeviceSchedule : userDeviceScheduleList) {
            delete(userDeviceSchedule);
        }
    }

    @Override
    public void deleteByDeviceIdAndCron(String deviceId, String cron, String remark) {
        //删除主控数据
        List<UserDeviceSchedule> userDeviceScheduleList = userDeviceScheduleService
                .listByMasterDeviceIdAndCronOrRemark(deviceId, cron, remark);
        ValidUtils.listIsEmptyThrow(userDeviceScheduleList, "没有找到该时间点任务或调度");
        delete(userDeviceScheduleList);
    }
}
