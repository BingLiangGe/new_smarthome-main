package com.lj.iot.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

@Slf4j
@Component
public class KafkaServiceClockReplyTopicHandler extends AbstractTopicHandler {
    public KafkaServiceClockReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.SERVICE_CLOCK_REPLY);
    }

    private Integer ZERO = Integer.parseInt("0");

    @Resource
    private IUserDeviceService userDeviceService;


    @Override
    public void handle(HandleMessage message) {
        log.info("闹钟消息接收",message);
        UserDevice userDevice = userDeviceService.getById(message.getTopicDeviceId());
        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_TRIGGER_CLOCK, userDevice.getProductId(), message.getTopicDeviceId());

        JSONObject body = message.getBody();
        if (ZERO.compareTo(body.getInteger("code")) != 0) {
            log.error("ServiceClockReplyTopicHandler.handle:闹钟回复失败");
            MQTT.publish(topic, JSON.toJSONString(new HashMap(){{
                put("id",message.getTopicDeviceId());
                put("enable",userDevice.getIsDel());
            }}));
            return;
        }
        MQTT.publish(topic, JSON.toJSONString(new HashMap(){{
            put("id",message.getTopicDeviceId());
            put("enable",userDevice.getIsDel());
        }}));
    }
}
