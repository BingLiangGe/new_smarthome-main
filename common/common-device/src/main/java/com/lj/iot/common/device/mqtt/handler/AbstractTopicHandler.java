package com.lj.iot.common.device.mqtt.handler;

import com.alibaba.fastjson2.JSONObject;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.device.enums.DeviceSubTopicEnum;
import com.lj.iot.common.device.mqtt.MQTT;
import com.lj.iot.common.device.mqtt.properties.HandleMessageVo;
import com.lj.iot.common.device.mqtt.properties.MqttResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 *
 */
@Slf4j
public abstract class AbstractTopicHandler implements ITopicHandler {
    private DeviceSubTopicEnum supportTopic;

    @Override
    public void setSupportTopic(DeviceSubTopicEnum topic) {
        this.supportTopic = topic;
    }

    @Override
    public DeviceSubTopicEnum getSupportTopic() {
        return this.supportTopic;
    }

    @Override
    public boolean isSupport(DeviceSubTopicEnum topic) {
        return supportTopic == topic;
    }

    @Override
    public void entrance(HandleMessageVo message) {
        try {
            handle(message);
            successReply(message);
        } catch (CommonException e) {
            log.error("AbstractTopicHandler.entrance:{}", JSONObject.toJSONString(message), e);
            errorReply(message, e);
        } catch (Exception e) {
            log.error("AbstractTopicHandler.entrance:{}", JSONObject.toJSONString(message), e);
            JSONObject jsonObject = JSONObject.parseObject(message.getBody());
            if(jsonObject == null){
                jsonObject = new JSONObject();
            }
            jsonObject.put("message", e.getMessage());
            errorReply(message, CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), e.getMessage()));
        }
    }


    @Override
    public void successReply(HandleMessageVo message) {
        if (StringUtils.hasText(this.getSupportTopic().getTopicReply())) {
            String topic = String.format(this.getSupportTopic().getTopicReply(), message.getTopicProductId(), message.getTopicDeviceId());
            JSONObject jsonObject = JSONObject.parseObject(message.getBody());
            MQTT.publish(topic,
                    MqttResultVo.SUCCESS(jsonObject.getString("id"),
                            jsonObject.get("data")).toString()
            );
        }
    }

    @Override
    public void errorReply(HandleMessageVo message, CommonException e) {
        if (StringUtils.hasText(this.getSupportTopic().getTopicReply())) {
            String topic = String.format(this.getSupportTopic().getTopicReply(), message.getTopicProductId(), message.getTopicDeviceId());
            JSONObject jsonObject = JSONObject.parseObject(message.getBody());
            MQTT.publish(topic,
                    MqttResultVo.FAILURE_MSG(jsonObject.getString("id"),
                            e.getCode(), e.getMsg(), jsonObject.get("data")
                    ).toString()
            );
        } else if (e.getCode().equals(CommonCodeEnum.SUB_NOT_EXIST.getCode())) {
            System.out.println("--------errorReply---------子设备数据不存在---");
//            String topic = String.format(PubTopicEnum.PUB_TOPOLOGY_NO_EXIST_DEVICE.getTopic(), message.getTopicProductId(), message.getTopicDeviceId());
//            MQTT.publish(topic,
//                    MqttResultVo.FAILURE_MSG(message.getBody().getString("id"),
//                            e.getCode(), e.getMsg(), message.getBody().get("data")
//                    ).toString()
//            );
        }
    }
}
