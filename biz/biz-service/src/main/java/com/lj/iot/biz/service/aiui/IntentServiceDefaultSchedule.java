package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.base.dto.HandleUserDeviceDto;
import com.lj.iot.biz.base.dto.ScheduleParamDto;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserDeviceSchedule;
import com.lj.iot.biz.db.smart.service.IUserDeviceScheduleService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.util.DatetimeSlotUtil;
import com.lj.iot.fegin.job.JobFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 调度
 */
@Component("intentService_schedule")
public class IntentServiceDefaultSchedule implements IntentService {

    @Autowired
    private JobFeignClient jobFeignClient;

    @Autowired
    private IUserDeviceScheduleService userDeviceScheduleService;

    @Autowired
    private IntentCommonHandler intentCommonHandler;

    /**
     * 插槽
     * <p>
     *
     * @param intentDto
     * @return
     */
    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {

        //把调度的名字去掉后缀“_schedule” 用来获取正常的设备控制信息
        intentDto.setIntentName(StringUtils.removeEnd(intentDto.getIntentName(), "_schedule"));

        List<HandleUserDeviceDto<UserDevice>> handleUserDeviceDtoList = intentCommonHandler.buildHandleData(masterUserDevice, intentDto);
        IntentDto.Slot datetimeSlot = intentDto.getSlots().get("datetime");
        String cron = DatetimeSlotUtil.hasNextTimeCron(datetimeSlot);
        for (HandleUserDeviceDto<UserDevice> userDeviceHandleUserDeviceDto : handleUserDeviceDtoList) {
            UserDeviceSchedule userDeviceSchedule = UserDeviceSchedule.builder()
                    .userId(masterUserDevice.getUserId())
                    .masterDeviceId(masterUserDevice.getDeviceId())
                    .productType(userDeviceHandleUserDeviceDto.getUserDevice().getProductType())
                    .productId(userDeviceHandleUserDeviceDto.getUserDevice().getProductId())
                    .deviceId(userDeviceHandleUserDeviceDto.getUserDevice().getDeviceId())
                    .keyCode(userDeviceHandleUserDeviceDto.getKeyCode())
                    .thingModel(userDeviceHandleUserDeviceDto.getChangeThingModel())
                    .cron(cron)
                    .remark(datetimeSlot.getValue())
                    .build();
            userDeviceScheduleService.save(userDeviceSchedule);

            jobFeignClient.addSchedule(ScheduleParamDto.builder()
                    .deviceId(userDeviceHandleUserDeviceDto.getUserDevice().getDeviceId())
                    .scheduleId(userDeviceSchedule.getId())
                    .cron(cron)
                    .build());
        }
    }

}
