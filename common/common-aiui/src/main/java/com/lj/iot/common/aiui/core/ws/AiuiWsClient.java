package com.lj.iot.common.aiui.core.ws;

import com.alibaba.fastjson.JSON;
import com.lj.iot.common.aiui.core.dto.AIUIWsDto;
import com.lj.iot.common.aiui.core.properties.AiuiProperties;
import com.lj.iot.common.aiui.core.service.ISkillWsAckProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.annotation.PreDestroy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author mz
 * @Date 2022/8/10
 * @since 1.0.0
 */
@Slf4j
public class AiuiWsClient {

    public static Bootstrap bootstrap = null;
    public static EventLoopGroup group = new NioEventLoopGroup();
    public static AiuiProperties properties;

    public AiuiWsClient(AiuiProperties properties) {
        AiuiWsClient.properties = properties;

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(8192))
                                .addLast(WebSocketClientCompressionHandler.INSTANCE);
                    }
                });
    }

    @PreDestroy
    public void destroyMethod() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    public static void uploadFile(AIUIWsDto aiuiWsDto, byte[] data, ISkillWsAckProcessor skillWsAckProcessor) {
        URI uri = URI.create(properties.getWsUrl() + buildHandShakeParams(properties, aiuiWsDto));
        bootstrap.connect(uri.getHost(), uri.getPort() == -1 ? 80 : uri.getPort()).addListener(
                (ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        AIUISocketUploadFileHandler handler =
                                new AIUISocketUploadFileHandler(data, WebSocketClientHandshakerFactory.newHandshaker(
                                        uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()), skillWsAckProcessor);
                        future.channel().pipeline().addLast(handler);
                    }
                }
        );
    }

    private static String buildHandShakeParams(AiuiProperties properties, AIUIWsDto aiuiWsDto) {
        Map<String, Object> custom = Map.of("userId", aiuiWsDto.getUserId(), "deviceId", aiuiWsDto.getDeviceId());

        final Map<String, Object> params = Map.of(
                "result_level", "plain",
                "data_type", "audio",
                "aue", "raw",
                "sample_rate", "16000",
                "scene", "main_box",
                "vad_info", "end",
                "UserParams", JSON.toJSONString(custom),
                "auth_id", aiuiWsDto.getUserId(),
                "pers_param", JSON.toJSONString(Map.of("auth_id", aiuiWsDto.getUserId(), "custom", "hello"))
        );
        final String strParams = JSON.toJSONString(params);

        final String base64Params = Base64.encodeBase64String(strParams.getBytes(StandardCharsets.UTF_8));
        final String curTime = System.currentTimeMillis() / 1000L + "";
        final String signType = "sha256";
        final String originStr = properties.getAppKey() + curTime + base64Params;
        final String checkSum = DigestUtils.sha256Hex(originStr);

        return "?appid=" + properties.getAppId() + "&checksum=" + checkSum + "&curtime=" + curTime + "&param=" + base64Params + "&signtype=" + signType;
    }
}
