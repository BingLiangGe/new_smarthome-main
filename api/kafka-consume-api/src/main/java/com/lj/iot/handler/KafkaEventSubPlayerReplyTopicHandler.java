package com.lj.iot.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.DeviceWsVo;
import com.lj.iot.biz.db.smart.entity.Device;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IDeviceService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 播报上报
 *
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class KafkaEventSubPlayerReplyTopicHandler extends AbstractTopicHandler {
    public KafkaEventSubPlayerReplyTopicHandler() {
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
