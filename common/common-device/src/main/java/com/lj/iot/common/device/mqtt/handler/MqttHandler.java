package com.lj.iot.common.device.mqtt.handler;


import com.lj.iot.common.device.mqtt.properties.HandleMessageVo;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 */
public interface MqttHandler {

    /**
     * 消息处理
     * @param message
     */
    void onMessage(ChannelHandlerContext channelHandlerContext, HandleMessageVo message);
}

