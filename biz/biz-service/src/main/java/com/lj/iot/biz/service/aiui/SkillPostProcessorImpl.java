package com.lj.iot.biz.service.aiui;

import cn.hutool.extra.spring.SpringUtil;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.service.ISkillPostProcessor;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component("skillPostProcessor")
public class SkillPostProcessorImpl implements ISkillPostProcessor {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Qualifier("intentService_default")
    @Autowired
    private IntentService defaultIntentService;

    @Qualifier("intentService_schedule")
    @Autowired
    private IntentService scheduleIntentService;

    /**
     * {
     * "name": "Device.OnOff",
     * "score": 1,
     * "confirmationStatus": "NONE",
     * "slots": {
     * "OnOff": {
     * "confirmationStatus": "NONE",
     * "moreValue": null,
     * "name": "OnOff",
     * "normValue": "TurnOn",
     * "value": "打开"
     * },
     * "device": {
     * "confirmationStatus": "NONE",
     * "moreValue": null,
     * "name": "device",
     * "normValue": "空调",
     * "value": "空调"
     * }* 			}
     * }
     */
    @Override
    public void handle(IntentDto intentDto) {

        UserDevice masterUserDevice = userDeviceService.getById(intentDto.getMasterDeviceId());
        ValidUtils.isNullThrow(masterUserDevice, "主控还未绑定,请添加主控");

        String[] beanNames = SpringUtil.getBeanNamesForType(IntentService.class);
        boolean hasMatch = false;
        for (String beanName : beanNames) {
            if (beanName.equals("intentService_" + intentDto.getIntentName())) {
                hasMatch = true;
                break;
            }
        }

        if (hasMatch) {
            IntentService intentService = SpringUtil.getBean("intentService_" + intentDto.getIntentName(), IntentService.class);
            intentService.handle(masterUserDevice, intentDto);
            return;
        }

        //调度  正常的意图名后面加上_schedule    比如设备开关的意图为deviceSwitch  他对应的任务调度就取名 deviceSwitch_schedule
        if (StringUtils.endsWith(intentDto.getIntentName(), "_schedule")) {
            scheduleIntentService.handle(masterUserDevice, intentDto);
            return;
        }

        //走统一技能处理
        defaultIntentService.handle(masterUserDevice, intentDto);
    }
}
