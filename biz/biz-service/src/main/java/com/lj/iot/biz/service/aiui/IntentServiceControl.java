package com.lj.iot.biz.service.aiui;

import cn.hutool.core.lang.Validator;
import cn.hutool.extra.spring.SpringUtil;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IEntityAliasService;
import com.lj.iot.biz.service.aiui.enums.TurnOnOff;
import com.lj.iot.common.aiui.core.dto.Answer;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 官方技能
 */
@Component("intentService_CONTROL")
public class IntentServiceControl implements IntentService {

    @Qualifier("intentService_default")
    @Autowired
    private IntentService defaultIntentService;

    @Autowired
    private IEntityAliasService entityAliasService;

    /**
     * 插槽
     * <p>
     * scene
     *
     * @param intentDto
     * @return
     */
    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {

        //设备开关调整后走默认技能处理
        IntentDto.Slot slot = intentDto.getSlots().get("insType");

        IntentDto.Slot deviceSlot= intentDto.getSlots().get("device");

        //分设备处理
        ValidUtils.isNullThrow(deviceSlot, "没有识别设备，请再说一次");

        // 风扇停止特殊处理
        /*if ("fan".equals(deviceSlot.getNormValue()) && "turnOff".equals(slot.getNormValue())){
            throw CommonException.FAILURE("该设备不支持此意图");
        }*/

        if (TurnOnOff.parse(slot.getNormValue()) != null) {

            intentDto.setIntentName("deviceSwitch");
            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }

        //设备暂停
        if ("pause".equals(slot.getNormValue())) {
            intentDto.setIntentName("deviceStop");
            defaultIntentService.handle(masterUserDevice, intentDto);
            return;
        }


        //官方技能有些设备没有英文key，这里进行转换
        if (Validator.hasChinese(deviceSlot.getNormValue())) {
            deviceSlot.setNormValue(entityAliasService.getDeviceKey(deviceSlot.getNormValue()));
        }

        String[] beanNames = SpringUtil.getBeanNamesForType(DeviceHandle.class);
        boolean hasMatch = false;
        for (String beanName : beanNames) {
            if (beanName.equals("deviceHandle_" + deviceSlot.getNormValue())) {
                hasMatch = true;
                break;
            }
        }
        if (hasMatch) {
            DeviceHandle deviceHandle = SpringUtil.getBean("deviceHandle_" + deviceSlot.getNormValue(), DeviceHandle.class);
            deviceHandle.handle(masterUserDevice, intentDto);
        } else {
            throw CommonException.FAILURE("还不支持该种命令");
        }
    }

}
