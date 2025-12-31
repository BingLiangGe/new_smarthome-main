package com.lj.iot.common.mqtt.client.core;

import cn.hutool.extra.spring.SpringUtil;
import com.lj.iot.common.mqtt.client.properties.MqttClientProperties;
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

/**
 * @author mz
 * @Date 2022/7/26
 * @since 1.0.0
 */
@Slf4j
public class MQTT {

    private static MQTT client;

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

        client = this;
        client.bootstrap = new Bootstrap();

        if (client.channel != null) {
            client.channel.close();
        }

        if (client.eventLoopGroup != null) {
            client.eventLoopGroup.shutdownGracefully();
        }
        /*if (client.executors != null) {
            client.executors.shutdownGracefully();
        }*/
        client.eventLoopGroup = new NioEventLoopGroup(2);
        //client.executors = new DefaultEventExecutorGroup(Math.max(1, NettyRuntime.availableProcessors() * 2));

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

                        log.info("=================================MQTT连接======================================"+properties.getClientId());
                        MqttConnectMessage mqttConnectMessage = MqttUtil.connectMessage(properties);
                        future.channel().writeAndFlush(mqttConnectMessage);

                        log.info("================================订阅默认主题=====================================");
                        MqttSubscribeMessage mqttSubscribeMessage = MqttUtil.subscribeMessage(properties.getDefaultTopic());
                        future.channel().writeAndFlush(mqttSubscribeMessage);

                        for (String topic:properties.getDefaultTopic()
                             ) {
                            log.info("================================"+topic+"=====================================");
                        }

                        future.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> {
                            log.info("==================================断线重连=================================");
                            client.eventLoopGroup.next().schedule(this::connect, properties.getRetryInterval(), TimeUnit.SECONDS);
                        });
                    }
                });
    }

    /**
     * 订阅主题
     *
     * @param topic
     */
    public static void subscription(String topic) {
        MqttSubscribeMessage mqttSubscribeMessage = MqttUtil.subscribeMessage(topic);
        client.channel.writeAndFlush(mqttSubscribeMessage);
    }

    /**
     * 推送消息
     *
     * @param topic
     * @param payload
     */
    public static void publish(String topic, String payload) {
        publish(topic, payload, MqttQoS.AT_MOST_ONCE, false);
    }

    /**
     * 推送消息
     *
     * @param topic
     * @param payload
     * @param qoS
     */
    public static void publish(String topic, String payload, MqttQoS qoS) {
        publish(topic, payload, qoS, false);

    }

    /**
     * 推送消息
     *
     * @param topic
     * @param payload
     * @param retain
     */
    public static void publish(String topic, String payload, Boolean retain) {
        publish(topic, payload, MqttQoS.AT_MOST_ONCE, retain);

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
        client.channel.writeAndFlush(mqttPublishMessage);
    }

    @PreDestroy
    public void destroyMethod() {
        if (client.eventLoopGroup != null) {
            client.eventLoopGroup.shutdownGracefully();
        }
    }
}
