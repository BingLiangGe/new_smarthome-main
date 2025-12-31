package com.lj.iot.biz.service.mqtt;

import com.alibaba.fastjson.JSON;
import com.lj.iot.biz.base.vo.MqttResultVo;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractTopicHandler implements ITopicHandler {

    private SubTopicEnum supportTopic;

    @Override
    public void setSupportTopic(SubTopicEnum topic) {
        this.supportTopic = topic;
    }

    @Override
    public SubTopicEnum getSupportTopic() {
        return this.supportTopic;
    }

    @Override
    public boolean isSupport(SubTopicEnum topic) {
        return supportTopic == topic;
    }

    @Override
    public void entrance(HandleMessage message) {
        try {
            handle(message);
            successReply(message);
        } catch (CommonException e) {
            log.error("AbstractTopicHandler.entrance:{}", JSON.toJSONString(message), e);
            errorReply(message, e);
        } catch (Exception e) {
            log.error("AbstractTopicHandler.entrance:{}", JSON.toJSONString(message), e);
            message.getBody().put("message", e.getMessage());
            errorReply(message, CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), e.getMessage()));
        }
    }


    @Override
    public void successReply(HandleMessage message) {
        if (StringUtils.hasText(this.getSupportTopic().getTopicReply())) {

            String topic = String.format(this.getSupportTopic().getTopicReply(), message.getTopicProductId(), message.getTopicDeviceId());
            MQTT.publish(topic,
                    MqttResultVo.SUCCESS(message.getBody().getString("id"),
                            message.getBody().get("data")).toString()
            );
        }
    }

    @Override
    public void errorReply(HandleMessage message, CommonException e) {

        if (StringUtils.hasText(this.getSupportTopic().getTopicReply())) {
            String topic = String.format(this.getSupportTopic().getTopicReply(), message.getTopicProductId(), message.getTopicDeviceId());
            MQTT.publish(topic,
                    MqttResultVo.FAILURE_MSG(message.getBody().getString("id"),
                            e.getCode(), e.getMsg(), message.getBody().get("data")
                    ).toString()
            );
        } else if (e.getCode().equals(CommonCodeEnum.SUB_NOT_EXIST.getCode())) {
            String topic = String.format(PubTopicEnum.PUB_TOPOLOGY_NO_EXIST_DEVICE.getTopic(), message.getTopicProductId(), message.getTopicDeviceId());
            MQTT.publish(topic,
                    MqttResultVo.FAILURE_MSG(message.getBody().getString("id"),
                            e.getCode(), e.getMsg(), message.getBody().get("data")
                    ).toString()
            );
        }
    }
}
