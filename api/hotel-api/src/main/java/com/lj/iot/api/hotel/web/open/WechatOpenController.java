package com.lj.iot.api.hotel.web.open;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.HandleUserDeviceDto;
import com.lj.iot.biz.base.dto.MqttParamDto;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.base.vo.UserDeviceFilterVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizProductThingModelKeyService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.aiui.IntentCommonHandler;
import com.lj.iot.biz.service.enums.ModeEnum;
import com.lj.iot.biz.service.enums.ProductTypeEnum;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelDataType;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.ToPinYin;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 微信小程序控制层
 *
 * @author tyj
 * @date 2023-8-14 14:17:02
 */
@Slf4j
@RequestMapping("/api/open/wechat")
@RestController
public class WechatOpenController {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IHomeService homeService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private BizProductThingModelKeyService bizProductThingModelKeyService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IntentCommonHandler intentCommonHandler;

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private MqttPushService mqttPushService;

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private BizWsPublishService bizWsPublishService;


    @RequestMapping("/sendSubdevice")
    public CommonResultVo<String> sendSubdevice() {

        List<UserDevice> listAll = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .status(true)
                .productType("gatway_872").build()));

        for (UserDevice masterDevice : listAll
        ) {
            QueryWrapper<UserDevice> wrapper = new QueryWrapper<>();
            wrapper.eq("master_device_id", masterDevice.getDeviceId());
            wrapper.eq("is_show", true);
            List<UserDevice> list = userDeviceService.list(wrapper);
            List<Map> resultList = new ArrayList<>();
            for (UserDevice userDevice :
                    list) {
                Map resultMap = new HashMap<>();
                ThingModel thingModel = userDevice.getThingModel();
                Map<String, ThingModelProperty> map = thingModel.thingModel2Map();
                List<Map<String, Object>> ThingModelMapList = new ArrayList<>();
                for (String key :
                        map.keySet()) {
                    //对每个map进行名称替换
                    ThingModelProperty thingModelProperty = map.get(key);
                    String pinYin = ToPinYin.getPinYin(thingModelProperty.getName());
                    ThingModelMapList.add(new HashMap<>() {{
                        put("name", pinYin);
                        put("identifier", key);
                        put("value", thingModelProperty.getValue());
                    }});
                }
                String customName = ToPinYin.getPinYin(userDevice.getCustomName());
                resultMap.put("deviceId", userDevice.getDeviceId());
                resultMap.put("status", userDevice.getStatus());
                resultMap.put("productId", userDevice.getProductId());
                if (customName.equals("kong1 diao4")) {
                    customName = "kong1 tiao2";
                } else if (customName.equals("jiu3 ju3 deng1")) {
                    customName = "jiu3 gui4 deng1";
                }
                resultMap.put("customName", customName);
                resultList.add(resultMap);
            }

            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_SUB_SUB_DEVICE_REPLY, masterDevice.getProductId(), masterDevice.getDeviceId());

            MQTT.publish(topic, JSON.toJSONString(resultList));
            log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(resultList));
        }
        return CommonResultVo.SUCCESS();
    }

    /**
     * 设置token
     *
     * @param masterDeviceId
     * @return
     */
    @RequestMapping("/settingToken")
    public CommonResultVo<String> settingToken(String masterDeviceId) {
        ValidUtils.isNullThrow(masterDeviceId, "masterDeviceId 必传");

        UserDevice masterDevice = userDeviceService.getById(masterDeviceId);

        ValidUtils.isNullThrow(masterDevice, "userDevice 必传");


        // 下发token
        UserAccount user = userAccountService.getById(masterDevice.getUserId());
        if (user != null) {
            //把token mqtt推送到硬件设备
            mqttPushService.pushLoginToken(masterDevice, LoginVo.<UserAccount>builder()
                    .account(user.getMobile())
                    .userInfo(user)
                    .token("")
                    .params(masterDevice.getHomeId())
                    .build());
        } else {
            HotelUserAccount hotelUserAccount = hotelUserAccountService.getById(masterDevice.getUserId());

            if (hotelUserAccount != null) {
                //把token mqtt推送到硬件设备
                mqttPushService.pushLoginToken(masterDevice, LoginVo.<HotelUserAccount>builder()
                        .account(hotelUserAccount.getMobile())
                        .userInfo(hotelUserAccount)
                        .token("")
                        .params(masterDevice.getHomeId())
                        .build());
            }
        }
        return CommonResultVo.SUCCESS();
    }

    /**
     * 设备控制
     *
     * @param masterDeviceId 主控设备id
     * @param type           0关  1开
     * @return
     */
    @RequestMapping("/deviceContr")
    public CommonResultVo<String> deviceContr(String masterDeviceId, Integer type, Integer isAll, Integer isLocked) {

        ValidUtils.isNullThrow(masterDeviceId, "masterDeviceId 必传");
        ValidUtils.isNullThrow(type, "type 必传");


        List<HandleUserDeviceDto<UserDevice>> handleUserDeviceDtoList = new ArrayList<>();


        List<HandleUserDeviceDto<UserDevice>> handleAirControllerList = Lists.newArrayList();

        List<HandleUserDeviceDto<UserDevice>> hanleRoomLockList = Lists.newArrayList();

        // 查询子设备
        List<UserDeviceFilterVo> userDeviceList = userDeviceService.listByMasterDeviceId(masterDeviceId);

        for (UserDeviceFilterVo userDeviceFilterVo : userDeviceList) {
            UserDevice userDevice = userDeviceService.getById(userDeviceFilterVo.getDeviceId());

            if (userDevice == null) {
                log.info("deviceContr.设备不存在{}", userDeviceFilterVo.getDeviceId());
                continue;
            }

            // 房间锁特殊处理
            if (isLocked != null && "room_lock".equals(userDevice.getProductType()) && type == 1) {
                userDevice.getThingModel().getProperties().get(0).setValue(type);

                hanleRoomLockList.add(HandleUserDeviceDto.<UserDevice>builder().userDevice(userDevice).changeThingModel(userDevice.getThingModel()).keyCode(type == 0 ? "close" : "open").keyIdx(type).build());
                continue;
            }

            // 房间锁跳过
            if (("room_lock".equals(userDevice.getProductType()) || "gate_lock".equals(userDevice.getProductType()))
                    || "mahjong_voice".equals(userDevice.getProductType()) || "mahjong_machine".equals(userDevice.getProductType())) {
                continue;
            }

            ProductThingModelKey productThingModelKey = bizProductThingModelKeyService.getProductThingModelKey(userDevice, type == 0 ? "close" : "open");


            //有些设备没有配置，说明不支持
            if (productThingModelKey == null) {
                log.info("deviceContr.handle:没有配置对应按钮{}", JSON.toJSONString(userDeviceFilterVo));
                continue;
            }
            ThingModel thingModel = buildThingModel(userDevice, productThingModelKey, type);

            // 窗帘跳过
            if ("curtain".equals(userDevice.getProductType())) {
                handleAirControllerList.add(HandleUserDeviceDto.<UserDevice>builder().userDevice(userDevice).changeThingModel(thingModel).keyCode(productThingModelKey.getKeyCode()).keyIdx(productThingModelKey.getKeyIdx()).build());
                continue;
            }

            // 空调设备跳过
            if (isAll != null && "airControl".equals(userDevice.getProductType())) {
                handleAirControllerList.add(HandleUserDeviceDto.<UserDevice>builder().userDevice(userDevice).changeThingModel(thingModel).keyCode(productThingModelKey.getKeyCode()).keyIdx(productThingModelKey.getKeyIdx()).build());
                continue;
            }

            handleUserDeviceDtoList.add(HandleUserDeviceDto.<UserDevice>builder().userDevice(userDevice).changeThingModel(thingModel).keyCode(productThingModelKey.getKeyCode()).keyIdx(productThingModelKey.getKeyIdx()).build());
        }

        intentCommonHandler.doSend(handleUserDeviceDtoList, OperationEnum.AI_C);

        if (isAll != null && !hanleRoomLockList.isEmpty()) {
            intentCommonHandler.doSend(hanleRoomLockList, OperationEnum.AI_C);
        }

        // 空调指令单独下发_休眠十秒
        if (isAll != null && !handleAirControllerList.isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000 * 10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    intentCommonHandler.doSend(handleAirControllerList, OperationEnum.AI_C);
                }
            }).start();
        }

        return CommonResultVo.SUCCESS();
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


    /**
     * 验证三元组
     *
     * @param deviceId
     * @return
     */
    @CrossOrigin
    @GetMapping("/checkDeviceId")
    public CommonResultVo<JSONObject> checkDeviceId(String deviceId) {


        ValidUtils.isNullThrow(deviceId, "deviceId参数有误");

        Device device = deviceService.getOne(new QueryWrapper<>(Device.builder().id(deviceId).build()));

        ValidUtils.isNullThrow(device, "三元组不存在");

        UserDevice userDevice = userDeviceService.getById(deviceId);

        if (userDevice != null) {


            ValidUtils.isNullThrow(null, "三元组已被绑定");
        }

        device.setSettingNumber(device.getSettingNumber() + 1);

        deviceService.updateById(device);

        JSONObject respJson = new JSONObject();

        respJson.put("isSetting", -1);
        respJson.put("settingNumber", device.getSettingNumber());
        respJson.put("createTime", device.getCreateTime());


        if (device.getIsSetting() == 1) {
            return CommonResultVo.SUCCESS(respJson);
        }

        device.setIsSetting(1);

        respJson.put("isSetting", device.getIsSetting());
        deviceService.updateById(device);

        return CommonResultVo.SUCCESS(respJson);
    }

    /**
     * 验证三元组
     *
     * @param CCCFDF
     * @return
     */
    @GetMapping("/checkCCCFDF")
    public String checkCCCFDF(String CCCFDF) {

        if (CCCFDF == null) {
            return "CCCFDF参数有误";
        }

        Device device = deviceService.getOne(new QueryWrapper<>(Device.builder().CCCFDF(CCCFDF).build()));

        if (device == null) {
            return "三元组不存在";
        }

        if (device.getIsSetting() == 1) {
            return "三元组已重复";
        }

        device.setIsSetting(1);

        deviceService.updateById(device);

        return "成功";
    }

    /**
     * 锁定主控
     *
     * @param homeId 房间id
     * @param type   类型 1锁定 0解锁
     * @return
     */
    @RequestMapping("/lockMasterDevice")
    public CommonResultVo<String> lockMasterDevice(String homeId, Integer type, Integer isAll) {
        ValidUtils.isNullThrow(homeId, "homeId必传");

        Home home = homeService.getById(homeId);

        ValidUtils.isNullThrow(homeId, "home信息不存在");

        List<UserDevice> masterList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().homeId(home.getId()).signalType("MASTER").build()));

        masterList.forEach(masterDevice -> {
            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, masterDevice.getProductId(), masterDevice.getDeviceId());
            String data = JSON.toJSONString(new HashMap() {{
                put("id", masterDevice.getDeviceId());

                userDeviceService.updateById(UserDevice.builder().
                        deviceId(masterDevice.getDeviceId()).
                        isDel(type == 1 ? true : false).build());
                put("enable", type);
            }});

            if (isAll != null) {
                deviceContr(masterDevice.getDeviceId(), type == 1 ? 0 : 1, 1, 1);

                if (type == 1) {
                    try {
                        Thread.sleep(1000 * 15);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // 发送锁定
                    MQTT.publish(topic, data);
                    bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_MASTER_DEVICE_LOCK, masterDevice.getHomeId(), data);
                } else {
                    // 发送解锁
                    MQTT.publish(topic, data);
                    bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_MASTER_DEVICE_LOCK, masterDevice.getHomeId(), data);
                }

            } else {
                // 发送锁定
                MQTT.publish(topic, data);
                bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_MASTER_DEVICE_LOCK, masterDevice.getHomeId(), data);
            }

            // 解锁主控
            if (type == 0 && masterList.size() > 0) {
                cacheService.del("app" + RedisConstant.wait_lock_reply_device + "player_" + masterDevice.getMasterDeviceId());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, masterDevice.getMasterProductId(), masterDevice.getMasterDeviceId());
                            for (int i = 0; i < 3; i++) {
                                Thread.sleep(1000 * 7);

                                String key = "locked_master" + masterDevice.getMasterDeviceId();
                                log.info("解锁主控重发,key={}", key);
                                String data = cacheService.get(key);
                                log.info("解锁主控重发,data={}", data);

                                if (data != null) {
                                    break;
                                }
                                MQTT.publish(topic, JSON.toJSONString(new HashMap() {{
                                    put("id", masterDevice.getMasterDeviceId());
                                    put("enable", 0);
                                }}));
                            }

                        } catch (Exception e) {
                            log.info("解锁主控重发----->deviceId={}", masterList.get(0).getMasterDeviceId());
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        });

        return CommonResultVo.SUCCESS();
    }

    /**
     * 播放音频
     * 1-十五分钟结束-岭捷
     * 2-五分钟结束-岭捷
     * 3-欢迎光临-岭捷
     * 4-结束-岭捷
     * 11-十五分钟结束-通用
     * 12-五分钟结束-通用
     * 13-欢迎光临-通用
     * 14-结束-通用
     * 15-结束_即将断电
     * 16-断电中
     *
     * @param masterDeviceId
     * @param code
     * @return
     */
    @RequestMapping("/audioFrequency")
    public CommonResultVo<String> sendVolmn(String masterDeviceId, String code) {

        ValidUtils.isNullThrow(masterDeviceId, "masterDeviceId必传");
        ValidUtils.isNullThrow(code, "code必传");

        UserDevice userDevice = userDeviceService.getById(masterDeviceId);
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        String url = "https://img.lj-smarthome.com/wechat_mp3/" + code + ".mp3";

        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, userDevice.getProductId(), masterDeviceId);
        String resp = JSON.toJSONString(new HashMap() {{
            put("id", masterDeviceId);
            put("url", url);
            put("code", code);
        }});
        MQTT.publish(topic, resp);

        log.info("Mqtt-Send:" + topic + "=" + resp);


        cacheService.del("app" + RedisConstant.wait_device + "player_" + masterDeviceId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 3; i++) {
                        Thread.sleep(1000 * 15);

                        String key = "app" + RedisConstant.wait_device + "player_" + masterDeviceId;
                        log.info("播报音频,key={}", key);
                        String data = cacheService.get(key);
                        log.info("播报音频,data={}", data);

                        if (data != null) {
                            break;
                        }

                        MQTT.publish(topic, resp);
                        log.info("Mqtt-Send:" + topic + "=" + resp);
                    }

                } catch (Exception e) {
                    log.info("播报音频----->deviceId={}", masterDeviceId);
                    throw new RuntimeException(e);
                }
            }
        }).start();

        return CommonResultVo.SUCCESS();
    }
}
