package com.lj.iot.api.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.common.base.constant.CodeConstant;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.redis.service.ICacheService;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author mz
 * @Date 2022/8/3
 * @since 1.0.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WS extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    private ICacheService cacheService;

    private static final ConcurrentMap<String, Set<Channel>> appUser2Channel = PlatformDependent.newConcurrentHashMap();
    private static final ConcurrentMap<Channel, String> appChannel2User = PlatformDependent.newConcurrentHashMap();

    private static final ConcurrentMap<String, Set<Channel>> hotelUser2Channel = PlatformDependent.newConcurrentHashMap();
    private static final ConcurrentMap<Channel, String> hotelChannel2User = PlatformDependent.newConcurrentHashMap();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //System.out.println("与客户端建立连接，通道开启！");
        log.info("与客户端建立连接，通道开启！");
        super.channelActive(ctx);
    }

    //客户端与服务器关闭连接的时候触发，
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String userId = appChannel2User.get(ctx.channel());
        Optional.ofNullable(userId).ifPresent(id -> {
            Set<Channel> channels = appUser2Channel.get(id);
            if (channels != null) {
                channels.remove(ctx.channel());
                if (channels.isEmpty()) {
                    appUser2Channel.remove(id);
                }
            }
            appChannel2User.remove(ctx.channel());
        });

        userId = hotelChannel2User.get(ctx.channel());
        Optional.ofNullable(userId).ifPresent(id -> {
            Set<Channel> channels = hotelUser2Channel.get(id);
            if (channels != null) {
                channels.remove(ctx.channel());
                if (channels.isEmpty()) {
                    hotelUser2Channel.remove(id);
                }
            }
            hotelChannel2User.remove(ctx.channel());
        });

        super.channelInactive(ctx);
    }

    //服务器接受客户端的数据信息，
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        if ("ping".equals(msg.text())) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame("pong"));
            return;
        }
        //判断用户是否加进来了


        try {
            JSONObject jsonObject = JSON.parseObject(msg.text());
            String token = (String) jsonObject.get("Authorization");
            UserDto userDto = cacheService.get("app" + RedisConstant.SESSION_TOKEN_2_USER + token);


            //app
            if (userDto != null) {
                //绑定用户
                Set<Channel> channels = appUser2Channel.computeIfAbsent(userDto.getUId(), k -> new HashSet<>());
                channels.add(ctx.channel());

                appChannel2User.put(ctx.channel(), userDto.getUId());
                CommonResultVo<Object> resultVo = CommonResultVo.SUCCESS();
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(resultVo)));
                return;
            }

            //酒店
            userDto = cacheService.get("hotel" + RedisConstant.SESSION_TOKEN_2_USER + token);
            if (userDto == null) {
                CommonResultVo<Object> resultVo = CommonResultVo.INSTANCE(CodeConstant.LOGIN_INFO_NOT_EXIST, "登录信息不存在，或已过期");
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(resultVo)));
                return;
            }

            //绑定用户
            Set<Channel> channels = hotelUser2Channel.computeIfAbsent(userDto.getUId(), k -> new HashSet<>());
            channels.add(ctx.channel());
            hotelChannel2User.put(ctx.channel(), userDto.getUId());
            CommonResultVo<Object> resultVo = CommonResultVo.SUCCESS();
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(resultVo)));
        } catch (Exception e) {
            HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            ctx.channel().writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
            log.info("websocket:error={}", e);
            e.printStackTrace();
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
        //超过心跳断开连接
        if (evt == IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT) {
            ctx.channel().close();
        }
    }

    /**
     * 推送消息
     *
     * @param userId
     * @param message
     */
    public static void appSend(String userId, String message) {
        log.info("ws.appSend=====message={},userId={}", message, userId);
        Optional.ofNullable(appUser2Channel.get(userId)).ifPresent(channels -> {
            for (Channel channel : channels) {
                channel.writeAndFlush(new TextWebSocketFrame(message));
            }
        });
    }

    /**
     * 酒店推送消息
     *
     * @param userId
     * @param message
     */
    public static void hotelSend(String userId, String message) {
        try {
            JSONObject respJson = JSONObject.parseObject(message);


            if (respJson.get("channel") != null){

                if ("TOPIC_CHANNEL_DEVICE_SCAN".equals(respJson.get("channel"))){
                    log.info("hotelSend_userId={},meessage={}", userId, message);
                }
            }
        } catch (Exception e) {
            log.info("解析出错>..");
        }
        Optional.ofNullable(hotelUser2Channel.get(userId)).ifPresent(channels -> {
            for (Channel channel : channels) {
                channel.writeAndFlush(new TextWebSocketFrame(message));
            }
        });
    }

    /**
     * 推送消息
     *
     * @param userIds
     * @param message
     */
    public static void appSend(List<String> userIds, String message) {
        if (userIds != null) {
            log.info("appendUserIds_lengsh={}", userIds.size());
        }
        userIds.forEach(userId -> appSend(userId, message));
    }

    /**
     * 酒店推送消息
     *
     * @param userIds
     * @param message
     */
    public static void hotelSend(List<String> userIds, String message) {
        userIds.forEach(userId -> hotelSend(userId, message));
    }
}
