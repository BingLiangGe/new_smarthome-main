package com.lj.iot.handler;

import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 设备配网成功上报网络信息
 *
 * @author tyj
 * @Date 2023-7-4 14:54:08
 */
@Slf4j
@Component
public class KafkaEventSubDeviceNetWorkTopicHandler extends AbstractTopicHandler {

    public KafkaEventSubDeviceNetWorkTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_SUB_DEVICE_NETWORK);
    }

    @Resource
    private IUserDeviceService userDeviceService;


    @Override
    public void handle(HandleMessage message) {
        log.info("进入wifi账号上传={}", message.getBody());

        JSONObject jsonObject = message.getBody();

        Integer ssid = jsonObject.getInteger("ssid");
        Integer level = jsonObject.getInteger("level");
        String wifiId = jsonObject.getString("wifiId");


        UserDevice userDevice = userDeviceService.getById(message.getTopicDeviceId());

        if (userDevice != null) {
            userDevice.setWifiSs(ssid);
            userDevice.setWifiLevel(level);
            userDevice.setWifiName(wifiId);

            if (jsonObject.getString("vol") != null) {
                try {
                    Integer vol= Integer.valueOf(jsonObject.getString("vol"));

                    Integer volNumber=(vol*100)/22;
                    userDevice.setVolume(volNumber);
                } catch (Exception e) {
                    log.info("进入wifi账号上传设备音量error={}", e.getMessage());
                }
            }
            userDeviceService.updateById(userDevice);
        }

        ValidUtils.isNullThrow(userDevice, "设备不存在");
    }
}
