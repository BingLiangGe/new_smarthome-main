package com.lj.iot.biz.service.mqtt.push.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.MqttParamDto;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.base.vo.SendUserDeviceBindVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.IOperationLogService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.OfflineTypeEnum;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.fegin.job.JobFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MqttPushServiceImpl implements MqttPushService {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private ICacheService cacheService;


    /**
     * 发送红外码
     *
     * @param masterDevice 主控设备ID
     * @param ir           信号类型
     * @param signal       红外码
     * @param extendData   型号参数
     *                     <p>
     *                     {
     *                     "id": "123", //消息ID
     *                     "time": 1524448722000, //时间
     *                     "params": {
     *                     "signalType": "IR", //信号类型  IR\|RF
     *                     "signal": [89, 23, 23, ...],// 信号
     *                     "extendData":{ //扩展参数(不一定都有)
     *                     "encodeType":"xx",
     *                     "zero":"xx",
     *                     "sym":"xx",
     *                     "syncHead":"xx"
     *                     }
     *                     }
     *                     }
     */
    @Override
    public void pushFROrIRCode(UserDevice masterDevice, SignalEnum ir, String[] signal, Object extendData) {

        Map<String, Object> data = new HashMap<>();
        data.put("signalType", ir.getCode());
        data.put("signal", signal);
        data.put("extendData", extendData);
        // 场景下发对应标识
        if (masterDevice.getIsTrigger() != null) {
            data.put("isTrigger", masterDevice.getIsTrigger());
        }
        push(masterDevice, PubTopicEnum.PUB_IR_OR_RF_CODE, data);
    }


    public void pushIRCode(UserDevice masterDevice, SignalEnum ir, String signal, Object extendData) {
        Map<String, Object> data = new HashMap<>();
        data.put("signalType", ir.getCode());
        data.put("signal", signal);
        data.put("extendData", extendData);
        // 场景下发对应标识
        if (masterDevice.getIsTrigger() != null) {
            data.put("isTrigger", masterDevice.getIsTrigger());
        }
        push(masterDevice, PubTopicEnum.PUB_IR_OR_RF_CODE, data);
    }

    @Override
    public void pushFROrIRCodeTrigger(UserDevice masterDevice, SignalEnum ir, String[] signal, Object extendData, Integer isTrigger) {
        Map<String, Object> data = new HashMap<>();
        data.put("signalType", ir.getCode());
        data.put("signal", signal);
        data.put("extendData", extendData);
        data.put("isTrigger", isTrigger);
        push(masterDevice, PubTopicEnum.PUB_IR_OR_RF_CODE, data);
    }

    @Override
    public void pushLoginToken(UserDevice masterDevice, Object extendData) {
        push(masterDevice, PubTopicEnum.PUB_LOGIN_TOKEN, extendData);
    }


    /**
     * 设置mesh设备属性
     *
     * @param
     */
    @Override
    public void pushMeshProperties(UserDevice masterUserDevice, UserDevice userDevice, ThingModel properties, OperationEnum operationEnum) {
        properties.thingModelPropertyExtend(userDevice.getThingModel());
        Map<String, Object> data = new HashMap<>();
        data.put("productId", userDevice.getProductId());
        data.put("deviceId", userDevice.getPhysicalDeviceId());
        data.put("properties", properties.simpleProperties());


        // 场景下发对应标识
        if (userDevice.getIsTrigger() != null) {
            data.put("isTrigger", userDevice.getIsTrigger());
        }


        //以后设备就不回来了,后台直接改变状态
        userDeviceService.saveChangeThingModel(userDevice, properties);
        push(masterUserDevice, PubTopicEnum.PUB_MESH_PROPERTIES, data);
    }

    @Override
    public void pushMethMajongMachine(UserDevice masterUserDevice, UserDevice userDevice, String[] datas, String identifier, ThingModel properties, OperationEnum operationEnum) {

        JSONObject identifierJSON = new JSONObject();

        identifierJSON.put("value", datas);
        identifierJSON.put("identifier", "mahjongCode");

        JSONArray propertiesArray = new JSONArray();
        propertiesArray.add(identifierJSON);

        Map<String, Object> data = new HashMap<>();
        data.put("productId", userDevice.getProductId());
        data.put("deviceId", userDevice.getDeviceId());
        data.put("properties", propertiesArray);

        // 场景下发对应标识
        if (userDevice.getIsTrigger() != null) {
            data.put("isTrigger", userDevice.getIsTrigger());
        }

        properties.thingModelPropertyExtend(userDevice.getThingModel());
        //以后设备就不回来了,后台直接改变状态
        userDeviceService.saveChangeThingModel(userDevice, properties);

        push(masterUserDevice, PubTopicEnum.PUB_MESH_PROPERTIES, data);
    }

    @Override
    public void pushMeshHeatingTable(UserDevice masterUserDevice, UserDevice userDevice, String commend, String identifier, ThingModel properties, OperationEnum operationEnum) {
        JSONObject identifierJSON = new JSONObject();

        identifierJSON.put("value", commend.split(","));
        identifierJSON.put("identifier", "warmCode");

        JSONArray propertiesJson = new JSONArray();
        propertiesJson.add(identifierJSON);

        Map<String, Object> data = new HashMap<>();
        data.put("productId", userDevice.getProductId());
        data.put("deviceId", userDevice.getDeviceId());
        data.put("properties", propertiesJson);

        // 场景下发对应标识
        if (userDevice.getIsTrigger() != null) {
            data.put("isTrigger", userDevice.getIsTrigger());
        }
        push(masterUserDevice, PubTopicEnum.PUB_MESH_PROPERTIES, data);
    }

    @Override
    public void pushMeshPropertiesCase(UserDevice masterUserDevice, UserDevice userDevice, ThingModel properties, OperationEnum operationEnum, JSONObject valueJson) {
        properties.thingModelPropertyExtend(userDevice.getThingModel());
        Map<String, Object> data = new HashMap<>();
        data.put("productId", userDevice.getProductId());
        data.put("deviceId", userDevice.getPhysicalDeviceId());
        data.put("properties", properties.simpleProperties());
        //以后设备就不回来了,后台直接改变状态
        userDeviceService.saveChangeThingModel(userDevice, properties);
        pushCase(masterUserDevice, PubTopicEnum.PUB_MESH_PROPERTIES, data, valueJson);
    }


    /**
     * 单火二路和单火三路mesh设备属性下发
     *
     * @param
     */
    @Override
    public void pushMeshSwitchProperties(UserDevice masterUserDevice, UserDevice userDevice, ThingModel properties, OperationEnum operationEnum) {
        properties.thingSwitchModelPropertyExtend(userDevice.getThingModel());
        Map<String, Object> data = new HashMap<>();
        data.put("productId", userDevice.getProductId());
        data.put("deviceId", userDevice.getPhysicalDeviceId());
        data.put("properties", properties.simpleProperties());
        //以后设备就不回来了,后台直接改变状态
        userDeviceService.saveChangeThingModel(userDevice, properties);
        push(masterUserDevice, PubTopicEnum.PUB_MESH_PROPERTIES, data);
    }

    /**
     * 学习射频码
     *
     * @param
     */
    @Override
    public void signalStudy(UserDevice masterUserDevice, Object params) {
        push(masterUserDevice, PubTopicEnum.PUB_RF_CODE, params);
    }

    /**
     * 通知网关topology关系
     * {
     * "id": "123", //消息ID
     * "time": 1524448722000, //时间
     * "params":[{
     * "productId": "56789", //产品ID
     * "deviceId": "123456" //设备ID
     * }, {
     * "productId": "567891", //产品ID
     * "deviceId": "1234561" //设备ID
     * },
     * ...
     * ]
     * }
     *
     * @param
     */
    @Override
    public void notifyTopology(UserDevice masterUserDevice) {
        List<UserDevice> userDeviceList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .masterDeviceId(masterUserDevice.getMasterDeviceId())
                .signalType(SignalEnum.MESH.getCode())
                .build()));
        JSONArray jsonArray = new JSONArray();
        for (UserDevice userDevice : userDeviceList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("productId", userDevice.getProductId());
            jsonObject.put("deviceId", userDevice.getDeviceId());
            jsonArray.add(jsonObject);
        }
        push(masterUserDevice, PubTopicEnum.PUB_TOPOLOGY_NOTIFY, jsonArray);
    }

    @Override
    public void reset(UserDevice masterUserDevice) {
        push(masterUserDevice, PubTopicEnum.PUB_RESET, "");
    }

    @Override
    public void delete(UserDevice masterUserDevice, UserDevice userDevice) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productId", userDevice.getProductId());
        jsonObject.put("deviceId", userDevice.getDeviceId());
        push(masterUserDevice, PubTopicEnum.PUB_TOPOLOGY_DELETE, jsonObject);
    }

    @Override
    public void addTopology(UserDevice masterUserDevice, Object params) {
        push(masterUserDevice, PubTopicEnum.PUB_TOPOLOGY_ADD, params);
    }

    @Override
    public void noExistDevice(String productId, String deviceId) {

        JSONObject params = new JSONObject();
        params.put("productId", productId);
        params.put("deviceId", deviceId);
        push(UserDevice.builder()
                .productId(productId)
                .physicalDeviceId(deviceId)
                .build(), PubTopicEnum.PUB_TOPOLOGY_NO_EXIST_DEVICE, params);
    }

    @Override
    public void noExistDevice(String productId, String deviceId, Object data) {
        push(UserDevice.builder()
                .productId(productId)
                .physicalDeviceId(deviceId)
                .build(), PubTopicEnum.PUB_TOPOLOGY_NO_EXIST_DEVICE, data);
    }

    @Override
    public void restartMasterDevice(UserDevice masterUserDevice) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productId", masterUserDevice.getProductId());
        jsonObject.put("deviceId", masterUserDevice.getDeviceId());
        push(masterUserDevice, PubTopicEnum.PUB_RESTART, jsonObject);
    }

    @Override
    public void sendBindDevice(UserDevice userDevice, List<SendUserDeviceBindVo> sendUserDeviceBindVos, String groupId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("groupId", groupId);
        jsonObject.put("deviceList", sendUserDeviceBindVos);
        push(userDevice, PubTopicEnum.PUB_BIND_DEVICE, jsonObject);


        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.TRIGGER, userDevice.getMasterProductId(), userDevice.getMasterDeviceId());
        MqttParamDto paramDto = MqttParamDto.builder()
                .id(IdUtil.simpleUUID())
                .time(DateUtil.current())
                .data("send")
                .build();
        MQTT.publish(topic, JSON.toJSONString(paramDto));
        log.info("Mqtt-Send_trigger:" + topic + "=" + JSON.toJSONString(paramDto));

    }


    @Override
    public void musicChange(UserDevice userDevice, String type, String musicId, String volume) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("musicId", musicId);
        jsonObject.put("volume", volume);
        push(userDevice, PubTopicEnum.PUB_MUSIC_CHANGE, jsonObject);
    }

    @Override
    public void musicMenu(UserDevice userDevice, List<MusicMenuTop> list) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("MusicMenuList", list);
        push(userDevice, PubTopicEnum.PUB_MUSIC_CHANGE, jsonObject);
    }

    @Override
    public void pushOfficeHomeData(Home home, String type, UserDevice userDevice) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("data", home.getId());
        //todo
        push(userDevice, PubTopicEnum.PUB_HOME_CHANGE, jsonObject);
    }

    @Override
    public void pushOfficeHomeRoomData(HomeRoom homeRoom, String type, UserDevice userDevice) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("data", homeRoom);
        push(userDevice, PubTopicEnum.PUB_HOMEROOM_CHANGE, jsonObject);
    }

    @Override
    public void pushOfficeSceneData(Object dto, String type, UserDevice userDevice) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("data", dto);
        push(userDevice, PubTopicEnum.PUB_SCENE_CHANGE, jsonObject);
    }

    @Override
    public void pushOfficeData(UserDevice masterUserDevice, OfflineTypeEnum offlineTypeEnum, PubTopicEnum pubTopicEnum, Object data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", offlineTypeEnum.getCode());
        jsonObject.put("data", data);
        push(masterUserDevice, pubTopicEnum, jsonObject);
    }

    @Override
    public void pushOfficeData(Long homeId, OfflineTypeEnum offlineTypeEnum, PubTopicEnum pubTopicEnum, Object data) {
        //推送消息到家下所有主控设备
        List<UserDevice> maUserDeviceList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .homeId(homeId)
                .signalType(SignalEnum.MASTER.getCode())
                .build()));
        maUserDeviceList.forEach(userDevice -> this.pushOfficeData(userDevice, offlineTypeEnum, pubTopicEnum, data));
    }

    @Override
    public void pushOfficeSceneTriggerData(Object sceneId, String type, UserDevice userDevice) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("data", sceneId);
        push(userDevice, PubTopicEnum.PUB_SCENE_TRIGGER, jsonObject);
    }

    @Override
    public void pushOfficeAddDeviceData(Object userDeviceList, String type, UserDevice userDevice) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("data", userDeviceList);
        push(userDevice, PubTopicEnum.PUB_DEVICE_ADD, jsonObject);
    }

    @Override
    public void pushOfficeSignalChange(Object dto, String type, UserDevice userDevice) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("data", dto);
        push(userDevice, PubTopicEnum.PUB_DEVICE_ADD, jsonObject);
    }

    @Override
    public void scanDevice(String masterProductId, String productId, String masterDeviceId) {
        JSONObject params = new JSONObject();
        params.put("productId", productId);
        params.put("masterDeviceId", masterDeviceId);
        push(UserDevice.builder()
                .productId(masterProductId)
                .physicalDeviceId(masterDeviceId)
                .build(), PubTopicEnum.PUB_DEVICE_SCAN, params);
    }

    @Override
    public void pushDevice(UserDevice userDevice, String masterProductId, String masterDeviceId) {
        UserDeviceUpdate userDeviceUpdate = new UserDeviceUpdate();
        userDeviceUpdate.setDeviceId(userDevice.getDeviceId());
        userDeviceUpdate.setMasterDeviceId(userDevice.getMasterDeviceId());
        userDeviceUpdate.setPhysicalDeviceId(userDevice.getPhysicalDeviceId());
        userDeviceUpdate.setStatus(userDevice.getStatus());
        userDeviceUpdate.setProductId(userDevice.getProductId());
        userDeviceUpdate.setMasterProductId(userDevice.getMasterProductId());
        userDevice.setProductId(masterProductId);
        userDevice.setPhysicalDeviceId(masterDeviceId);
        push(userDevice, PubTopicEnum.PUB_DEVICE_UPDATE, userDeviceUpdate);
    }

    @Override
    public void searchNewMesh(UserDevice masterUserDevice, String productId) {
        if (StringUtils.isNotBlank(productId)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("productId", masterUserDevice.getProductId());
            push(masterUserDevice, PubTopicEnum.TOPOLOGY_MESH_UNBIND, jsonObject);
            return;
        }
        push(masterUserDevice, PubTopicEnum.TOPOLOGY_MESH_UNBIND, null);
    }

    @Override
    public void push(UserDevice userDevice, PubTopicEnum topicEnum, Object params) {
        String topic = PubTopicEnum.handlerTopic(topicEnum, userDevice.getProductId(), userDevice.getPhysicalDeviceId());

        MqttParamDto paramDto = null;

        // 旗云酒店-时间固定
        if ("1.3".equals(userDevice.getHardWareVersion())) {
            paramDto = MqttParamDto.builder()
                    .id(IdUtil.simpleUUID())
                    .time(1693917705000L)
                    .data(params)
                    .build();
        } else {
            paramDto = MqttParamDto.builder()
                    .id(IdUtil.simpleUUID())
                    .time(DateUtil.current())
                    .data(params)
                    .build();
        }

        MQTT.publish(topic, JSON.toJSONString(paramDto));
        log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(paramDto));
        //mqtt下行
        /*operationLogService.save(OperationLog.builder()
                .action(new Byte("0"))
                .deviceId(userDevice.getDeviceId())
                .productId(userDevice.getProductId())
                .productType(userDevice.getProductType())
                .customName(userDevice.getCustomName())
                .userId(userDevice.getUserId())
                .masterDeviceId(userDevice.getMasterDeviceId())
                .signalType(userDevice.getSignalType())
                .params(JSON.toJSONString(paramDto))
                .remark(topic)
                .build());*/
    }

    public void pushCase(UserDevice userDevice, PubTopicEnum topicEnum, Object params, JSONObject valueJson) {
        String topic = PubTopicEnum.handlerTopic(topicEnum, userDevice.getProductId(), userDevice.getPhysicalDeviceId());
        MqttParamDto paramDto = MqttParamDto.builder()
                .id(IdUtil.simpleUUID())
                .time(DateUtil.current())
                .data(params)
                .build();
        String data = JSON.toJSONString(paramDto);
        JSONObject respJson = JSONObject.parseObject(data);

        respJson.getJSONObject("data").getJSONArray("properties").getJSONObject(0).put("value", valueJson);
        data = JSON.toJSONString(respJson);
        MQTT.publish(topic, data);
        log.info("Mqtt-Send-CASE:" + topic + "=" + data);
        //mqtt下行
       /* operationLogService.save(OperationLog.builder()
                .action(new Byte("0"))
                .deviceId(userDevice.getDeviceId())
                .productId(userDevice.getProductId())
                .productType(userDevice.getProductType())
                .customName(userDevice.getCustomName())
                .userId(userDevice.getUserId())
                .masterDeviceId(userDevice.getMasterDeviceId())
                .signalType(userDevice.getSignalType())
                .params(JSON.toJSONString(paramDto))
                .remark(topic)
                .build());*/
    }


//    //创建一个定时任务判断有没有发送成功
//    public void ifSend(String topic,MqttParamDto paramDto){
//        //缓冲获取
//        //发送时候存储
//        //cacheService.addSeconds(topic, null, 60 * 5 * 1000L);
//        //topic返回时候存储
//        //cacheService.addSeconds(topic, topic, 60 * 5 * 1000L);
//
//        jobFeignClient.saveTopicJob(TopicJobParamDto.builder()
//                .topic(topic).build());
//        //定时任务时候获取是否成功
//        String redisTopic = cacheService.get(topic);
//        if (redisTopic!=null){
//            //成功
//        }else{
//            MQTT.publish(topic, JSON.toJSONString(paramDto));
//            //重发
//            cacheService.addSeconds(topic, null, 60 * 5 * 1000L);
//        }
//    }
}

