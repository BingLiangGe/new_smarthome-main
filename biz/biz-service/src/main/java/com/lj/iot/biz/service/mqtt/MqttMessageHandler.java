package com.lj.iot.biz.service.mqtt;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.lj.iot.biz.db.smart.entity.OperationLog;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IHotelUserAccountService;
import com.lj.iot.biz.db.smart.service.IOperationLogService;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MqttHandler;
import com.lj.iot.common.redis.service.ICacheService;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
@Slf4j
@Service
public class MqttMessageHandler implements MqttHandler {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private MqttPushService mqttPushService;

    @Autowired
    private IOperationLogService operationLogService;

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;

    DefaultEventExecutorGroup executorGroup = new DefaultEventExecutorGroup(Math.max(1, NettyRuntime.availableProcessors() * 2));

    @Override
    public void onMessage(HandleMessage message) {

        executorGroup.next().execute(new Runnable() {
            @Override
            public void run() {
                log.info("MqttMessageHandler.onMessage====={}", JSON.toJSONString(message));
                try {
                    String topic = message.getTopic();

                    //emqx系统topic   $SYS/brokers/emqx@127.0.0.1/clients/1559715685333352449/disconnected
                    if (topic.contains("$SYS")) {
                        int index = topic.lastIndexOf("/");
                        String action = topic.substring(index + 1);
                        topic = "$SYS/brokers/" + action;
                        message.setTopic(topic);
                        message.setTopicDeviceId(message.getBody().getString("clientid"));
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

                    UserDevice userDevice = userDeviceService.getById(message.getTopicDeviceId());
                    if (userDevice == null) {
                        if (message.getTopicProductId() != null) {
                            log.info("MqttMessageHandler.onMessage====={}", "设备不存在");
                            mqttPushService.noExistDevice(message.getTopicProductId(), message.getTopicDeviceId());
                        }
                        return;
                    }

                    /*operationLogService.save(OperationLog.builder()
                            .action(new Byte("1"))
                            .deviceId(userDevice.getDeviceId())
                            .productId(userDevice.getProductId())
                            .productType(userDevice.getProductType())
                            .customName(userDevice.getCustomName())
                            .userId(userDevice.getUserId())
                            .masterDeviceId(userDevice.getMasterDeviceId())
                            .signalType(userDevice.getSignalType())
                            .params(JSON.toJSONString(message.getBody()))
                            .remark(topic)
                            .build());*/

                    SubTopicEnum subTopicEnum = SubTopicEnum.parse(topic);
                    Map<String, ITopicHandler> handlers = SpringUtil.getBeansOfType(ITopicHandler.class);
                    for (Map.Entry<String, ITopicHandler> entry : handlers.entrySet()) {
                        final ITopicHandler handler = entry.getValue();
                        if (handler.isSupport(subTopicEnum)) {
                            handler.entrance(message);
                        }
                    }
                } catch (Exception e) {
                    log.error("MqttMessageHandler.onMessage", e);
                }
            }
        });
    }
}
