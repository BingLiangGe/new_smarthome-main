package com.lj.iot.config.mqtt;

import com.alibaba.fastjson2.JSON;
import com.lj.iot.common.device.mqtt.properties.HandleMessageVo;
import com.lj.iot.common.device.mqtt.utils.MqttUtil;
import com.lj.iot.config.kafka.core.KafkaProducter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AsyncServiceImpl implements IAsyncService {
    @Resource
    private KafkaProducter kafkaProducter;

    @Async
    @Override
    public void send2Kafka(ChannelHandlerContext channelHandlerContext, String topic, HandleMessageVo message)  {
        boolean flag = kafkaProducter.sendMessage(topic, JSON.toJSONString(message));
        if (flag) {
            //发送回复消息
            MqttPubAckMessage mqttPubAckMessage = MqttUtil.pubAckMessage(message.getMessageId());
            channelHandlerContext.channel().writeAndFlush(mqttPubAckMessage);
        }
    }
}
