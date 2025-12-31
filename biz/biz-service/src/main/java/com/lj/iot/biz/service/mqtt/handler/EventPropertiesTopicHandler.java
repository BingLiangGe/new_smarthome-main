package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserDeviceMeshKey;
import com.lj.iot.biz.db.smart.service.IApiConfigService;
import com.lj.iot.biz.db.smart.service.IUserDeviceMeshKeyService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizSceneService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mahjong.MahjongMachineUtil;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class EventPropertiesTopicHandler extends AbstractTopicHandler {

    public EventPropertiesTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_PROPERTIES_POST);
    }

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private BizWsPublishService bizWsPublishService;

    @Autowired
    private BizUserDeviceService bizUserDeviceService;

    @Autowired
    private IUserDeviceMeshKeyService userDeviceMeshKeyService;

    @Autowired
    private BizSceneService bizSceneService;

    @Autowired
    private ICacheService cacheService;


    @Autowired
    private IApiConfigService apiConfigService;

    /**
     * 设备属性数据上报
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": { //属性对象
     * "powerstate1": "on", //属性键值对
     * "powerstate2": "off",
     * ...
     * },
     * }
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {

        Map<String, Object> map = (Map<String, Object>) message.getBody().get("data");

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId((String) map.get("deviceId"))
                .masterDeviceId(message.getTopicDeviceId())
                .build()));

        ValidUtils.isNullThrow(userDevice, CommonCodeEnum.SUB_NOT_EXIST.getCode(), "设备不存在");

        // 门锁特殊处理
        if ("gate_lock".equals(userDevice.getProductType()) || "room_lock".equals(userDevice.getProductType())) {
            String key = "app" + RedisConstant.wait_lock_device + "player_" + userDevice.getDeviceId();
            cacheService.addSeconds(key, "1", 30);

            UserDevice masterDeviceId = userDeviceService.getById(userDevice.getMasterDeviceId());

            if ("1.3".equals(masterDeviceId.getHardWareVersion()) ) {

                JSONObject paramJson = JSON.parseObject(JSON.toJSONString(map));

                String power = paramJson.getJSONArray("properties").getJSONObject(0).getString("power");

                JSONObject paramsPower = new JSONObject();
                paramsPower.put("device_id", paramJson.getString("deviceId"));
                paramsPower.put("code", "2");
                paramsPower.put("value", power);
                apiConfigService.sendApiConfigData(paramsPower, "/device/push/device/status");

                JSONObject paramsValue = new JSONObject();
                paramsValue.put("device_id", paramJson.getString("deviceId"));
                paramsValue.put("code", "0");
                apiConfigService.sendApiConfigData(paramsValue, "/device/push/device/status");


            }
        }

        // 麻将机特殊处理
        if ("mahjong_machine".equals(userDevice.getProductType())) {

            JSONObject paramJson = message.getBody().getJSONObject("data");


            String value = paramJson.getJSONArray("properties").getJSONObject(0).getString("value");
            String identifier = paramJson.getJSONArray("properties").getJSONObject(0).getString("identifier");

            Integer data = MahjongMachineUtil.hexToInteger(value);

            paramJson.getJSONArray("properties").getJSONObject(0).put("value", data);

            bizUserDeviceService.saveTogetherProperties(userDevice, paramJson);
        } else {
            bizUserDeviceService.saveTogetherProperties(userDevice, map);
        }

        userDevice = userDeviceService.getById(userDevice.getDeviceId());

        // 蓝牙设备
        if ("2".equals(map.get("type")) && "MESH".equals(userDevice.getSignalType())) {

            userDevice.setMessageTime(LocalDateTime.now());

            userDeviceService.updateById(userDevice);
        }

        //情景面板相关设备如果是type类型为2表示主动触发，且有情景按钮 需要执行场景
        if ("2".equals(map.get("type")) &&
                userDeviceMeshKeyService.count(new QueryWrapper<>(UserDeviceMeshKey.builder()
                        .deviceId(userDevice.getDeviceId())
                        .build())) > 0L) {
            ThingModel changeThingModel = JSON.parseObject(JSON.toJSONString(map), ThingModel.class);
            String identifier = changeThingModel.getProperties().get(0).getIdentifier();
            String value = changeThingModel.getProperties().get(0).getValue().toString();
            List<UserDeviceMeshKey> keyList = userDeviceMeshKeyService.list(new QueryWrapper<>(UserDeviceMeshKey.builder()
                    .deviceId(userDevice.getDeviceId())
                    .identifier(identifier)
                    .value(Integer.valueOf(value))
                    .build()));

            if (!keyList.isEmpty()) {

                for (UserDeviceMeshKey key : keyList
                ) {

                    /* todo 插卡取电特殊处理——等待设备升级后解除
                    if (key != null && key.getSceneId() != 0L) {
                        // 插卡取电则跳出
                        if ("scene_card".equals(userDevice.getProductType())) {
                            break;
                        }
                        bizSceneService.trigger(key.getSceneId(), OperationEnum.S_S_C);
                    }*/


                    if (key != null && key.getSceneId() != 0L) {


                        if ("scene_three".equals(userDevice.getProductType())) {
                            bizSceneService.triggerThree(key.getSceneId(), OperationEnum.S_S_C);
                        } else {
                            bizSceneService.trigger(key.getSceneId(), OperationEnum.S_S_C);
                        }
                    }
                }
            }
        }

        // TODO: 2023/3/16 给APP推数据,可能要判断用户
        bizWsPublishService.publishAllMemberByHomeId(
                RedisTopicConstant.TOPIC_CHANNEL_DEVICE_PROPERTIES_POST,
                userDevice.getHomeId(), userDevice);


        // 插卡取电
        if ("scene_card".equals(userDevice.getProductType())) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ThingModel changeThingModel = JSON.parseObject(JSON.toJSONString(map), ThingModel.class);
                    String value = changeThingModel.getProperties().get(0).getValue().toString();
                    String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_TRIGGER_CLOCK, message.getTopicProductId(), message.getTopicDeviceId());
                    try {
                        Thread.sleep(1000 * 10);
                        // 解锁
                        if ("0".equals(value)) {
                            MQTT.publish(topic, JSON.toJSONString(new HashMap() {{
                                put("id", message.getTopicDeviceId());
                                put("enable", 1);
                            }}));
                        } else {
                            MQTT.publish(topic, JSON.toJSONString(new HashMap() {{
                                put("id", message.getTopicDeviceId());
                                put("enable", 0);
                            }}));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }
    }
}
