package com.lj.iot.mqtt.auth.impl;

import com.lj.iot.mqtt.auth.IAuthService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;

public class DefaultAuthServiceImpl implements IAuthService {
    @Override
    public boolean authorized(Channel channel, MqttConnectMessage mqttConnectMessage) {

        // mqttConnectMessage.variableHeader().hasUserName();
        // mqttConnectMessage.variableHeader().hasPassword();
        // mqttConnectMessage.payload().userName();
        // mqttConnectMessage.payload().passwordInBytes();
        return true;
    }
}

