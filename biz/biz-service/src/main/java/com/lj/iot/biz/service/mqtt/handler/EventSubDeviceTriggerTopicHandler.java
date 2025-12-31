package com.lj.iot.biz.service.mqtt.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.MqttParamDto;
import com.lj.iot.biz.base.vo.SceneSendInfoVo;
import com.lj.iot.biz.base.vo.SceneSendVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizIrDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 获取场景
 *
 * @author tyj
 * @Date 2023-6-8 11:41:48
 */
@Slf4j
@Component
public class EventSubDeviceTriggerTopicHandler extends AbstractTopicHandler {

    public EventSubDeviceTriggerTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_SUB_DEVICE_TRIGGER);
    }

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private ISceneDeviceService sceneDeviceService;


    @Override
    public void handle(HandleMessage message) {
        String masterDeviceId = message.getTopicDeviceId();

        UserDevice masterDevice = userDeviceService.getById(masterDeviceId);

        ValidUtils.isNullThrow(masterDevice, "设备不存在");

        List<Scene> sceneList = sceneService.list(new QueryWrapper<>(Scene.builder()
                .homeId(masterDevice.getHomeId()).build()));

        List<SceneSendVo> dataList = Lists.newArrayList();

        for (Scene scene : sceneList
        ) {
            List<SceneDevice> sceneDeviceList = sceneDeviceService.getBySceneId(scene.getId());

            log.info("comm={}",scene.getCommand());
            SceneSendVo vo = SceneSendVo.builder()
                    .sceneId(scene.getId())
                    .command(scene.getCommand())
                    .sceneName(scene.getSceneName()).build();

            List<SceneSendInfoVo> infoList = Lists.newArrayList();
            for (SceneDevice sceneDevice : sceneDeviceList
            ) {
                UserDevice userDevice = userDeviceService.getById(sceneDevice.getDeviceId());

                if (userDevice == null){
                    continue;
                }
                infoList.add(SceneSendInfoVo.builder()
                        .deviceId(sceneDevice.getDeviceId())
                                .deviceType(userDevice.getSignalType())
                        .type(sceneDevice.getThingModel().getProperties().get(0).getValue()).build());
            }
            vo.setDeviceList(infoList);
            dataList.add(vo);
        }

        // 设备端要求
        for (SceneSendVo vo : dataList
        ) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.TRIGGER_REPLY, masterDevice.getProductId(), masterDevice.getMasterDeviceId());
            MqttParamDto paramDto = MqttParamDto.builder()
                    .id(IdUtil.simpleUUID())
                    .time(DateUtil.current())
                    .data(vo)
                    .build();
            MQTT.publish(topic, JSON.toJSONString(paramDto));
            log.info("Mqtt-Send_trigger:" + topic + "=" + JSON.toJSONString(paramDto));
        }
    }

}
