package com.lj.iot.biz.service.aiui;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.db.smart.entity.Device;
import com.lj.iot.biz.db.smart.entity.SpeechRecord;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IDeviceService;
import com.lj.iot.biz.db.smart.service.ISpeechRecordService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.service.IIdentifyingTextPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IdentifyingTextPostProcessorImpl implements IIdentifyingTextPostProcessor {


    @Autowired
    private BizWsPublishService bizWsPublishService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private ISpeechRecordService speechRecordService;

    @Autowired
    private IDeviceService deviceService;

    @Async
    @Override
    public void handle(IntentDto intentDto) {

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(intentDto.getMasterDeviceId())
                .build()));


        if (userDevice != null) {


            speechRecordService.save(SpeechRecord.builder()
                    .deviceId(userDevice.getDeviceId())
                    .userId(userDevice.getUserId())
                    .homeId(userDevice.getHomeId())
                    .text(intentDto.getText())
                    .answer(intentDto.getAnswer())
                    .intentName(intentDto.getIntentName())
                    .build());

            log.info("进入识别记录推送homeId={},deviceId={}", userDevice.getHomeId(), userDevice.getMasterDeviceId());
            //websocket推送
            JSONObject param = new JSONObject();
            param.put("deviceId", intentDto.getMasterDeviceId());
            param.put("text", intentDto.getText());
            bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_IDENTIFYING_TEXT, userDevice.getHomeId(),
                    param);
        }
    }
}
