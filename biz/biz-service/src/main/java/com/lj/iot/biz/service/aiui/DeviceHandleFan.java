package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 风扇
 */
@Component("deviceHandle_fan")
public class DeviceHandleFan implements DeviceHandle {

    @Qualifier("intentService_default")
    @Autowired
    private IntentService defaultIntentService;

    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {


        IntentDto.Slot attrSlot = intentDto.getSlots().get("attr");
        ValidUtils.isNullThrow(attrSlot, "我没听懂，请再说一遍");

        //空调风速
        if ("windSpeed".equals(attrSlot.getNormValue())) {
            intentDto.setIntentName("fanWindSpeed");

            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }

        //风扇摇头
        if ("mode".equals(attrSlot.getNormValue())) {
            intentDto.setIntentName("fanSwing");

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

        throw CommonException.FAILURE("我没听懂，请在说一遍");
    }
}
