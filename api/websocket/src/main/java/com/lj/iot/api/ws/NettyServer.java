package com.lj.iot.api.ws;

import com.lj.iot.api.ws.properties.WsProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author mz
 * @Date 2022/8/3
 * @since 1.0.0
 */
@Slf4j
@Component
public class NettyServer {

    @Autowired
    private WS ws;

    @Autowired
    private WsProperties properties;

    private static EventLoopGroup bossGroup = null;
    private static EventLoopGroup workerGroup = null;

    @PostConstruct
    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.group(workerGroup, bossGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(properties.getPort())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec())
                                    .addLast(new ChunkedWriteHandler())
                                    .addLast(new HttpObjectAggregator(8192))
                                    .addLast(new WebSocketServerProtocolHandler("/ws", "WebSocket", true, 65536 * 10))
                                    .addLast(new IdleStateHandler(0, 0, properties.getKeepAliveInterval()))
                                    .addLast("bizHandler", ws);
                        }
                    });

            // 服务器异步创建绑
            ChannelFuture cf = bootstrap.bind(properties.getPort()).sync();
            cf.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("=================================WS连接======================================");
                    future.channel().closeFuture();
                }
            });
        } catch (
                Exception e) {
            destroyMethod();
        }

    }

    @PreDestroy
    public void destroyMethod() throws InterruptedException {
        if (bossGroup != null && bossGroup.isShutdown()) {
            bossGroup.shutdownGracefully().sync(); // 释放线程池资源
        }
        if (workerGroup != null && workerGroup.isShutdown()) {
            workerGroup.shutdownGracefully().sync(); // 释放线程池资源
        }
    }
}
