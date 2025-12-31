package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.MqttOtaDto;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.db.smart.service.impl.HomeServiceImpl;
import com.lj.iot.biz.service.BizDeviceService;
import com.lj.iot.biz.service.BizProductUpgrade;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class ServiceTopologyOtaReplyTopicHandler extends AbstractTopicHandler {

    public ServiceTopologyOtaReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.SERVICE_TOPOLOGY_OTA_REPLY);
    }

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private BizProductUpgrade bizProductUpgrade;

    @Autowired
    private IOperationLogService operationLogService;

    @Autowired
    private BizDeviceService bizDeviceService;
    @Resource
    ISystemMessagesService systemMessagesService;

    @Autowired
    private BizWsPublishService bizWsPublishService;

    @Autowired
    private IHomeService homeService;
    @Autowired
    private IHomeRoomService homeRoomService;

    @Autowired
    private IUpgradeRecordService upgradeRecordService;



    /**
     * 云端删除设备的拓扑关系,返回   暂时不需要处理
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {
        Device userDevice = deviceService.getById(message.getTopicDeviceId());

        UserDevice userDevices = userDeviceService.getById(message.getTopicDeviceId());


        JSONObject body = message.getBody();
        String action = (String) message.getBody().get("action");
        String productId = (String) message.getBody().get("productId");
        String deviceId = (String) message.getBody().get("deviceId");
        Integer code = (Integer) message.getBody().get("code");
        if (code == 2) { //对全禁止查询业务做处理
            log.info("全禁止查询业务的接收--------------------", body);

            /* todo 测试环境配合插卡取电  UserDevice device = userDeviceService.getById(message.getTopicDeviceId());
            if (device != null) {
                String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_TRIGGER_CLOCK, userDevice.getProductId(), message.getTopicDeviceId());


                MQTT.publish(topic, JSON.toJSONString(new HashMap() {{
                    put("id", message.getTopicDeviceId());

                    int isDel = 0;

                    if (device.getIsDel() != null) {
                        isDel = device.getIsDel() ? 1 : 0;
                    }

                    put("enable", isDel);
                }}));
            }*/
        } else {
            String hardwareversion = (String) message.getBody().get("hardwareversion");
            String softwareversion = (String) message.getBody().get("softwareversion");
            log.info("message版本号=软件版本号={},硬件版本号={}", softwareversion, hardwareversion);
            String bluetoothversion = (String) message.getBody().get("bluetoothversion");

            if (userDevices != null) {
                userDevices.setHardWareVersion(hardwareversion);
                userDevices.setSoftWareVersion(softwareversion);
                userDevices.setBluetoothVersion(bluetoothversion);
                userDeviceService.updateById(userDevices);
            }

            if (Boolean.valueOf(action)) {
                String softWareVersion = userDevice.getVersion();
                if (softWareVersion != null) {
                    //主动请求，需要升级任务
                    UpgradeRecord upgradeRecord = upgradeRecordService.findUpgradeRecordByNotSuccess(message.getTopicDeviceId(), softwareversion, hardwareversion);

                    log.info("进入升级前校验deviceId={},sof={},hard={},升级={}", message.getTopicDeviceId(), softwareversion, hardwareversion, upgradeRecord);
                    if (upgradeRecord != null) {

                        // 升级次数小于五次
                        if (upgradeRecord.getSuccessCount() <= 5) {
                            // 更新ota升级状态
                            upgradeRecord.setIsSuccess(1);
                            upgradeRecord.setSuccessCount(upgradeRecord.getSuccessCount() + 1);
                            upgradeRecord.setSuccessTime(LocalDateTime.now());

                            // 更新设备软件版本
                            userDevices.setSoftWareVersion(softwareversion);
                            userDeviceService.updateById(userDevices);

                            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, productId, deviceId);

                            //版本不相同请求更新
                            MqttOtaDto mqttOtaDto = MqttOtaDto.builder()
                                    .filepath(upgradeRecord.getFilePath())
                                    .hardwareversion(hardwareversion)
                                    .softwareversion(upgradeRecord.getSoftWareVersion())
                                    .productId(productId)
                                    .deviceId(deviceId)
                                    .time(LocalDateTime.now())
                                    .build();
                            MQTT.publish(topic, JSON.toJSONString(mqttOtaDto));
                            log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(mqttOtaDto));
                           /* operationLogService.save(OperationLog.builder()
                                    .action(new Byte("0"))
                                    .deviceId(deviceId)
                                    .productId(productId)
                                    .params(JSON.toJSONString(mqttOtaDto))
                                    .remark(topic)
                                    .build());*/
                            UserDevice one = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(deviceId).build()));
                            Home byId = homeService.getById(one.getHomeId());
                            HomeRoom byId1 = homeRoomService.getById(one.getRoomId());
                            SystemMessages systemMessages = SystemMessages.builder()
                                    .type(1)
                                    .homeName(byId.getHomeName())
                                    .roomName(byId1.getRoomName())
                                    .readType(0)
                                    .createTime(LocalDateTime.now())
                                    .updateTime(LocalDateTime.now())
                                    .userId(one.getUserId())
                                    .homeId(one.getHomeId().intValue())
                                    .messages("网关升级成：" + upgradeRecord.getSoftWareVersion())
                                    .inType("网关升级")
                                    .build();
                            systemMessagesService.save(systemMessages);

                            bizWsPublishService.publish(WsResultVo.SUCCESS(
                                    one.getUserId(),
                                    one.getHomeId(),
                                    RedisTopicConstant.TOPIC_CHANNEL_OTA_UPDATE,
                                    systemMessages));
                            upgradeRecordService.updateById(upgradeRecord);
                        } else {
                            String content = "OTA升级次数超时提醒:【" + upgradeRecord.getDeviceId() + "】时间：【" + LocalDateTime.now().toString() + "】";
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("content", content);
                            JSONObject params = new JSONObject();
                            params.put("msgtype", "text");
                            params.put("text", jsonObject);
                            try {
                                OkHttpUtils.postJson("https://oapi.dingtalk.com/robot/send?access_token=7dd69f0d564356a1ec14396e4acd6c3a62b51c1b3adffe6dc91a0238a1d147f0", params);
                            } catch (Exception e) {
                            }
                        }
                    }


                } else {
                    //更新数据库最新版本
                    Device device = bizDeviceService.findById(deviceId);
                    device.setVersion(softwareversion);
                    log.info("空版本进来更新", device);
                    bizDeviceService.upDataById(device);
                    String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, productId, deviceId);
                    //版本不相同请求更新
                    MqttOtaDto mqttOtaDto = MqttOtaDto.builder()
                            .time(LocalDateTime.now())
                            .build();
                    MQTT.publish(topic, JSON.toJSONString(mqttOtaDto));
                }
            }
        }
        //message.setTopic("");
    }
}
