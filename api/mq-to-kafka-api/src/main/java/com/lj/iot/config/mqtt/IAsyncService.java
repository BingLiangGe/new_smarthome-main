package com.lj.iot.config.mqtt;

import com.lj.iot.common.device.mqtt.properties.HandleMessageVo;
import io.netty.channel.ChannelHandlerContext;

public interface IAsyncService {
    /**
     * 发送消息到kafka
     * @param channelHandlerContext
     * @param topic
     * @param message
     * @throws InterruptedException
     */
    void send2Kafka(ChannelHandlerContext channelHandlerContext, String topic, HandleMessageVo message) throws InterruptedException;
}
