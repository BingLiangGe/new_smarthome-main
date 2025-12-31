package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 空调
 */
@Component("deviceHandle_airControl")
public class DeviceHandleAirControl implements DeviceHandle {

    @Qualifier("intentService_default")
    @Autowired
    private IntentService defaultIntentService;

    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {


        IntentDto.Slot attrSlot = intentDto.getSlots().get("attr");
        ValidUtils.isNullThrow(attrSlot, "我没听懂，请再说一遍");

        //空调模式
        if ("mode".equals(attrSlot.getNormValue())) {
            intentDto.setIntentName("acMode");

            IntentDto.Slot modeSlot = new IntentDto.Slot();

            IntentDto.Slot attrValueSlot = intentDto.getSlots().get("attrValue");
            ValidUtils.isNullThrow(attrValueSlot, "我没听懂，请再说一遍");

            modeSlot.setName("mode");
            modeSlot.setValue(attrValueSlot.getValue());
            modeSlot.setNormValue(attrValueSlot.getNormValue());
            intentDto.getSlots().put("mode", modeSlot);
            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }

        //空调温度
        if ("temperature".equals(attrSlot.getNormValue())) {
            intentDto.setIntentName("acTemperatureAdjust");

            //设置具体温度时需要解析温度
            IntentDto.Slot insTypeSlot = intentDto.getSlots().get("insType");
            ValidUtils.isNullThrow(insTypeSlot, "我没听懂，请再说一遍");

            if ("set".equals(insTypeSlot.getNormValue())) {
                IntentDto.Slot attrValueSlot = intentDto.getSlots().get("attrValue");
                ValidUtils.isNullThrow(attrValueSlot, "我没听懂，请再说一遍");
                IntentDto.Slot valueSlot = new IntentDto.Slot();
                valueSlot.setName("value");
                valueSlot.setValue(attrValueSlot.getValue());
                valueSlot.setNormValue(attrValueSlot.getNormValue());
                intentDto.getSlots().put("value", valueSlot);
            }

            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }

        //空调风速
        if ("windSpeed".equals(attrSlot.getNormValue())) {
            intentDto.setIntentName("acWindSpeed");

            IntentDto.Slot windSpeedSlot = new IntentDto.Slot();

            //设置具体温度时需要解析温度
            IntentDto.Slot insTypeSlot = intentDto.getSlots().get("insType");
            ValidUtils.isNullThrow(insTypeSlot, "我没听懂，请再说一遍");

            IntentDto.Slot attrValueSlot = intentDto.getSlots().get("attrValue");
            ValidUtils.isNullThrow(attrValueSlot, "我没听懂，请再说一遍");
            windSpeedSlot.setName("windSpeed");
            windSpeedSlot.setValue(attrValueSlot.getValue());
            windSpeedSlot.setNormValue(attrValueSlot.getNormValue());
            intentDto.getSlots().put("windSpeed", windSpeedSlot);

            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }

        throw CommonException.FAILURE("我没听懂，请在说一遍");
    }
}
