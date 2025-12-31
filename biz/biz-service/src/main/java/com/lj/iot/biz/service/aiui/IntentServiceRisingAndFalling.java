package com.lj.iot.biz.service.aiui;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.mahjong.MahjongMachineUtil;
import com.lj.iot.biz.service.mahjong.MahjongMachineVoiceUtil;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 打色子
 */
@Component("intentService_RisingAndFalling")
public class IntentServiceRisingAndFalling implements IntentService {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Resource
    MqttPushService mqttPushService;


    /**
     * 插槽
     * <p>
     */
    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {

        IntentDto.Slot majData = intentDto.getSlots().get("rising_maj");


        UserDevice majonDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .masterDeviceId(masterUserDevice.getDeviceId())
                .productType("mahjong_machine").build()));


        if (majonDevice == null) {
            majonDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                    .masterDeviceId(masterUserDevice.getDeviceId())
                    .productType("mahjong_voice").build()));
        }


        if (majonDevice == null) {
            intentDto.setAnswer("没有绑定麻将机,或该设备不支持此操作");
            return;
        }

        int value = 0;

        if ("mahjong_machine".equals(majonDevice.getProductType())) {
            if ("down".equals(majData.getNormValue())) {  // 下
                value = 0;
            } else if ("up".equals(majData.getNormValue())) {  // 上
                value = 1;
            }
        }


        List<ThingModelProperty> propertyList = Lists.newArrayList();

        ThingModelProperty property = majonDevice.getThingModel().getProperties().get(5);

        property.setValue(value);

        propertyList.add(property);

        ThingModel properties = ThingModel
                .builder()
                .properties(propertyList).build();

        String identifier = properties.getProperties().get(0).getIdentifier();

        String[] datas = null;

        if ("mahjong_machine".equals(majonDevice.getProductType())) {
            datas = MahjongMachineUtil.sendMachineData(identifier, MahjongMachineUtil.getPositionData(value));
        } else {
            datas = MahjongMachineVoiceUtil.sendMachineData(identifier, MahjongMachineUtil.getPositionData(value));
        }

        mqttPushService.pushMethMajongMachine(masterUserDevice, majonDevice, datas, identifier, properties, null);


        intentDto.setAnswer("已经为您" + majData.getValue());
    }
}
