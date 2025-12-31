package com.lj.iot.biz.service.aiui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.ScheduleParamDto;
import com.lj.iot.biz.db.smart.entity.UserClock;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserClockService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.mahjong.MahjongMachineUtil;
import com.lj.iot.biz.service.mahjong.MahjongMachineVoiceUtil;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.util.DatetimeSlotUtil;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.util.ValidUtils;
import com.lj.iot.fegin.job.JobFeignClient;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 闹钟
 */
@Component("intentService_GearPosition")
public class IntentServiceGearPosition implements IntentService {


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

        List<ThingModelProperty> propertyList = Lists.newArrayList();

        ThingModelProperty property = majonDevice.getThingModel().getProperties().get(6);

        property.setValue(intentDto.getSlots().get("majo_junt").getNormValue());

        propertyList.add(property);

        ThingModel properties = ThingModel
                .builder()
                .properties(propertyList).build();

        int value = Integer.valueOf(properties.getProperties().get(0).getValue().toString());
        String identifier = properties.getProperties().get(0).getIdentifier();



        String[] datas = null;

        if ("mahjong_machine".equals(majonDevice.getProductType())){
            datas= MahjongMachineUtil.sendMachineData(identifier, MahjongMachineUtil.getPositionData(value));
        }else {
            datas= MahjongMachineVoiceUtil.sendMachineData(identifier, MahjongMachineUtil.getPositionData(value));
        }

        mqttPushService.pushMethMajongMachine(masterUserDevice, majonDevice, datas, identifier, properties, null);


        intentDto.setAnswer("好的,调档到" + intentDto.getSlots().get("majo_junt").getNormValue() + "张");
    }
}
