package com.lj.iot.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class KafkaEventTopologyDeleteTopicHandler extends AbstractTopicHandler {

    public KafkaEventTopologyDeleteTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_TOPOLOGY_DELETE);
    }

    @Resource
    private IUserDeviceService userDeviceService;

    @Resource
    @Lazy
    private BizWsPublishService bizWsPublishService;

    /**
     * 主控设备topology删除上报
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": {
     * "productId": "56789", //产品ID
     * "deviceId": "123456", //设备ID
     * }
     * }
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {

        //主控设备
        UserDevice master = userDeviceService.getById(message.getTopicDeviceId());
        JSONObject params = message.getBody().getJSONObject("data");
        String deviceId = params.getString("deviceId");

        ValidUtils.isFalseThrow(StringUtils.hasText(deviceId), CommonCodeEnum.SUB_NOT_EXIST.getCode(), "设备不存在");

        UserDevice device = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(deviceId)
                .masterDeviceId(master.getDeviceId())
                .build()));
        ValidUtils.isNullThrow(device, CommonCodeEnum.SUB_NOT_EXIST.getCode(), "设备不存在");


        //userDeviceService.removeById(device.getDeviceId());
        //删掉  提示异常
        //不删掉更新
        //需要把PO 和 P1等全部更新
        List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .physicalDeviceId(device.getPhysicalDeviceId())
                .build()));
        for (UserDevice userDevice : list) {
            userDevice.setIsDel(true);
            userDevice.setStatus(false);
            userDeviceService.updateById(userDevice);

            //websocket推送
            bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_DELETE, userDevice.getHomeId(),
                    userDevice.getDeviceId());
        }
    }
}
