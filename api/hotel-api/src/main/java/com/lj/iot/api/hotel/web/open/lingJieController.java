package com.lj.iot.api.hotel.web.open;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.vo.FloorHomeVo;
import com.lj.iot.biz.db.smart.entity.HotelDeviceTime;
import com.lj.iot.biz.db.smart.entity.HotelFloor;
import com.lj.iot.biz.db.smart.entity.IrData;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.SendDataDto;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.ValidUtils;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/open/lingJie")
public class lingJieController {
    @Autowired
    IUserDeviceService userDeviceService;
    @Autowired
    BizUserDeviceService bizUserDeviceService;
    @Autowired
    IHotelFloorService hotelFloorService;
    @Autowired
    IHotelFloorHomeService hotelFloorHomeService;
    @Autowired
    IHotelDeviceTimeService hotelDeviceTimeService;
    @Autowired
    IIrDataService iIrDataService;

    @Autowired
    private ICacheService cacheService;


    /**
     * 发送音频文件
     * <p>
     * 45: 四个朋友 - 欢迎光临
     * 46:四个朋友-订单结束
     * 47:四个朋友-剩余15分钟提疆
     * 48:通用版-订单结束
     * 49:通用版-欢迎光临
     * 50:通用版-剩余15分钟提醒
     * 51:四个朋友-5分钟提醒
     * 52:通用版-5分钟提醒
     *
     * @return
     */
    @RequestMapping("/audioFrequency")
    public CommonResultVo<String> sendVolmn(String masterDeviceId, String code) {

        ValidUtils.isNullThrow(masterDeviceId, "masterDeviceId必传");
        ValidUtils.isNullThrow(code, "code必传");

        UserDevice userDevice = userDeviceService.getById(masterDeviceId);
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        String url = "https://img.lj-smarthome.com/MP3/" + code + ".mp3";


        //String url = "http://47.100.238.205:8888/MP3/" + code + ".mp3";

        String result = "操作完成";

        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, userDevice.getProductId(), masterDeviceId);
        String resp = JSON.toJSONString(new HashMap() {{
            put("id", masterDeviceId);
            put("url", url);
            put("code", code);
        }});
        MQTT.publish(topic, resp);
        log.info("Mqtt-Send:" + topic + "=" + resp);

        /*cacheService.del("app" + RedisConstant.wait_device + "player_" + masterDeviceId);
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
        }).start();*/
        return CommonResultVo.SUCCESS(result);
    }

    /**
     * 重启主控
     *
     * @param deviceId
     * @return
     */
    @RequestMapping("/resetMasterDevice")
    public CommonResultVo<String> resetMasterDevice(@RequestBody DeviceIdDto deviceId) {

        UserDevice userDevice = userDeviceService.getById(deviceId.getDeviceId());

        ValidUtils.isNullThrow(userDevice, "设备不存在");

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
        return CommonResultVo.SUCCESS();
    }

    /**
     * 设置音量
     *
     * @return
     */
    @RequestMapping("/settingVolume")
    public CommonResultVo<String> settingVolume(@RequestBody DeviceVolumeVo vo) {
        log.info("deviceId={},value={}", vo.getDeviceId(), vo.getValue());

        UserDevice userDevice = userDeviceService.getById(vo.getDeviceId());

        ValidUtils.isNullThrow(userDevice, "设备不存在");


        if (vo.getValue() < 0 || vo.getValue() > 100) {
            ValidUtils.isNullThrow(null, "音量仅支持0-100范围调节");
        }

        if (!userDevice.getMasterDeviceId().equals(vo.getDeviceId())) {
            ValidUtils.isNullThrow(null, "非主控不可执行该操作");
        }


        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_SUB_DEVICE_NETWORK, userDevice.getProductId(), userDevice.getDeviceId());

        JSONObject respJson = new JSONObject();

        respJson.put("vol", vo.getValue());
        MQTT.publish(topic, respJson.toJSONString());
        log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(respJson));

        userDeviceService.updateById(UserDevice.builder()
                .deviceId(userDevice.getDeviceId())
                .volume(vo.getValue()).build());

        return CommonResultVo.SUCCESS();
    }


    @RequestMapping("device_list")
    public CommonResultVo<List<Map>> getDeviceList(@RequestParam("userId") String userId) {
        List<Map> listResult = new ArrayList<>();
        QueryWrapper<UserDevice> objectQueryWrapper = new QueryWrapper<>(UserDevice.builder().userId(userId).build()).select("master_device_id").groupBy("master_device_id");
        List<UserDevice> userDevices = userDeviceService.list(objectQueryWrapper);
        Map masterMap = new HashMap();
        //每个主控查询下面的子设备列表
        for (UserDevice userDevice :
                userDevices) {
            QueryWrapper<UserDevice> wrapper = new QueryWrapper<>();
            wrapper.eq("master_device_id", userDevice.getMasterDeviceId());
            List<UserDevice> ls1 = userDeviceService.list(wrapper);
            masterMap.put(userDevice.getMasterDeviceId(), ls1);
        }
        listResult.add(masterMap);

        return CommonResultVo.SUCCESS(listResult);
    }

    @RequestMapping("floor_device_list")
    public CommonResultVo<List<Map>> getFloorDeviceList(@RequestParam("userId") String userId) {
        List<Map> listResult = new ArrayList<>();
        //先查账号下的楼层
        List<HotelFloor> floorList = hotelFloorService.list(new QueryWrapper<>(HotelFloor.builder().hotelUserId(userId).build()));
        if (floorList.size() > 0) {
            for (HotelFloor hotelFloor :
                    floorList) {
                Map floorMap = new HashMap();
                List<FloorHomeVo> floorHomeVos = hotelFloorHomeService.listFloorHomeByFloorId(hotelFloor.getId(), hotelFloor.getHotelUserId());
                List<Map> homeList = new ArrayList<>();
                //房间下面找设备列表
                for (FloorHomeVo floorHomeVo :
                        floorHomeVos) {
                    Map homeMap = new HashMap();
                    List<UserDevice> deviceList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().homeId(floorHomeVo.getHomeId()).build()));
                    homeMap.put("homeName", floorHomeVo.getHomeName());
                    homeMap.put("homeId", floorHomeVo.getHomeId());
                    homeMap.put("list", deviceList);
                    homeList.add(homeMap);
                }
                floorMap.put("floorName", hotelFloor.getFloorName());
                floorMap.put("floorId", hotelFloor.getId());
                floorMap.put("list", homeList);
                listResult.add(floorMap);
            }
        } else {
            ValidUtils.isFalseThrow(false, "未查到该用户下的门店信息");
        }
        return CommonResultVo.SUCCESS(listResult);
    }

    @RequestMapping("device_control")
    public CommonResultVo<String> getDeviceList(@RequestParam("userId") String userId, @RequestParam("deviceId") String deviceId, @RequestParam("code") Integer code, @RequestParam("time") String time) {
        String result = "操作完成";
        UserDevice userDevice = userDeviceService.getById(deviceId);
        ValidUtils.isNullThrow(userDevice, "未查到该设备信息");
        ThingModel thingModel = userDevice.getThingModel();
        List<ThingModelProperty> properties = thingModel.getProperties();
        List<ThingModelProperty> onOffList = properties.stream().filter(it -> it.getIdentifier().contains("powerstate")).collect(Collectors.toList());
        SendDataDto sendDataDto = new SendDataDto();
        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, userDevice.getProductId(), deviceId);
        switch (code) {
            case 0: //关设备
                onOffList.get(0).setValue(0);
                sendDataDto.setDeviceId(deviceId);
                sendDataDto.setKeyCode("close");
                if (userDevice.getProductId().equals("2000000100")) { //空调特殊处理
                    onOffList.addAll(properties.subList(1, properties.size()));
                }
                if (userDevice.getProductId().equals("9337719") || userDevice.getProductId().equals("9337720")) {
                    result = "门锁不支持关命令！";
                }
                thingModel.setProperties(onOffList);
                sendDataDto.setThingModel(thingModel);
                bizUserDeviceService.sendData(sendDataDto, OperationEnum.THIRD_PARTY);
                break;
            case 1: //开设备
                onOffList.get(0).setValue(1);
                sendDataDto.setDeviceId(deviceId);
                sendDataDto.setKeyCode("open");
                if (userDevice.getProductId().equals("2000000100")) { //空调特殊处理
                    onOffList.addAll(properties.subList(1, properties.size()));
                }

                thingModel.setProperties(onOffList);
                sendDataDto.setThingModel(thingModel);
                bizUserDeviceService.sendData(sendDataDto, OperationEnum.THIRD_PARTY);
                break;
            case 2: //删除设备
                DeviceIdDto deviceIdDto = new DeviceIdDto();
                deviceIdDto.setDeviceId(deviceId);
                bizUserDeviceService.delete(deviceIdDto, userId);
                break;
            case 3: //主控全禁止
                userDeviceService.saveOrUpdate(UserDevice.builder().deviceId(deviceId).isDel(true).build());
                MQTT.publish(topic, JSON.toJSONString(new HashMap() {{
                    put("id", deviceId);
                    put("enable", 1);
                }}));
                break;
            case 4: //主控全开启
                userDeviceService.saveOrUpdate(UserDevice.builder().deviceId(deviceId).isDel(false).build());
                MQTT.publish(topic, JSON.toJSONString(new HashMap() {{
                    put("id", deviceId);
                    put("enable", 0);
                }}));
                break;
            case 5: //到期或者续费时间设置
                HotelDeviceTime hotelDeviceTime = HotelDeviceTime.builder().id(deviceId).datetime(LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build();
                hotelDeviceTimeService.saveOrUpdate(hotelDeviceTime);
                Duration duration = Duration.between(LocalDateTime.now(), LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                hotelDeviceTime.setLongDatetime(duration.toMillis());
                MQTT.publish(topic, JSON.toJSONString(hotelDeviceTime));
                break;
        }
        return CommonResultVo.SUCCESS(result);
    }

    @RequestMapping("sendFileCode")
    public CommonResultVo<String> sendFileCode(@RequestParam("userId") String userId, @RequestParam("deviceId") String deviceId, @RequestParam("code") Integer code) {
        String result = "操作完成";
        UserDevice userDevice = userDeviceService.getById(deviceId);
        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, userDevice.getProductId(), deviceId);
        MQTT.publish(topic, JSON.toJSONString(new HashMap() {{
            put("id", deviceId);
            put("code", code);
        }}));
        return CommonResultVo.SUCCESS(result);
    }

    /**
     * 给设备端提供吸烟灯的控制码
     */
    @RequestMapping("getSmokingCode")
    public CommonResultVo<List<Map>> getSmokingCode() {
        List<Map> result = new ArrayList<>();
        List<IrData> list = iIrDataService.list(new QueryWrapper<>(IrData.builder().fileId("999970").build()));
        Map map = new HashMap<>() {{
            put(0, "open/close");
            put(1, "stop");
            put(2, "lowSpeed");
            put(3, "middleSpeed");
            put(4, "highSpeed");
            put(5, "mute");
            put(6, "light");
            put(8, "up");
            put(9, "down");
        }};
        for (IrData irData :
                list) {
            Map mapIr = new HashMap();
            mapIr.put("keyName", map.get(Integer.parseInt(irData.getDataIndex())));
            mapIr.put("code", irData.getIrData());
            result.add(mapIr);
        }

        return CommonResultVo.SUCCESS(result);
    }

}
