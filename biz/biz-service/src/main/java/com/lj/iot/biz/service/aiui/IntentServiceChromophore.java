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
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.util.DatetimeSlotUtil;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.fegin.job.JobFeignClient;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 打色子
 */
@Component("intentService_Chromophore")
public class IntentServiceChromophore implements IntentService {

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

        IntentDto.Slot majData = intentDto.getSlots().get("chromophore_maj");


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

        if ("mahjong_voice".equals(majonDevice.getProductType())){
            intentDto.setAnswer("声控麻将机不支持此操作");
            return;
        }

        int value = 0;

        if ("north".equals(majData.getNormValue())) {  // 北
            value = 3;
        } else if ("west".equals(majData.getNormValue())) {  // 西
            value = 2;
        } else if ("south".equals(majData.getNormValue())) {  // 南
            value = 1;
        } else if ("east".equals(majData.getNormValue())) {  // 东
            value = 0;
        }


        List<ThingModelProperty> propertyList = Lists.newArrayList();

        ThingModelProperty property = majonDevice.getThingModel().getProperties().get(2);

        property.setValue(value);

        propertyList.add(property);

        ThingModel properties = ThingModel
                .builder()
                .properties(propertyList).build();

        String identifier = properties.getProperties().get(0).getIdentifier();
        String[] datas = MahjongMachineUtil.sendMachineData(identifier, value);

        mqttPushService.pushMethMajongMachine(masterUserDevice, majonDevice, datas, identifier, properties, null);


        intentDto.setAnswer("已经为您设置" + majData.getValue());
    }
}
