package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 电视
 */
@Component("deviceHandle_tv")
public class DeviceHandleTv implements DeviceHandle {

    @Qualifier("intentService_default")
    @Autowired
    private IntentService defaultIntentService;

    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {


        IntentDto.Slot attrSlot = intentDto.getSlots().get("attr");
        ValidUtils.isNullThrow(attrSlot, "我没听懂，请再说一遍");

        //声音
        if ("volume".equals(attrSlot.getNormValue())) {
            intentDto.setIntentName("volumeAdjust");
            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }

        throw CommonException.FAILURE("我没听懂，请在说一遍");
    }
}
