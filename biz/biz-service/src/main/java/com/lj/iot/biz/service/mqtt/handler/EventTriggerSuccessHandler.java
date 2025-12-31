package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.DeviceWsVo;
import com.lj.iot.biz.db.smart.entity.Device;
import com.lj.iot.biz.db.smart.entity.SceneDevice;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IDeviceService;
import com.lj.iot.biz.db.smart.service.ISceneDeviceService;
import com.lj.iot.biz.db.smart.service.ISceneService;
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

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;


/**
 * 离线场景执行
 *
 * @author tyj
 */
@Slf4j
@Component
public class EventTriggerSuccessHandler extends AbstractTopicHandler {

    public EventTriggerSuccessHandler() {
        setSupportTopic(SubTopicEnum.EVENT_TRIGGER_SUCCESS);
    }

    @Autowired
    private ISceneDeviceService sceneDeviceService;

    @Autowired
    private IUserDeviceService userDeviceService;


    @Override
    public void handle(HandleMessage message) {
        log.info("EventTriggerSuccessHandler={}", message.getBody());
        long senceId = message.getBody().getLong("senceId");

        List<SceneDevice> list = sceneDeviceService.getBySceneId(senceId);

        for (SceneDevice scneDevice : list
        ) {
            UserDevice userDevice = userDeviceService.getById(scneDevice.getDeviceId());

            if (userDevice == null){
                continue;
            }

            userDeviceService.saveChangeThingModel(userDevice, scneDevice.getThingModel());
        }
    }
}
