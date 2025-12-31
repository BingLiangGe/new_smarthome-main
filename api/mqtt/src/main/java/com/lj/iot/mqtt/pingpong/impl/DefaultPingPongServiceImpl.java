package com.lj.iot.mqtt.pingpong.impl;

import com.lj.iot.mqtt.pingpong.IPingPongService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;

public class DefaultPingPongServiceImpl implements IPingPongService {
    @Override
    public void handler(Channel channel, MqttMessage msg) {

    }
}
