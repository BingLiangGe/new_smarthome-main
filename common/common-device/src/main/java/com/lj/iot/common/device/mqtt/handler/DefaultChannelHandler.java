package com.lj.iot.common.device.mqtt.handler;

import com.lj.iot.common.device.mqtt.properties.HandleMessageVo;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
@ChannelHandler.Sharable
public class DefaultChannelHandler extends SimpleChannelInboundHandler<MqttMessage> {

    @Resource
    private MqttHandler mqttHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {
        MqttPublishMessage mqttPublishMessage;
        String payload = null;
        switch (mqttMessage.fixedHeader().messageType()) {
            case PUBLISH:
                 mqttPublishMessage = (MqttPublishMessage) mqttMessage;
                try {
                    if (Optional.ofNullable(mqttPublishMessage.payload()).isPresent()) {
                       payload = mqttPublishMessage.payload().toString(StandardCharsets.UTF_8);
                        log.error("打印接受到MQ消息：{}", payload);
                        log.info("+++++++++++++++++++++++++PUBLISH->message={}", payload);
                    }
                } catch (Exception e) {
                    log.error("DefaultChannelHandler.channelRead0:解析消息体异常topic:{},messageId:{}",
                            mqttPublishMessage.variableHeader().topicName(), mqttPublishMessage.variableHeader().packetId());
                    e.printStackTrace();
                }
                mqttHandler.onMessage(channelHandlerContext, HandleMessageVo.builder()
                        .messageId(mqttPublishMessage.variableHeader().packetId())
                        .topic(mqttPublishMessage.variableHeader().topicName())
                        .body(payload)
                        .build());

                //发送回复消息
//                MqttPubAckMessage mqttPubAckMessage = MqttUtil.pubAckMessage(mqttPublishMessage.variableHeader().packetId());
//                channelHandlerContext.channel().writeAndFlush(mqttPubAckMessage);
                break;
            case CONNACK:
                log.info("++++++++++++++++++++++++++CONNACK++++++++++++++++++++++++++++++++");
                break;
            case DISCONNECT:
                log.info("++++++++++++++++++++++++++DISCONNECT++++++++++++++++++++++++++++++++");
                break;
            case SUBACK:
                log.info("++++++++++++++++++++++++++SUBACK++++++++++++++++++++++++++++++++");
                break;
            case UNSUBACK:
                log.info("++++++++++++++++++++++++++UNSUBACK++++++++++++++++++++++++++++++++");
                break;
            case PUBACK:
                log.info("++++++++++++++++++++++++++PUBACK++++++++++++++++++++++++++++++++");
                break;
            case PUBREC:
                log.info("++++++++++++++++++++++++++PUBREC++++++++++++++++++++++++++++++++");
                break;
            case PUBREL:
                log.info("++++++++++++++++++++++++++PUBREL++++++++++++++++++++++++++++++++");
                break;
            case PUBCOMP:
                log.info("++++++++++++++++++++++++++PUBCOMP++++++++++++++++++++++++++++++++");
                break;
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
