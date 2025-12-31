package com.lj.iot.common.device.mqtt;


import cn.hutool.extra.spring.SpringUtil;
import com.lj.iot.common.device.mqtt.handler.DefaultChannelHandler;
import com.lj.iot.common.device.mqtt.properties.MqttClientProperties;
import com.lj.iot.common.device.mqtt.utils.MqttUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MQTT {
    private static MQTT mqttClient;
    private Bootstrap bootstrap;

    private Channel channel;

    private EventLoopGroup eventLoopGroup;

    //private DefaultEventExecutorGroup executors;

    private MqttClientProperties properties;

    public MQTT() {
    }

    public MQTT(MqttClientProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void connect() {
        mqttClient = this;
        mqttClient.bootstrap = new Bootstrap();

        if (mqttClient.channel != null) {
            mqttClient.channel.close();
        }

        if (mqttClient.eventLoopGroup != null) {
            mqttClient.eventLoopGroup.shutdownGracefully();
        }
        /*if (mqttClient.executors != null) {
            mqttClient.executors.shutdownGracefully();
        }*/
        mqttClient.eventLoopGroup = new NioEventLoopGroup(2);
        //mqttClient.executors = new DefaultEventExecutorGroup(Math.max(1, NettyRuntime.availableProcessors() * 2));

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast("decoder", new MqttDecoder())//解码
                                .addLast("encoder", MqttEncoder.INSTANCE)//编码
                                .addLast("idleState", new IdleStateHandler(0, 0, properties.getPingInterval()))
                                .addLast("handler", SpringUtil.getBean(DefaultChannelHandler.class));
                    }
                })
                .connect(properties.getHost(), properties.getPort())
                .addListener((ChannelFutureListener) future -> {

                    if (future.isSuccess()) {
                        this.channel = future.channel();

                        log.info("=================================MQTT连接======================================" + properties.getClientId());
                        MqttConnectMessage mqttConnectMessage = MqttUtil.connectMessage(properties);
                        future.channel().writeAndFlush(mqttConnectMessage);

                        log.info("================================订阅默认主题=====================================");
                        MqttSubscribeMessage mqttSubscribeMessage = MqttUtil.subscribeMessage(properties.getDefaultTopic());
                        future.channel().writeAndFlush(mqttSubscribeMessage);

                        for (String topic : properties.getDefaultTopic()) {
                            log.info("================================" + topic + "=====================================");
                        }

                        future.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> {
                            log.info("==================================断线重连=================================");
                            mqttClient.eventLoopGroup.next().schedule(this::connect, properties.getRetryInterval(), TimeUnit.SECONDS);
                        });
                    }
                });
    }

    @PreDestroy
    public void destroyMethod() {
        if (mqttClient.eventLoopGroup != null) {
            mqttClient.eventLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 推送消息
     *
     * @param topic
     * @param payload
     */
    public static void publish(String topic, String payload) {
        publish(topic, payload, MqttQoS.AT_LEAST_ONCE, false);
    }

    /**
     * 推送消息
     *
     * @param topic
     * @param payload
     * @param qoS
     * @param retain
     */
    public static void publish(String topic, String payload, MqttQoS qoS, Boolean retain) {
        publish(topic, Unpooled.buffer().writeBytes(Optional.ofNullable(payload).orElse("").getBytes(StandardCharsets.UTF_8)), qoS, retain);
    }

    /**
     * 推送消息
     *
     * @param topic
     * @param payload
     * @param qoS
     * @param retain
     */
    public static void publish(String topic, ByteBuf payload, MqttQoS qoS, Boolean retain) {
        MqttPublishMessage mqttPublishMessage = MqttUtil.publishMessage(topic, payload, qoS, retain);
        mqttClient.channel.writeAndFlush(mqttPublishMessage);
    }
}
