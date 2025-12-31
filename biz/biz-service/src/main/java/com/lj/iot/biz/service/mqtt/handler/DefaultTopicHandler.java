package com.lj.iot.biz.service.mqtt.handler;

import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
@Slf4j
@Component
public class DefaultTopicHandler extends AbstractTopicHandler {

    public DefaultTopicHandler() {
        setSupportTopic(SubTopicEnum.DEFAULT);
    }

    @Override
    public void handle(HandleMessage message) {
        log.info("没有匹配到处理器MqttMessageHandler.onMessage.topic:{}", message);
    }
}
