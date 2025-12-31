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
 * 窗帘
 */
@Component("deviceHandle_curtain")
public class DeviceHandleCurtain implements DeviceHandle {

    @Qualifier("intentService_default")
    @Autowired
    private IntentService defaultIntentService;

    @Autowired
    private IEntityAliasService entityAliasService;

    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {


        IntentDto.Slot attrSlot = intentDto.getSlots().get("attr");
        ValidUtils.isNullThrow(attrSlot, "我没听懂，请再说一遍");

        //窗帘打开  一半   80%
        if ("crack".equals(attrSlot.getNormValue())) {
            intentDto.setIntentName("deviceSwitch");

            IntentDto.Slot slot = new IntentDto.Slot();

            IntentDto.Slot attrValueSlot = intentDto.getSlots().get("attrValue");
            if (attrValueSlot != null) {
                slot.setName("value");
                slot.setValue(attrValueSlot.getValue());
                slot.setNormValue(entityAliasService.getEntityKey("new_degree", attrValueSlot.getNormValue()));
                intentDto.getSlots().put("value", slot);
            } else {
                //当说百叶窗的时候技能会命中，这地方需要抛出 insType 为set  且没有attrValue
                ValidUtils.isTrueThrow("set".equals(intentDto.getSlots().get("insType").getNormValue())
                        , "我没听懂，请再说一遍");
            }
            IntentDto.Slot insType = intentDto.getSlots().get("insType");
            if ("adjustDown".equals(intentDto.getSlots().get("insType").getNormValue())) {
                insType.setNormValue("turnOff");
            }
            if ("set".equals(intentDto.getSlots().get("insType").getNormValue())) {
                insType.setNormValue("turnOff");
            }
            if ("adjustUp".equals(intentDto.getSlots().get("insType").getNormValue())) {
                insType.setNormValue("turnOn");
            }

            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }

        //百叶窗、梦幻帘 透光、遮光等
        if ("set".equals(intentDto.getSlots().get("insType").getNormValue())) {
            String entityKey = entityAliasService.getEntityKey("shading", attrSlot.getNormValue());

            intentDto.setIntentName("curtainControl");
            IntentDto.Slot slot = new IntentDto.Slot();
            slot.setName("curtainAdjust");
            slot.setValue(attrSlot.getValue());
            slot.setNormValue(entityKey);
            intentDto.getSlots().put("curtainAdjust", slot);
            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }


        throw CommonException.FAILURE("我没听懂，请在说一遍");
    }
}
