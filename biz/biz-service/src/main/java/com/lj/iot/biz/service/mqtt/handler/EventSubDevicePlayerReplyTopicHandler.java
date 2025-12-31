package com.lj.iot.biz.service.mqtt.handler;

import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.redis.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 播报回复
 *
 * @author tyj
 * @date 2023-11-13 11:15:03
 */
@Slf4j
@Component
public class EventSubDevicePlayerReplyTopicHandler extends AbstractTopicHandler {

    public EventSubDevicePlayerReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_SUB_PLAYER_REPLY);
    }

    @Autowired
    private ICacheService cacheService;

    @Override
    public void handle(HandleMessage message) {
        String key = "app" + RedisConstant.wait_device + "player_" + message.getTopicDeviceId();
        cacheService.addSeconds(key, "1", 30);

        log.info("EVENT_SUB_PLAYER_REPLY,key={},value={}",key, cacheService.get(key).toString());
    }
}
