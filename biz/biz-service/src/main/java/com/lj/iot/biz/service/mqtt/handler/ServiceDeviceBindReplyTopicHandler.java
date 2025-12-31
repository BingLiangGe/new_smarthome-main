package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class ServiceDeviceBindReplyTopicHandler extends AbstractTopicHandler {

    public ServiceDeviceBindReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.SERVICE_DEVICE_BIND_REPLY);
    }

    private Integer ZERO = Integer.parseInt("0");
    @Autowired
    private BizUserDeviceService bizUserDeviceService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private BizWsPublishService bizWsPublishService;

    /**
     * 互联设备绑定响应
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "code": 0, //0:成功  -1:失败
     * "data": { //属性对象
     * "groupId": "232435325", //属性键值对
     * "deviceList": [{"deviceId":"eewq"
     *                 "properties":"powerstate"
     * }],
     * ...
     * }
     * }
     */
    @Override
    public void handle(HandleMessage message) {
        UserDevice userDevice = userDeviceService.getById(message.getTopicDeviceId());

        JSONObject body = message.getBody();
        if (ZERO.compareTo(body.getInteger("code")) != 0) {
            log.error("ServiceDeviceBindReplyTopicHandler.handle:互联设备绑定失败");
            bizWsPublishService.publish(WsResultVo.FAILURE(userDevice.getUserId(),
                    userDevice.getHomeId(),
                    RedisTopicConstant.TOPIC_DEVICE_BIND,
                    body.get("data")
            ));
            return;
        }
        bizUserDeviceService.updateBindDevice(userDevice,message.getBody().get("data"));
        bizWsPublishService.publish(WsResultVo.SUCCESS(userDevice.getUserId(),
                userDevice.getHomeId(),
                RedisTopicConstant.TOPIC_DEVICE_BIND,
                body.get("data")
        ));
    }

}
