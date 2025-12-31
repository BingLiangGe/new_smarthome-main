package com.lj.iot.mqtt.pingpong;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;

public interface IPingPongService {

    void handler(Channel channel, MqttMessage msg);
}
