package com.lj.iot.biz.service.aiui;

import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.db.smart.entity.IrModel;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IDeviceService;
import com.lj.iot.biz.db.smart.service.IIrModelService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizNoticeService;
import com.lj.iot.biz.service.enums.ProductTypeEnum;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.aiui.core.dto.DeviceNotificationDto;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.mqtt.client.core.MQTT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SignalType;

import java.util.List;

/**
 * 3326绑定子设备
 */
@Slf4j
@Component("deviceNotifi_bind")
public class DeviceNotifiBind implements DeviceNotificationService {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IIrModelService irModelService;

    /**
     * 插槽
     *
     * @param deviceNotificationDto
     */
    @Override
    public void handle(DeviceNotificationDto deviceNotificationDto) {
        log.info("3326主控发送回调bind---->{}", deviceNotificationDto.getMasterDeviceId());

        List<UserDevice> list = userDeviceService.selectUserDeviceWith3326(deviceNotificationDto.getHomeId());

        if (!list.isEmpty()) {

            list.forEach(userDevice -> {
                String topic = PubTopicEnum.handlerTopic(PubTopicEnum.ADD_DEVICE_BIND, userDevice.getProductId(), userDevice.getDeviceId());
                JSONObject respJson = new JSONObject();

                UserDevice device = userDeviceService.getById(deviceNotificationDto.getMasterDeviceId());
                respJson.put("deviceId", deviceNotificationDto.getMasterDeviceId());

                if (device != null){
                    respJson.put("productType", device.getProductType());
                    respJson.put("productId", device.getProductId());
                    respJson.put("signalType", device.getSignalType());

                    // 空调特殊处理
                    if ("IR".equals(device.getSignalType()) || "RF".equals(device.getSignalType())) {
                        IrModel irModel = irModelService.getById(device.getModelId());
                        respJson.put("modelId", device.getModelId());
                        respJson.put("brandId", device.getBrandId());
                        respJson.put("fileType", irModel.getFileType());
                        respJson.put("kfId", irModel.getFileId());
                    }
                }

                MQTT.publish(topic, respJson.toJSONString());
                log.info("Mqtt-Send:" + topic + "=" + respJson.toJSONString());
            });
        }
    }
}
