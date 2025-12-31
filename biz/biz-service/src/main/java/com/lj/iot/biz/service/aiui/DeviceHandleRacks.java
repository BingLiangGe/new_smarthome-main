package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IEntityAliasService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 晾衣架
 */
@Component("deviceHandle_racks")
public class DeviceHandleRacks implements DeviceHandle {

    @Qualifier("intentService_default")
    @Autowired
    private IntentService defaultIntentService;

    @Autowired
    private IEntityAliasService entityAliasService;

    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {


        IntentDto.Slot insTypeSlot = intentDto.getSlots().get("insType");
        ValidUtils.isNullThrow(insTypeSlot, "我没听懂，请再说一遍");

        //晾衣架
        if ("set".equals(insTypeSlot.getNormValue())) {
            intentDto.setIntentName("racksControl");

            IntentDto.Slot slot = new IntentDto.Slot();

            IntentDto.Slot attrValueSlot = intentDto.getSlots().get("attrValue");
            if (attrValueSlot != null) {

                String normValue = entityAliasService.getEntityKey("racks_mode", attrValueSlot.getNormValue());
                ValidUtils.isNullThrow(normValue,"我没听懂，请再说一遍");

                slot.setName("mode");
                slot.setValue(attrValueSlot.getValue());
                slot.setNormValue(normValue);
                intentDto.getSlots().put("mode", slot);
            }
            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }

        if ("ascend".equals(insTypeSlot.getNormValue())) {
            intentDto.setIntentName("racksControl");

            IntentDto.Slot slot = new IntentDto.Slot();
            slot.setName("mode");
            slot.setValue("上升");
            slot.setNormValue("open");
            intentDto.getSlots().put("mode", slot);

            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }
        if ("descend".equals(insTypeSlot.getNormValue())) {
            intentDto.setIntentName("racksControl");

            IntentDto.Slot slot = new IntentDto.Slot();
            slot.setName("mode");
            slot.setValue("上升");
            slot.setNormValue("open");
            intentDto.getSlots().put("mode", slot);

            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }
        throw CommonException.FAILURE("我没听懂，请在说一遍");
    }
}
