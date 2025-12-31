package com.lj.iot.biz.service.aiui;

import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.aiui.core.dto.DeviceNotificationDto;
import com.lj.iot.common.mqtt.client.core.MQTT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 3326绑定子设备
 */
@Slf4j
@Component("deviceNotifi_edit")
public class DeviceNotifiEdit implements DeviceNotificationService {

    @Autowired
    private IUserDeviceService userDeviceService;

    /**
     * 插槽
     *
     * @param deviceNotificationDto
     */
    @Override
    public void handle(DeviceNotificationDto deviceNotificationDto) {
        log.info("3326主控发送回调edit---->{}", deviceNotificationDto.getMasterDeviceId());

        List<UserDevice> list = userDeviceService.selectUserDeviceWith3326(deviceNotificationDto.getHomeId());

        if (!list.isEmpty()) {

            list.forEach(userDevice -> {
                UserDevice chidUserDevice = userDeviceService.getById(deviceNotificationDto.getMasterDeviceId());

                String topic = PubTopicEnum.handlerTopic(PubTopicEnum.ADD_DEVICE_EDIT, userDevice.getMasterProductId(), userDevice.getDeviceId());

                JSONObject respJson = new JSONObject();

                respJson.put("deviceId", chidUserDevice.getDeviceId());
                respJson.put("deviceName", chidUserDevice.getCustomName());
                respJson.put("roomId", chidUserDevice.getRoomId());
                if (chidUserDevice != null) {
                    respJson.put("modelId", chidUserDevice.getModelId());
                }
                MQTT.publish(topic, respJson.toJSONString());
                log.info("Mqtt-Send:" + topic + "=" + respJson.toJSONString());
            });
        }
    }
}
