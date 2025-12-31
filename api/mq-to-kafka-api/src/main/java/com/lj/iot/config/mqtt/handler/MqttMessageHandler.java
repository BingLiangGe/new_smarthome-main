package com.lj.iot.config.mqtt.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lj.iot.common.device.enums.DeviceSubTopicEnum;
import com.lj.iot.common.device.mqtt.handler.MqttHandler;
import com.lj.iot.common.device.mqtt.properties.HandleMessageVo;
import com.lj.iot.config.mqtt.IAsyncService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 消息处理实现类
 */
@Slf4j
@Service
public class MqttMessageHandler implements MqttHandler {
    @Resource
    private IAsyncService asyncService;

    DefaultEventExecutorGroup executorGroup = new DefaultEventExecutorGroup(Math.max(1, NettyRuntime.availableProcessors() * 2));

    @Override
    public void onMessage(ChannelHandlerContext channelHandlerContext, HandleMessageVo message) {
        executorGroup.next().execute(new Runnable() {
            @Override
            public void run() {
//                log.info("MqttMessageHandler.onMessage====={}", JSONObject.toJSONString(message));
                try {
                    String topic = message.getTopic();

                    //emqx系统topic   $SYS/brokers/emqx@127.0.0.1/clients/1559715685333352449/disconnected
                    if (topic.contains("$SYS")) {
                        int index = topic.lastIndexOf("/");
                        String action = topic.substring(index + 1);
                        topic = "$SYS/brokers/" + action;
                        message.setTopic(topic);
                        String body = message.getBody();
                        if(JSON.isValid(body)){
                            JSONObject object = JSONObject.parseObject(body);
                            message.setTopicDeviceId(object.getString("clientid"));
                        }
                    } else if (topic.contains("sys")) {
                        int index = topic.indexOf("/", 4);
                        String productId = topic.substring(4, index);
                        topic = topic.substring(index + 1);
                        index = topic.indexOf("/");
                        String deviceId = topic.substring(0, index);
                        topic = topic.substring(index + 1);
                        message.setTopic(topic);
                        message.setTopicProductId(productId);
                        message.setTopicDeviceId(deviceId);
                    }

                    DeviceSubTopicEnum deviceSubTopicEnum = DeviceSubTopicEnum.parse(topic);
                    if (deviceSubTopicEnum == null) {
                        return;
                    }
                    //发送kafka
                    asyncService.send2Kafka(channelHandlerContext, getKafkaOut(deviceSubTopicEnum), message);
                } catch (Exception e) {
                    log.error("MqttMessageHandler.onMessage", e);
                }
            }
        });
    }

    private String getKafkaOut(DeviceSubTopicEnum deviceSubTopicEnum) {
        return deviceSubTopicEnum.name().toLowerCase();
    }
}
