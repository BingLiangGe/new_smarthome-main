package com.lj.iot.mqtt.cache;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作静态类
 */
@Slf4j
public class IQ {

    public static void loginSuccess(ChannelHandlerContext ctx, MqttConnectMessage message) {
    }

    public static void disConnect(Channel channel, MqttMessage msg) {

        if (channel.isActive()) {
            channel.close();
        }
    }
}
