package com.lj.iot.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.TestIrDataDto;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.IIrModelService;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.lj.iot.biz.db.smart.service.IProductTypeService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizIrDataService;
import com.lj.iot.biz.service.BizIrDeviceService;
import com.lj.iot.biz.service.BizProductThingModelKeyService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.ProductTypeEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.ValidUtils;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BizIrDeviceServiceImpl implements BizIrDeviceService {
    @Resource
    IIrModelService irModelService;

    @Resource
    IUserDeviceService userDeviceService;

    @Resource
    MqttPushService mqttPushService;

    @Autowired
    private BizIrDataService irDataService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IProductTypeService productTypeService;

    @Autowired
    private BizProductThingModelKeyService bizProductThingModelKeyService;

    @Resource
    private BizWsPublishService bizWsPublishService;

    @Autowired
    private ICacheService cacheService;

    public ScheduledThreadPoolExecutor scheduledThreadPool = new ScheduledThreadPoolExecutor(NettyRuntime.availableProcessors());

    @Override
    public void sendIrData(UserDevice userDevice, ThingModel changeThingModel, String keyCode) {

        String key = "send_ir" + RedisConstant.wait_device + "_" + keyCode + ":" + userDevice.getDeviceId();

        UserDevice masterUserDevice = userDeviceService.masterStatus(userDevice.getMasterDeviceId());

        if (cacheService.get(key) != null && "213350486".equals(masterUserDevice.getProductId()) ) {
            log.info("空调触发指定时间不发送码,deviceId={}", userDevice.getMasterDeviceId());
            return;
        }


        IrModel irModel = irModelService.getById(userDevice.getModelId());
        ValidUtils.isNullThrow(irModel, "红外设备模型数据不存在");

        List<ThingModelProperty> changeThingModelProperties = changeThingModel.getProperties();
        ValidUtils.isNullThrow(changeThingModelProperties, "属性数据不能为空");

        ProductThingModelKey productThingModelKey = bizProductThingModelKeyService.getProductThingModelKey(userDevice, keyCode);
        //获取红外码
        String irData = irDataService.getIrData(userDevice.getRealProductType(), irModel, changeThingModel, productThingModelKey.getKeyIdx());


        Integer time = productThingModelKey.getTime();

        //发送红外数据
        for (int t = 0; t < time; t++) {
            JSONObject extend = extendData(userDevice.getTopProductType(), changeThingModel);
            extend.put("deviceId", userDevice.getDeviceId());
            extend.put("operateType", keyCode);
            extend.put("typeId", userDevice.getProductId());


            // 场景下发对应标识
            if (userDevice.getIsTrigger() != null) {
                masterUserDevice.setIsTrigger(1);
            }


            log.info("time={},delay={},time*d={}", t, productThingModelKey.getDelay(), (long) productThingModelKey.getDelay() * t);

            scheduledThreadPool.schedule(() -> mqttPushService.pushFROrIRCode(masterUserDevice, SignalEnum.IR, irData.split(","),
                            extend),
                    (long) productThingModelKey.getDelay() * t, TimeUnit.MILLISECONDS);

            cacheService.add(key, "1", 8500, TimeUnit.MILLISECONDS);
           /*
            todo 新代码
             String finalRespData = respData;
              String [] irDatas=irData.split(",");

            String respData="";

            for (String data:irDatas
                 ) {
                respData+=data;
            }
            scheduledThreadPool.schedule(() -> mqttPushService.pushIRCode(masterUserDevice, SignalEnum.IR, finalRespData,
                            extend),
                    (long) productThingModelKey.getDelay() * t, TimeUnit.MILLISECONDS);*/
        }

        userDeviceService.saveChangeThingModel(userDevice, changeThingModel);
        /*bizWsPublishService.publishEditMemberByHomeId(
                RedisTopicConstant.TOPIC_CHANNEL_DEVICE_PROPERTIES_POST,
                userDevice.getHomeId(),
                userDeviceService.getById(userDevice.getDeviceId()));*/
    }

    @Override
    public void testIrData(TestIrDataDto dto, String userId) {

        UserDevice masterUserDevice = userDeviceService.masterStatus(dto.getMasterDeviceId(), userId);

        //masterUserDevice.setStatus();
        IrModel irModel = irModelService.getById(dto.getModelId());
        ValidUtils.isNullThrow(irModel, "红外设备模型数据不存在");

        List<ThingModelProperty> properties = dto.getThingModel().getProperties();
        ValidUtils.isNullThrow(properties, "属性数据不能为空");

        Product product = productService.getById(dto.getProductId());
        ValidUtils.isNullThrow(product, "产品不存在");

        //获取红外码
        String irData = irDataService.getIrData(product.getProductType(), irModel, dto.getThingModel(), dto.getKeyIndex());

        ValidUtils.isNullThrow(irData, "当前遥控器无此按键");
        //发送红外数据
        ProductType topProductType = productTypeService.getTopProductType(product.getProductType());

        JSONObject extend = extendData(topProductType.getProductType(), dto.getThingModel());
        mqttPushService.pushFROrIRCode(masterUserDevice, SignalEnum.IR, irData.split(","), extend);
    }

    @Override
    public JSONObject extendData(String topProductType, ThingModel changeThingModel) {
        JSONObject extend = new JSONObject();
        extend.put("topProductType", topProductType);
        if (ProductTypeEnum.AC.getCode().equals(topProductType)) {
            extend.put("powerstate", changeThingModel.thingModel2Map().get("powerstate").getValue() + "");
            extend.put("temperature", changeThingModel.thingModel2Map().get("temperature").getValue() + "");
            extend.put("workmode", changeThingModel.thingModel2Map().get("workmode").getValue() + "");
            extend.put("fanspeed", changeThingModel.thingModel2Map().get("fanspeed").getValue() + "");
        }
        return extend;
    }
}
