package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class EventSubDeviceLineTopicHandler extends AbstractTopicHandler {

    public EventSubDeviceLineTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_SUB_DEVICE_LINE);
    }

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private BizWsPublishService bizWsPublishService;

    /**
     * 子设备在线状态
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params": {
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


        UserDevice ifLock = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(message.getTopicDeviceId())
                .build()));

        /*//判断是不是锁
        UserDevice ifLock = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(message.getTopicDeviceId())
                .build()));
        if (ifLock.getProductId().equals("9337719") | ifLock.getProductId().equals("9337720")) {
            //特殊处理
            List<UserDevice> deviceId = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().masterDeviceId(ifLock.getMasterDeviceId()).build()));
            //状态同步
            String masterDeviceId = deviceId.get(0).getMasterDeviceId();
            UserDevice byId = userDeviceService.getById(masterDeviceId);

            for (int i = 0; i < deviceId.size(); i++) {
                //把子设备设置成主控的状态
                UserDevice userDevice = deviceId.get(i);
                userDevice.setStatus(byId.getStatus());
                userDeviceService.updateById(userDevice);
            }
            return;
        }*/
        //心跳过来没数据代表在线
        if (jsonObject == null) {
            //设置在线
            userDeviceService.update(
                    UserDevice.builder()
                            .status(true)
                            .statusTime(LocalDateTime.now())
                            .build(),
                    new QueryWrapper<>(UserDevice.builder()
                            .masterDeviceId(ifLock.getMasterDeviceId())
                            .build()));
            String [] deviceIds={ifLock.getDeviceId()};
            try {
                //bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_ONLINE, ifLock.getHomeId(), deviceIds);
            }catch (Exception e){
                log.info("line====>",e.getMessage());
            }
            return;
        }
       /* UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId((jsonObject.getString("deviceId")))
                .masterDeviceId(message.getTopicDeviceId())
                .build()));

        if (userDevice == null){
            log.info("子设备状态推送_设备不存在,deviceId={},masterDeviceId={}",jsonObject.getString("deviceId"),message.getTopicDeviceId());
        }

        ValidUtils.isNullThrow(userDevice, CommonCodeEnum.SUB_NOT_EXIST.getCode(), "设备不存在");

        String [] deviceIds={userDevice.getDeviceId()};*/

        //bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_ONLINE, userDevice.getHomeId(), deviceIds);
       /* userDeviceService.update(
                UserDevice.builder()
                        .status(jsonObject.getBoolean("line"))
                        .statusTime(LocalDateTime.now())
                        .build(), new QueryWrapper<>(UserDevice.builder()
                        // .physicalDeviceId(userDevice.getDeviceId())
                        .masterDeviceId(userDevice.getMasterDeviceId())
                        .build()));*/

    }

}
