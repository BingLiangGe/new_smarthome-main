package com.lj.iot.common.mqtt.client.core;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author mz
 * @Date 2022/7/26
 * @since 1.0.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class DefaultChannelHandler extends SimpleChannelInboundHandler<MqttMessage> {

    @Autowired
    private MqttHandler mqttHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) {
        switch (msg.fixedHeader().messageType()) {
            case PUBLISH:
                MqttPublishMessage mqttPublishMessage = (MqttPublishMessage) msg;

                JSONObject message = new JSONObject();
                try {
                    if (Optional.ofNullable(mqttPublishMessage.payload()).isPresent()) {
                        message = (JSONObject) JSONObject.parse(mqttPublishMessage.payload()
                                .toString(StandardCharsets.UTF_8));
                    }
                } catch (Exception e) {
                    /*log.error("DefaultChannelHandler.channelRead0:解析消息体异常topic:{},messageId:{}",
                            mqttPublishMessage.variableHeader().topicName(), mqttPublishMessage.variableHeader().packetId());*/
                }

                mqttHandler.onMessage(HandleMessage.builder()
                        .messageId(mqttPublishMessage.variableHeader().packetId())
                        .topic(mqttPublishMessage.variableHeader().topicName())
                        .body(message)
                        .build());

                //发送回复消息
                MqttPubAckMessage mqttPubAckMessage = MqttUtil.pubAckMessage(mqttPublishMessage.variableHeader().packetId());
                ctx.channel().writeAndFlush(mqttPubAckMessage);
                break;
            case CONNACK:
            case DISCONNECT:
            case SUBACK:
            case UNSUBACK:
            case PUBACK:
            case PUBREC:
            case PUBREL:
            case PUBCOMP:
            default:
        }
    }

    /**
     * 心跳
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt == IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT) {
            MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);
            ctx.channel().writeAndFlush(new MqttMessage(fixedHeader));
        }
    }
}
