package com.lj.iot.mqtt.auth;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;

public interface IAuthService {

    boolean authorized(Channel channel, MqttConnectMessage mqttConnectMessage);

}
