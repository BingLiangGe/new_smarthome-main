package com.lj.iot.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class KafkaServicePropertiesSetReplyTopicHandler extends AbstractTopicHandler {

    public KafkaServicePropertiesSetReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.SERVICE_PROPERTIES_SET_REPLY);
    }

    private final Integer ZERO = Integer.parseInt("0");

    @Resource
    private IUserDeviceService userDeviceService;

    @Resource
    @Lazy
    private BizWsPublishService bizWsPublishService;

    @Resource
    private BizUserDeviceService bizUserDeviceService;

    /**
     * 设置设备属性响应
     * <p>
     *     {
     *     "id": "123", //消息ID
     *     "time": 1524448722000, //时间
     *     "data": { //属性对象
     *         "productId":"56789",
     *         "deviceId":"123456",
     *         "properties":[{
     *             "identifier":"powerstate_1",
     *             "value":"0"
     *         },
     *         ...
     *         ]//属性键
     *     }
     * }
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {

        Map<String, Object> map = (Map<String, Object>) message.getBody().get("data");

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId((String) map.get("deviceId"))
                .masterDeviceId(message.getTopicDeviceId())
                .build()));

        ValidUtils.isNullThrow(userDevice, CommonCodeEnum.SUB_NOT_EXIST.getCode(), "设备不存在");


        JSONObject body = message.getBody();
        if (ZERO.compareTo(body.getInteger("code")) != 0) {
            log.error("PropertiesSetReplyTopicHandler.handle:设置属性值失败:{}", JSON.toJSONString(message));
            bizWsPublishService.publish(WsResultVo.FAILURE(userDevice.getUserId(),
                    userDevice.getHomeId(),
                    RedisTopicConstant.TOPIC_CHANNEL_DEVICE_PROPERTIES_SET,
                    body.get("data")
            ));
            return;
        }

        bizUserDeviceService.saveTogetherProperties(userDevice, map);
        bizWsPublishService.publishAllMemberByHomeId(
                RedisTopicConstant.TOPIC_CHANNEL_DEVICE_PROPERTIES_SET,
                userDevice.getHomeId(), RedisTopicConstant.TOPIC_CHANNEL_DEVICE_PROPERTIES_SET);
    }
}
