package com.lj.iot.api.app;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.MqttParamDto;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.mqtt.client.core.MQTT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;

@Slf4j
@SpringBootTest
public class DeviceRestart {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Test
    public void restart() {
        List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .hardWareVersion("1.3")
                .status(true)
                .build()));

        log.info("size={},deviceId={}", list.size(), list.get(0).getDeviceId());
        for (UserDevice userDevice : list
        ) {
           /*JSONObject jsonObject = new JSONObject();
            jsonObject.put("productId", userDevice.getProductId());
            jsonObject.put("deviceId", userDevice.getDeviceId());

            // 重启设备
            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_RESTART, userDevice.getProductId(), userDevice.getPhysicalDeviceId());
            MqttParamDto paramDto = MqttParamDto.builder()
                    .id(IdUtil.simpleUUID())
                    .time(DateUtil.current())
                    .data(jsonObject)
                    .build();
            MQTT.publish(topic, JSON.toJSONString(paramDto));
            log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(paramDto));*/
        }
    }
}
