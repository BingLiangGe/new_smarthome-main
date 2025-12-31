package com.lj.iot.common.mqtt.client.core;

import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
public interface MqttHandler {


    void onMessage(HandleMessage message);
}

