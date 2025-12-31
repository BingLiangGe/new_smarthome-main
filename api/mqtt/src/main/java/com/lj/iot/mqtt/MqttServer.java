package com.lj.iot.mqtt;

import com.lj.iot.common.util.IPUtils;
import com.lj.iot.mqtt.hander.DefaultChannelHandlerAdapter;
import com.lj.iot.mqtt.properties.CustomMqttProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Component
public class MqttServer {

    @Resource
    private DefaultChannelHandlerAdapter defaultChannelHandlerAdapter;

    @Resource
    private CustomMqttProperties properties;

    @PostConstruct
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)     //等待连接队列
                    .childOption(ChannelOption.TCP_NODELAY, true)   //默认数据包，要等到一定大小才发送，设置为true，只要一个完整消息就发送
                    .childOption(ChannelOption.SO_REUSEADDR, true)   //一个进程结束后端口释放有一段时间，设置为true时，可以直接占用
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  //两个小时没有收发信息，服务端会发起一个心跳
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 这个地方注意，如果客户端发送请求体超过此设置值，会抛异常
                            pipeline.addLast(new MqttDecoder(1024 * 1024));
                            // 加载MQTT编解码协议
                            pipeline.addLast(MqttEncoder.INSTANCE);
                            //心跳机制
                            pipeline.addLast(new IdleStateHandler(properties.getReaderIdleTimeSeconds(),
                                    properties.getWriterIdleTimeSeconds(),
                                    properties.getAllIdleTimeSeconds()));
                            // 业务逻辑对象
                            pipeline.addLast(defaultChannelHandlerAdapter);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(properties.getPort()).sync().addListener(future -> {
                if (future.isSuccess()) {
                    log.info("服务端启动成功【" + IPUtils.getHost() + ":" + properties.getPort() + "】");
                } else {
                    log.error("服务端启动失败【" + IPUtils.getHost() + ":" + properties.getPort() + "】");
                }
            });
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            try {
                bossGroup.shutdownGracefully().sync();
                workerGroup.shutdownGracefully().sync();
            } catch (InterruptedException interruptedException) {
                log.error("服务端关闭资源失败【" + IPUtils.getHost() + ":" + properties.getPort() + "】");
            }
            log.error("mqttServer启动失败:", e);
        }
    }
}

