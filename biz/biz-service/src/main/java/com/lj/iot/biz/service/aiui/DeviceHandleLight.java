package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 灯
 */
@Component("deviceHandle_light")
public class DeviceHandleLight implements DeviceHandle {

    @Qualifier("intentService_default")
    @Autowired
    private IntentService defaultIntentService;

    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {


        IntentDto.Slot attrSlot = intentDto.getSlots().get("attr");
        ValidUtils.isNullThrow(attrSlot, "我没听懂，请再说一遍");

        //颜色
        if ("color".equals(attrSlot.getNormValue())) {
            intentDto.setIntentName("rgbColor");

            IntentDto.Slot attrValueSlot = intentDto.getSlots().get("attrValue");
            ValidUtils.isNullThrow(attrValueSlot, "我没听懂，请再说一遍");
            IntentDto.Slot colorSlot = new IntentDto.Slot();
            colorSlot.setName("color");
            colorSlot.setValue(attrValueSlot.getValue());
            colorSlot.setNormValue(attrValueSlot.getNormValue());
            intentDto.getSlots().put("color", colorSlot);

            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }

        //亮度
        if ("brightness".equals(attrSlot.getNormValue())) {
            intentDto.setIntentName("brightnessAdjust");

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

        //色温
        if ("colorTemperature".equals(attrSlot.getNormValue())) {
            intentDto.setIntentName("colorTemperatureAdjust");

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

        throw CommonException.FAILURE("我没听懂，请在说一遍");
    }
}
