package com.lj.iot.biz.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.SaveRfDataDto;
import com.lj.iot.biz.base.dto.StudyRfData2Dto;
import com.lj.iot.biz.base.dto.StudyRfDataDto;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.db.smart.entity.RfModel;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserDeviceRfKey;
import com.lj.iot.biz.db.smart.service.IRfModelService;
import com.lj.iot.biz.db.smart.service.IUserDeviceRfKeyService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizRfDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.aiui.DeviceNotificationService;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.aiui.core.dto.DeviceNotificationDto;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BizRfDeviceServiceImpl implements BizRfDeviceService {
    @Resource
    MqttPushService mqttPushService;

    @Resource
    IRfModelService rfModelService;

    @Resource
    IUserDeviceService userDeviceService;

    @Autowired
    private IUserDeviceRfKeyService userDeviceRfKeyService;

    @Resource
    private BizWsPublishService bizWsPublishService;

    @Override
    public void learnRfData(StudyRfDataDto dto, String userId) {

        UserDeviceRfKey userDeviceRfKey = userDeviceRfKeyService.getOne(new QueryWrapper<>(UserDeviceRfKey.builder()
                .id(dto.getUserRfKeyId())
                .deviceId(dto.getDeviceId())
                .userId(userId)
                .build()));
        ValidUtils.isNullThrow(userDeviceRfKey, "数据不存在");
        learnRfData(userDeviceRfKey);
    }

    @Override
    public void learnRfData(StudyRfData2Dto dto, String userId) {
        UserDeviceRfKey userDeviceRfKey = userDeviceRfKeyService.getOne(new QueryWrapper<>(UserDeviceRfKey.builder()
                .keyCode(dto.getKeyCode())
                .deviceId(dto.getDeviceId())
                .userId(userId)
                .build()));
        ValidUtils.isNullThrow(userDeviceRfKey, "数据不存在");
        learnRfData(userDeviceRfKey);
    }

    private void learnRfData(UserDeviceRfKey userDeviceRfKey) {

        //查询设备
        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(userDeviceRfKey.getDeviceId())
                .signalType(SignalEnum.RF.getCode())
                .build()));

        ValidUtils.isNullThrow(userDevice, "设备不存在");
        ValidUtils.isNullThrow(userDevice.getStatus(), "设备不在线");

        //控制器设备需要验证控制器设备与主控设备是不是在线
        userDeviceService.controlDeviceStatus(userDevice.getControlDeviceId(), userDevice.getUserId());

        //主控
        UserDevice masterUserDevice = userDeviceService.getById(userDevice.getMasterDeviceId());
        ValidUtils.isNullThrow(masterUserDevice, "主控不存在");
        ValidUtils.isFalseThrow(masterUserDevice.getStatus(), "主控不在线");

        JSONObject data = new JSONObject();
        data.put("signalType", SignalEnum.RF.getCode());
        data.put("controlDeviceId", userDevice.getControlDeviceId());
        data.put("keyId", userDeviceRfKey.getId());
        data.put("keyCode", userDeviceRfKey.getKeyCode());
        data.put("modelId", userDeviceRfKey.getModelId());
        data.put("productId", userDevice.getProductId());
        data.put("deviceId", userDevice.getDeviceId());

        // 3326进行分发-bind
        DeviceNotificationDto notificationDto = DeviceNotificationDto.builder().intentName("rfData")
                .masterDeviceId(userDeviceRfKey.getId() + "")
                .homeId(userDevice.getHomeId())
                .deviceId(userDevice.getDeviceId()).build();

        DeviceNotificationService deviceNotificationService = SpringUtil.getBean("deviceNotifi_" + notificationDto.getIntentName(), DeviceNotificationService.class);
        deviceNotificationService.handle(notificationDto);

        mqttPushService.signalStudy(masterUserDevice, data);
    }

    @Override
    public void saveRfData(SaveRfDataDto dto, String userId) {

        UserDeviceRfKey userDeviceRfKey = userDeviceRfKeyService.getOne(new QueryWrapper<>(UserDeviceRfKey.builder()
                .keyId(dto.getUserRfKeyId())
                .userId(userId)
                .deviceId(dto.getDeviceId())
                .build()));
        ValidUtils.isNullThrow(userDeviceRfKey, "按键不存在");

        userDeviceRfKeyService.updateById(UserDeviceRfKey.builder()
                .id(userDeviceRfKey.getId())
                .codeData(dto.getData())
                .build());
    }

    //TODO 射频设备也会出现虚设备
    @Override
    public void sendRfData(UserDevice userDevice, ThingModel changeThingModel, String keyCode) {
        //主控
        UserDevice masterUserDevice = userDeviceService.masterStatus(userDevice.getMasterDeviceId());

        UserDeviceRfKey userDeviceRfKey = userDeviceRfKeyService.getOne(new QueryWrapper<>(UserDeviceRfKey.builder()
                .keyCode(keyCode)
                .deviceId(userDevice.getDeviceId())
                .build()));
        ValidUtils.isNullThrow(userDeviceRfKey, "未找到按键码");
        ValidUtils.isEmptyThrow(userDeviceRfKey.getCodeData(), "还未学码，请先学码");
        String codeData = userDeviceRfKey.getCodeData();
        //控制器设备需要验证控制器设备与主控设备是不是在线
        userDeviceService.controlDeviceStatus(userDevice.getControlDeviceId(), userDevice.getUserId());

        RfModel rfModel = rfModelService.getById(userDevice.getModelId());
        JSONObject rfData = new JSONObject();
        rfData.put("encodeType", rfModel.getCodeType());
        rfData.put("zero", rfModel.getStartZeroTime());
        rfData.put("sym", rfModel.getUnitTime());
        rfData.put("syncHead", rfModel.getHeadData());
        rfData.put("controlDeviceId", userDevice.getControlDeviceId());
        rfData.put("type", userDevice.getRealProductType());
        rfData.put("keyCode", keyCode);

        mqttPushService.pushFROrIRCode(masterUserDevice, SignalEnum.RF, codeData.split(","), rfData);

        userDeviceService.saveChangeThingModel(userDevice, changeThingModel);
    }
}

