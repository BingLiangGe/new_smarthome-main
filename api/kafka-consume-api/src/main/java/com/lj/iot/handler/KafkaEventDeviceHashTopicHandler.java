package com.lj.iot.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.DeviceWsVo;
import com.lj.iot.biz.db.smart.entity.Device;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IDeviceService;
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

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 设备秘钥hash
 *
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class KafkaEventDeviceHashTopicHandler extends AbstractTopicHandler {

    public KafkaEventDeviceHashTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_DEVICE_HASH);
    }

    @Resource
    IDeviceService deviceService;

    @Resource
    private IUserDeviceService userDeviceService;

    @Resource
    @Lazy
    private BizWsPublishService bizWsPublishService;


    /**
     * 子设备在线状态
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "data": {
     * "productId":"56789",
     * "deviceId":"123456",
     * "line":true //true 在线   false  离线
     * }
     * }
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {

        JSONObject jsonObject = message.getBody().getJSONObject("data");
        Device device = deviceService.getById(jsonObject.getString("deviceId"));
        ValidUtils.isNullThrow(device, CommonCodeEnum.SUB_NOT_EXIST.getCode(), "设备不存在");

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(message.getTopicDeviceId())
                .build()));

        UserDevice eleUserDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(jsonObject.getString("deviceId"))
                .build()));
        if (Optional.ofNullable(eleUserDevice).isPresent()) {
            DeviceWsVo dw = Optional.ofNullable(userDeviceService.getDeviceWsData(jsonObject.getString("deviceId"))).orElse(DeviceWsVo.builder().deviceId(jsonObject.getString("deviceId")).build());
            log.error("BizUserDeviceServiceImpl.topologyAddDevice.设备已被绑定deviceId:{}家ID homeId:{}", jsonObject.getString("deviceId"), userDevice.getHomeId());
            bizWsPublishService.publishEditMemberByHomeIdFailure(
                    RedisTopicConstant.TOPIC_CHANNEL_DEVICE_ADD,
                    userDevice.getHomeId(),
                    dw, "设备已被绑定");
        }
        ValidUtils.noNullThrow(eleUserDevice, CommonCodeEnum.SUB_NOT_EXIST.getCode(), "设备已被绑定");

        //返回值
        message.getBody().put("data", deviceService.sha256(device.getId()));
    }

}
