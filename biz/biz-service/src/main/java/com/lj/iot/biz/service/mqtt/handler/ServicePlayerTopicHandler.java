package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserDeviceMeshKey;
import com.lj.iot.biz.db.smart.service.IUserDeviceMeshKeyService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizSceneService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.redis.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author tyj
 * @date 2023-10-30 13:37:23
 */
@Slf4j
@Component
public class ServicePlayerTopicHandler extends AbstractTopicHandler {

    public ServicePlayerTopicHandler() {
//        setSupportTopic(SubTopicEnum.PLAYER_REPLY);
    }

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private ICacheService cacheService;


    @Override
    public void handle(HandleMessage message) {
        log.info("ServicePlayerTopicHandler.handle{}", JSON.toJSONString(message));
        UserDevice userDevice = userDeviceService.getById(message.getTopicDeviceId());

        String key="player_"+userDevice.getDeviceId();
        cacheService.addSeconds(key, "success",  30 * 1000L);
    }
}
