package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.base.dto.MqttOtaDto;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;
import com.lj.iot.biz.db.smart.entity.UpgradeRecord;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IProductUpgradeService;
import com.lj.iot.biz.db.smart.service.IUpgradeRecordService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 设备绑定成功上报版本
 *
 * @author tyj
 * @Date 2023-7-4 14:54:08
 */
@Slf4j
@Component
public class EventSubDeviceBindSuccessTopicHandler extends AbstractTopicHandler {

    public EventSubDeviceBindSuccessTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_SUB_DEVICE_BIND_SUCCESS);
    }

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IUpgradeRecordService upgradeRecordService;

    @Autowired
    private IProductUpgradeService productUpgradeService;


    @Override
    public void handle(HandleMessage message) {

       /* JSONObject jsonObject = message.getBody();

        String softWareVersion = jsonObject.getString("softwareversion");
        String hardWareVersion = jsonObject.getString("hardwareversion");

        UserDevice userDevice = userDeviceService.getById(message.getTopicDeviceId());

        if (userDevice != null) {
            userDevice.setHardWareVersion(hardWareVersion);
            userDevice.setSoftWareVersion(softWareVersion);

            userDeviceService.updateById(userDevice);
        }
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        log.info("进入验证版本,deviceId=#{},productId={},hardWareVersion={},softWareVersion={}",
                userDevice.getDeviceId(), userDevice.getProductId(), hardWareVersion, softWareVersion);

        //主动请求，需要升级任务
        ProductUpgrade productUpgrade = productUpgradeService.findNewUpgradeByProduct(userDevice.getProductId(), hardWareVersion, softWareVersion);

        log.info("进入主动升级=device_id=#{},状态{}", userDevice.getDeviceId(), productUpgrade);
        if (productUpgrade != null) {

            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, userDevice.getProductId(), userDevice.getDeviceId());

            //版本不相同请求更新
            MqttOtaDto mqttOtaDto = MqttOtaDto.builder()
                    .filepath(productUpgrade.getVersionUrl())
                    .hardwareversion(productUpgrade.getHardWareVersion())
                    .softwareversion(productUpgrade.getNewVersion())
                    .productId(userDevice.getProductId())
                    .deviceId(userDevice.getDeviceId())
                    .time(LocalDateTime.now()).build();
            MQTT.publish(topic, JSON.toJSONString(mqttOtaDto));
            log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(mqttOtaDto));

            upgradeRecordService.save(UpgradeRecord.builder()
                    .deviceId(userDevice.getDeviceId()).
                    createTime(LocalDateTime.now()).
                    filePath(productUpgrade.getVersionUrl()).
                    softWareVersion(productUpgrade.getNewVersion()).
                    isSuccess(0)
                    .hardWareVersion(productUpgrade.getHardWareVersion())
                    .build());
        }*/
    }
}
