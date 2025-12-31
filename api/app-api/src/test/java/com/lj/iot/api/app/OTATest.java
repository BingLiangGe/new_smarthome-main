package com.lj.iot.api.app;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.dto.HandleUserDeviceDto;
import com.lj.iot.biz.base.dto.MqttParamDto;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.vo.UserDeviceFilterVo;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.db.smart.entity.ProductThingModelKey;
import com.lj.iot.biz.db.smart.entity.UpgradeRecord;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.lj.iot.biz.db.smart.service.IUpgradeRecordService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizProductThingModelKeyService;
import com.lj.iot.biz.service.aiui.IntentCommonHandler;
import com.lj.iot.biz.service.enums.ModeEnum;
import com.lj.iot.biz.service.enums.ProductTypeEnum;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelDataType;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.mqtt.client.core.MQTT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@SpringBootTest
public class OTATest {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IUpgradeRecordService upgradeRecordService;

    @Autowired
    private IntentCommonHandler intentCommonHandler;

    @Autowired
    private BizProductThingModelKeyService bizProductThingModelKeyService;

    @Autowired
    private IProductService productService;



    @Test
    public void sendComment() {
        List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .userId("20230411103052698117793677094912")
                .productType("gatway_872").build()));
        log.info("listsize={}", list.size());
        for (UserDevice masterDeviceId : list) {
            List<HandleUserDeviceDto<UserDevice>> handleUserDeviceDtoList = new ArrayList<>();

            // 查询子设备
            List<UserDeviceFilterVo> userDeviceList = userDeviceService.listByMasterDeviceId(masterDeviceId.getDeviceId());

            for (UserDeviceFilterVo userDeviceFilterVo : userDeviceList) {
                UserDevice userDevice = userDeviceService.getById(userDeviceFilterVo.getDeviceId());

                if (userDevice == null) {
                    log.info("deviceContr.设备不存在{}", userDeviceFilterVo.getDeviceId());
                    continue;
                }

                // 房间锁跳过
                if ("room_lock".equals(userDevice.getProductType())) {
                    continue;
                }

                ProductThingModelKey productThingModelKey = bizProductThingModelKeyService.getProductThingModelKey(userDevice, "close");


                //有些设备没有配置，说明不支持
                if (productThingModelKey == null) {
                    log.info("deviceContr.handle:没有配置对应按钮{}", JSON.toJSONString(userDeviceFilterVo));
                    continue;
                }
                ThingModel thingModel = buildThingModel(userDevice, productThingModelKey, 0);

                handleUserDeviceDtoList.add(HandleUserDeviceDto.<UserDevice>builder().userDevice(userDevice).changeThingModel(thingModel).keyCode(productThingModelKey.getKeyCode()).keyIdx(productThingModelKey.getKeyIdx()).build());
            }

            intentCommonHandler.doSend(handleUserDeviceDtoList, OperationEnum.AI_C);
        }
    }

    private ThingModel buildThingModel(UserDevice userDevice, ProductThingModelKey productThingModelKey, Integer value) {

        Product product = productService.getById(userDevice.getProductId());

        ThingModel productThingModel = product.getThingModel();
        ThingModel historyThingModel = userDevice.getThingModel();

        //物理模型属性List 2 Map
        Map<String, ThingModelProperty> productThingModelMap = productThingModel.thingModel2Map();

        Map<String, ThingModelProperty> thingModelPropertyMap = historyThingModel.thingModel2Map();

        String identifier = getIdentifier(userDevice, productThingModelKey.getIdentifier());
        ThingModelProperty thingModelProperty = productThingModelMap.get(identifier);
        ThingModelProperty historyThingModelProperty = thingModelPropertyMap.get(identifier);

        ThingModelDataType dataType = thingModelProperty.getDataType();
        String type = dataType.getType();

        ModeEnum modeEnum = ModeEnum.parse(productThingModelKey.getMode());

        switch (Objects.requireNonNull(modeEnum)) {
            case EQ: {
                historyThingModelProperty.setValue(value);
                break;
            }
            case LOOP:
            case ADD: {
                historyThingModelProperty.setValue(NumberUtil.parseInt(historyThingModelProperty.getValue() + "") + value);
                break;
            }
            case REDUCE: {

                //Math.abs(value)  数据库中减的mode存的是负数。备注：开始存的是正数，为了使场景保存设备不传keCode参数,能用identify查到唯一数据，改成了负数
                historyThingModelProperty.setValue(NumberUtil.parseInt(historyThingModelProperty.getValue() + "") - Math.abs(value));
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + modeEnum);
        }


        Map<String, String> specs = dataType.getSpecs();
        switch (type) {
            case "bool":
            case "enum": {
                value = NumberUtil.parseInt(historyThingModelProperty.getValue() + "") % specs.size();
                break;
            }
            case "int": {
                int max = NumberUtil.parseInt(specs.get("max"));
                int min = NumberUtil.parseInt(specs.get("min"));
                value = Math.min(Math.max(NumberUtil.parseInt(historyThingModelProperty.getValue() + ""), min), max);
                break;
            }
        }
        historyThingModelProperty.setValue(value);

        //空调需要全属性
        if (ProductTypeEnum.AC.getCode().equals(userDevice.getRealProductType())) {
            return historyThingModel;
        }
        List<ThingModelProperty> properties = new ArrayList<>();
        properties.add(historyThingModelProperty);

        return ThingModel.builder().properties(properties).build();
    }

    private String getIdentifier(UserDevice userDevice, String identifier) {
        ThingModel thingModel = userDevice.getThingModel();
        List<ThingModelProperty> thingModelPropertyDtoList = thingModel.getProperties();

        for (ThingModelProperty thingModelPropertyDto : thingModelPropertyDtoList) {
            if (thingModelPropertyDto.getIdentifier().startsWith(identifier)) {
                return thingModelPropertyDto.getIdentifier();
            }
        }
        return null;
    }

    @Test
    public void ota() {

        List<String> deviceIds= Lists.newArrayList();
        deviceIds.add("15ec38fc4afe");
        deviceIds.add("15ec38fc4b32");
        deviceIds.add("16a5dd4bc90f");
        deviceIds.add("16a5dd4bc929");
        deviceIds.add("15ec38fc4b1e");
        deviceIds.add("15ec38fc4af5");
        deviceIds.add("16a5dd4bc92b");


        QueryWrapper queryWrapper= new QueryWrapper<>();

        queryWrapper.in("device_id",deviceIds);
        List<UserDevice> list = userDeviceService.list(queryWrapper);
        log.info("listsize={}", list.size());
        for (UserDevice userDevice : list) {


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("productId", userDevice.getProductId());
            jsonObject.put("deviceId", userDevice.getDeviceId());

            // 重启设备
            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_RESTART, userDevice.getProductId(), userDevice.getPhysicalDeviceId());
            MqttParamDto paramDto = MqttParamDto.builder()
                    .id(IdUtil.simpleUUID())
                    .time(DateUtil.current())
                    .data(jsonObject)
                    .build();
            MQTT.publish(topic, JSON.toJSONString(paramDto));
            log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(paramDto));


            upgradeRecordService.save(UpgradeRecord.builder()
                    .deviceId(userDevice.getDeviceId())
                    .createTime(LocalDateTime.now())
                    .filePath("http://img.lj-smarthome.com/IOT/xr_system_v1.0.134_LJYL_ZS.img")
                    .softWareVersion("1.0.134")
                    .hardWareVersion("2.9")
                    .isSuccess(0).build());
        }
    }
}
