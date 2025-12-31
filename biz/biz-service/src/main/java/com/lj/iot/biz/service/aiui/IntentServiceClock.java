package com.lj.iot.biz.service.aiui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.base.dto.ScheduleParamDto;
import com.lj.iot.biz.db.smart.entity.UserClock;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserClockService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.util.DatetimeSlotUtil;
import com.lj.iot.fegin.job.JobFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 闹钟
 */
@Component("intentService_clock")
public class IntentServiceClock implements IntentService {

    @Autowired
    private JobFeignClient jobFeignClient;

    @Autowired
    private IUserClockService userClockService;

    /**
     * 插槽
     * <p>
     */
    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {

        IntentDto.Slot datetimeSlot = intentDto.getSlots().get("datetime");
      //  String cron = DatetimeSlotUtil.hasNextTimeCron(datetimeSlot);
        JSONObject jsonObject = JSON.parseObject(datetimeSlot.getNormValue());
        String suggestDatetime = jsonObject.getString("suggestDatetime");
        String cron=  DatetimeSlotUtil.convertToCronExpressionCase(DatetimeSlotUtil.locationDateTimeCaseString(DatetimeSlotUtil.toLocalDateTime(suggestDatetime)));

        UserClock userClock = UserClock.builder()
                .userId(masterUserDevice.getUserId())
                .masterDeviceId(masterUserDevice.getDeviceId())
                .cron(cron)
                .remark(datetimeSlot.getValue())
                .clockStatus(0)
                .build();
        userClockService.save(userClock);
        jobFeignClient.addClock(ScheduleParamDto.builder()
                .deviceId(masterUserDevice.getDeviceId())
                .scheduleId(userClock.getId())
                .cron(cron)
                .build());

        intentDto.setAnswer("已经为您设置"+datetimeSlot.getValue()+"的闹钟。");
    }
}
