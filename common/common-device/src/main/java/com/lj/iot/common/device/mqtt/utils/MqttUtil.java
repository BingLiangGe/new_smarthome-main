package com.lj.iot.common.device.mqtt.utils;

import com.lj.iot.common.device.mqtt.properties.MqttClientProperties;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.*;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MqttUtil {
    private final static AtomicInteger nextMessageId = new AtomicInteger(1);

    public static MqttConnectMessage connectMessage(MqttClientProperties properties) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(
                MqttMessageType.CONNECT,
                false,
                MqttQoS.AT_MOST_ONCE,
                false,
                0);
        MqttConnectVariableHeader variableHeader = new MqttConnectVariableHeader(
                properties.getProtocolVersion().protocolName(),
                properties.getProtocolVersion().protocolLevel(),
                true,
                true,
                false,
                0,
                false,
                false,
                properties.getKeepAliveInterval());
        MqttConnectPayload payload = new MqttConnectPayload(
                properties.getClientId(),
                null,
                null,
                properties.getClientId(),
                properties.getPassword().getBytes(StandardCharsets.UTF_8));
        return new MqttConnectMessage(fixedHeader, variableHeader, payload);
    }

    public static MqttSubscribeMessage subscribeMessage(String[] topics) {
        List<MqttTopicSubscription> list = getTopicSubscriptions(topics);
        return subscribeMessage(getNewMessageId(), list);
    }

    public static List<MqttTopicSubscription> getTopicSubscriptions(String[] topics) {
        List<MqttTopicSubscription> list = new LinkedList<>();
        for (String topic : topics) {
            list.add(new MqttTopicSubscription(topic, MqttQoS.valueOf(0)));
        }
        return list;
    }

    public static MqttSubscribeMessage subscribeMessage(int messageId, List<MqttTopicSubscription> mqttTopicSubscriptions) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE,
                false, 0);
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttSubscribePayload mqttSubscribePayload = new MqttSubscribePayload(mqttTopicSubscriptions);
        return new MqttSubscribeMessage(mqttFixedHeader, mqttMessageIdVariableHeader, mqttSubscribePayload);
    }

    private static Integer getNewMessageId() {
        synchronized (nextMessageId) {
            nextMessageId.compareAndSet(0xffff, 1);
            return nextMessageId.getAndIncrement();
        }
    }

    public static MqttPubAckMessage pubAckMessage(int messageId) {
        messageId = messageId == -1 ? getNewMessageId() : messageId;
        return (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                null);
    }

    public static MqttPublishMessage publishMessage(String topic, ByteBuf payload, MqttQoS qos, boolean retain) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, qos, retain, 0);
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(topic, getNewMessageId());
        return new MqttPublishMessage(fixedHeader, variableHeader, payload);
    }
}