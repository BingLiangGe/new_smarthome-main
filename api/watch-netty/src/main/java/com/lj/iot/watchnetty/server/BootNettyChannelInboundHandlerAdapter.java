package com.lj.iot.watchnetty.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.fegin.websocket.WsFeignClient;
import com.lj.iot.watchnetty.handle.TcpHandle;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * I/O数据读写处理类
 * 蚂蚁舞
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class BootNettyChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {


    // 要注入的组件
    private static IUserDeviceService userDeviceService;

    private static final ConcurrentMap<Channel, String> deviceIdChannel = PlatformDependent.newConcurrentHashMap();

    private static final ConcurrentMap<String, Set<Channel>> deviceChannel = PlatformDependent.newConcurrentHashMap();

    private static WsFeignClient wsFeignClient;

    @Autowired
    public void setSendConfig(IUserDeviceService userDeviceService, WsFeignClient wsFeignClient) {
        BootNettyChannelInboundHandlerAdapter.userDeviceService = userDeviceService;
        BootNettyChannelInboundHandlerAdapter.wsFeignClient = wsFeignClient;
    }

    /**
     * 发送消息
     *
     * @param deviceId
     * @param msg
     */
    public static void sendMsg(String deviceId, String msg) {
        log.info("tcp.sendMsg====={}", JSON.toJSONString("ws.appSend=====" + "userId:" + deviceId + "message:" + msg));
        Optional.ofNullable(deviceChannel.get(deviceId)).ifPresent(channels -> {
            for (Channel channel : channels) {
                channel.writeAndFlush(Unpooled.buffer().writeBytes((msg).getBytes()));
            }
        });
    }

    /**
     * 注册时执行
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        log.info("--握手成功--={}", ctx.channel().id().toString());
    }

    /**
     * 离线时执行
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        log.info("--断开--", ctx.channel().id().toString());
    }

    /**
     * 从客户端收到新的数据时，这个方法会在收到消息时被调用
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        log.info("===============watchNetty_收到消息msg={}", msg);

        String channelId = ctx.channel().id().toString();
        if (msg == null) {
            log.info("收到空消息chanelId={}", channelId);
            return;
        }

        Map<String, String> dataMap = null;

        try {
            dataMap = msgCase((String) msg);
        } catch (Exception e) {
            log.info("不支持此内容={},error={}", msg, e.getMessage());
            return;
        }


        // 无数据
        if (dataMap == null) {
            return;
        }

        if ("ICCID".equals(dataMap.get("type"))) {
            String respData = "[DW*" + dataMap.get("deviceId") + "*0005*ICCID]";
            ctx.writeAndFlush(Unpooled.buffer().writeBytes((respData).getBytes()));
            return;
        }

        TcpHandle topHanle = null;
        try {
            topHanle = SpringUtil.getBean("tcpHandle_" + dataMap.get("type"), TcpHandle.class);
        } catch (Exception e) {
            log.info("不支持此内容,type={},msg={}", dataMap.get("type"), msg);
            return;
        }

        try {

            Set<Channel> channels = deviceChannel.computeIfAbsent(dataMap.get("deviceId"), k -> new HashSet<>());
            channels.add(ctx.channel());

            deviceIdChannel.put(ctx.channel(), dataMap.get("deviceId"));

            UserDevice userDevice = userDeviceService.getById(dataMap.get("deviceId"));

            // 设备未绑定
            if (userDevice == null) {
                log.info("设备未绑定deviceId={}", dataMap.get("deviceId"));
                return;
            }

            String respData = topHanle.handle(userDevice, dataMap);

            // 有些文档表明无需回复
            if (respData != null) {
                ctx.writeAndFlush(Unpooled.buffer().writeBytes((respData).getBytes()));
            }
            // netty的编码已经指定，因此可以不需要再次确认编码
            // ctx.writeAndFlush(Unpooled.buffer().writeBytes(channelId.getBytes(CharsetUtil.UTF_8)));
        } catch (Exception e) {
            log.info("收到消息处理异常msg={},exception={}", msg, e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, String> msgCaseBloodMany(String msg) {
        String datas[] = msg.split("\\[");

        Map<String, String> dataMap = new HashMap<>();

        for (String data : datas
        ) {

            if ("".equals(data)) {
                continue;
            } else if (data.contains("WG")) {
                continue;
            }

            StringBuffer sb = new StringBuffer();
            sb.append("[").insert(1, data);
            Map<String, String> respMap = msgCaseJk(sb.toString());

            dataMap.put("deviceId", respMap.get("deviceId"));

            if (respMap.get("data").contains("blood")) {
                dataMap.put(respMap.get("data").split(",")[0], respMap.get("data").split(",")[1] + "," + respMap.get("data").split(",")[2]);
            } else {
                dataMap.put(respMap.get("data").split(",")[0], respMap.get("data").split(",")[1]);
            }
        }

        dataMap.put("type", "bloodMany");
        return dataMap;
    }

    public Map<String, String> msgCaseJk(String msg) {
        msg = msg.substring(1, msg.length() - 1);
        String[] datas = msg.split("DW*")[1].split("\\*");

        Map<String, String> topicMap = new HashMap<>();

        topicMap.put("deviceId", datas[1]);
        topicMap.put("date", datas[3].split(",")[1]);
        topicMap.put("number", datas[2]);
        topicMap.put("data", datas[3]);
        topicMap.put("type", datas[3].split(",")[0]);
        return topicMap;
    }


    private Map<String, String> msgCase(String msg) {

        // 健康数据组处理
        if (msg.contains("*heart") || msg.contains("*blood") || msg.contains("*oxygen")) {
            return msgCaseBloodMany(msg);
        }


        msg = msg.substring(1, msg.length() - 1);
        if (msg.contains("*TKQ") || msg.contains("*JMEMBERS") || msg.contains("*APPDOWNLOADURLREQ")) {
            log.info("无效消息类型,msg={}", msg);
            return null;
        }

        // cdd7a29063fcaf9e6ff302e372e1ac04DW*358800002343854*0002*CR

        if (msg.contains("*bldstart") || msg.contains("*oxstart") || msg.contains("*wdstart") || msg.contains("*hrtstart") ||
                msg.contains("*CR") || msg.contains("*oxstart") || msg.contains("*UPLOAD") || msg.contains("*REMOVE")) {

            String deviceId = msg.split("DW\\*")[1].split("\\*")[0];

            JSONObject respJson = new JSONObject();
            respJson.put("deviceId", deviceId);
            respJson.put("type", "bind_reply");

            UserDevice userDevice = userDeviceService.getById(deviceId);

            if (userDevice != null) {
                log.info("发送socket 通知app,绑定上报信息,deviceId={},wsFeignClient={}", deviceId, wsFeignClient);

                List<String> userIds = Lists.newArrayList();
                userIds.add(userDevice.getUserId());
                wsFeignClient.appSend(WsResultVo.FAILURE(userIds, null, RedisTopicConstant.TOPIC_CHANNEL_WATCH_BIN, null, respJson.toJSONString()));
            }

            return null;
        }

        // 特殊处理
        if (msg.contains("*UD")) {

            String[] datas = msg.split("DW\\*")[1].split("\\*");
            String deviceId = datas[0];
            String dataArray[] = datas[2].split(",");

            String latitude = null;
            String longitude = null;
            String dataType = "NOT";
            String wifiMac = null;
            Map<String, String> topicMap = new HashMap<>();

            for (int i = 0; i < dataArray.length; i++) {
                if ("V".equals(dataArray[i])) {
                    latitude = dataArray[i + 1];
                    dataType = dataArray[i];

                    if (msg.contains(":")) {
                        wifiMac = dataArray[38];
                    }
                    topicMap.put("lbs", dataArray[19] + "," + dataArray[20] + "," + dataArray[21] + "," + dataArray[22] + "," + dataArray[23]);
                } else if ("A".equals(dataArray[i])) {
                    latitude = dataArray[i + 1];
                    dataType = dataArray[i];
                } else if ("N".equals(dataArray[i])) {
                    longitude = dataArray[i + 1];
                }
            }


            topicMap.put("type", "UD");
            topicMap.put("deviceId", deviceId);
            topicMap.put("latitude", latitude);
            topicMap.put("longitude", longitude);
            topicMap.put("dataType", dataType);
            topicMap.put("wifiMac", wifiMac);

            return topicMap;
        }

        String test = msg.split("DW\\*")[1];

        String[] datas = msg.split("DW\\*")[1].split("\\*");

        Map<String, String> topicMap = new HashMap<>();

        // 特殊处理
        if (msg.contains("*ICCID")) {
            topicMap.put("type", "ICCID");
            topicMap.put("deviceId", datas[1]);

            return topicMap;
        }

        topicMap.put("deviceId", datas[0]);
        topicMap.put("number", datas[1]);
        topicMap.put("data", datas[2]);
        topicMap.put("type", datas[2].split(",")[0]);
        return topicMap;
    }

    /**
     * 从客户端收到新的数据、读取完成时调用
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws IOException {
        System.out.println("channelReadComplete");
        ctx.flush();
    }

    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws IOException {
        log.info("exceptionCaught");
        cause.printStackTrace();
        ctx.close();//抛出异常，断开与客户端的连接
    }

    /**
     * 客户端与服务端第一次建立连接时 执行
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception, IOException {
        super.channelActive(ctx);
        ctx.channel().read();
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        //此处不能使用ctx.close()，否则客户端始终无法与服务端建立连接
        log.info("首次握手数据={}", clientIp + ctx.name());
    }

    /**
     * 客户端与服务端 断连时 执行
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception, IOException {
        super.channelInactive(ctx);

        String deviceId = deviceIdChannel.get(ctx.channel());
        Optional.ofNullable(deviceId).ifPresent(id -> {
            Set<Channel> channels = deviceChannel.get(id);
            if (channels != null) {
                channels.remove(ctx.channel());
                if (channels.isEmpty()) {
                    deviceChannel.remove(id);
                }
            }
            deviceIdChannel.remove(ctx.channel());
        });

        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        log.info("断连={}", clientIp);
        ctx.close(); //断开连接时，必须关闭，否则造成资源浪费，并发量很大情况下可能造成宕机
    }

    /**
     * 服务端当read超时, 会调用这个方法
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception, IOException {
        super.userEventTriggered(ctx, evt);
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();

        ctx.close();//超时时断开连接
        log.info("超时断开={}", clientIp);
    }


}
