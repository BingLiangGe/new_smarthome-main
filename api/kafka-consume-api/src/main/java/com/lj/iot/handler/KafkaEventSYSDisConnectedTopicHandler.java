package com.lj.iot.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统topic  设备离线
 *
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class KafkaEventSYSDisConnectedTopicHandler extends AbstractTopicHandler {

    public KafkaEventSYSDisConnectedTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_SYS_DISCONNECTED);
    }

    @Resource
    private IUserDeviceService userDeviceService;

    @Resource
    @Lazy
    private BizWsPublishService bizWsPublishService;

    /**
     * 设备离线监听
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {
        String topicDeviceId = message.getTopicDeviceId();

        //判断是不是锁
        UserDevice ifLock = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(message.getTopicDeviceId())
                .build()));
        if(ifLock!=null){
            if (ifLock.getProductId().equals("9337719")|ifLock.getProductId().equals("9337720")){
                //特殊处理
                List<UserDevice> deviceId = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().masterDeviceId(ifLock.getMasterDeviceId()).build()));
                //状态同步
                for (int i = 0; i < deviceId.size(); i++) {
                    //把子设备设置成主控的状态
                    UserDevice userDevice = deviceId.get(i);
                    userDevice.setStatus(false);
                    userDeviceService.updateById(userDevice);
                }

                return;
            }
        }


        //主控设备下的所有在线设备离线
        UserDevice masterDevice = userDeviceService.getById(topicDeviceId);

        List<String> deviceIdList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .masterDeviceId(masterDevice.getDeviceId())
                .build())).stream().filter(UserDevice::getStatus)
                .map(UserDevice::getDeviceId).collect(Collectors.toList());

        if (deviceIdList.size() == 0) {
            return;
        }
        userDeviceService.update(UserDevice.builder()
                .status(false)
                .build(), new QueryWrapper<>(UserDevice.builder().build()).in("device_id", deviceIdList));

        bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_OFFLINE, masterDevice.getHomeId(), deviceIdList);
        bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_OFFLINE_SUBSIDIARY, masterDevice.getHomeId(), masterDevice.getMasterDeviceId());

    }
}