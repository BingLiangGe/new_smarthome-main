package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.db.smart.entity.RfModel;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserDeviceMeshKey;
import com.lj.iot.biz.db.smart.entity.UserDeviceRfKey;
import com.lj.iot.biz.db.smart.service.IRfModelService;
import com.lj.iot.biz.db.smart.service.IUserDeviceMeshKeyService;
import com.lj.iot.biz.db.smart.service.IUserDeviceRfKeyService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizSceneService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.OfflineTypeEnum;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tyj
 * @date 2023-9-23 16:15:27
 */
@Slf4j
@Component
public class ServiceLockTopicHandler extends AbstractTopicHandler {

    public ServiceLockTopicHandler() {
        setSupportTopic(SubTopicEnum.LOCK);
    }

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private BizSceneService bizSceneService;

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private IUserDeviceMeshKeyService userDeviceMeshKeyService;

    /**
     * 射频学码数据上报
     * {
     * "id": "123",  //消息ID
     * "code": 0,    //0:成功  -1:失败
     * "data":{
     * "signalType":"IR", //信号类型  IR\|RF
     * "keyId": 12,  //按键ID
     * "productId": "56789", //产品ID
     * "deviceId": "123456", //设备ID
     * "value": [89,23,23,...] //学到的码值
     * },
     * "msg": "success",   //消息描述
     * }
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {
        log.info("ServiceLockTopicHandler.handle{}", JSON.toJSONString(message));
        UserDevice userDevice = userDeviceService.getById(message.getTopicDeviceId());
        JSONObject paramJson = message.getBody();

        // 解锁类型
        int type = paramJson.getInteger("enable");

        // 解锁
        if (type == 0) {
            String key = "app" + RedisConstant.wait_lock_reply_device + "player_" + userDevice.getMasterDeviceId();

            log.info("解锁主控={}",key);
            cacheService.addSeconds(key, "1", 20);

            log.info("ServiceLockTopicHandler,key={},value={}",key, cacheService.get(key).toString());
        }

        UserDevice sceneDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .masterDeviceId(userDevice.getDeviceId())
                .productType("scene_card").build()));

        if (sceneDevice == null) {
            log.info("ServiceLockTopicHandler无插卡取电,deviceId={}", userDevice.getDeviceId());
            return;
        }

        String identifier = "powerstate";
        List<UserDeviceMeshKey> keyList = userDeviceMeshKeyService.list(new QueryWrapper<>(UserDeviceMeshKey.builder()
                .deviceId(sceneDevice.getDeviceId())
                .identifier(identifier)
                .value(type)
                .build()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!keyList.isEmpty()) {

                        for (UserDeviceMeshKey key : keyList
                        ) {
                            if (key != null && key.getSceneId() != 0L) {
                                Thread.sleep(1000 * 18);
                                bizSceneService.trigger(key.getSceneId(), OperationEnum.S_S_C);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
