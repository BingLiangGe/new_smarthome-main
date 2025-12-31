package com.lj.iot.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.MqttOtaDto;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IHotelUserAccountService;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统topic  设备上线
 *
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class KafkaEventSYSConnectedTopicHandler extends AbstractTopicHandler {

    public KafkaEventSYSConnectedTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_SYS_CONNECTED);
    }

    @Resource
    private IUserDeviceService userDeviceService;

    @Resource
    @Lazy
    private BizWsPublishService bizWsPublishService;

    @Resource
    private IUserAccountService userAccountService;

    @Resource
    private IHotelUserAccountService hotelUserAccountService;

    @Resource
    private MqttPushService mqttPushService;

    /**
     * 设备上线监听
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {
        String topicDeviceId = message.getTopicDeviceId();

        //判断是不是锁 特殊处理
        UserDevice ifLock = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(message.getTopicDeviceId())
                .build()));
        /*if (ifLock.getProductId().equals("9337719") | ifLock.getProductId().equals("9337720")) {
            //特殊处理
            List<UserDevice> deviceId = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().masterDeviceId(ifLock.getMasterDeviceId()).build()));
            //状态同步
            for (int i = 0; i < deviceId.size(); i++) {
                //把子设备设置成主控的状态
                UserDevice userDevice = deviceId.get(i);
                userDevice.setStatus(true);
                userDeviceService.updateById(userDevice);
            }

            return;
        }*/

        //主控设备下的所有离线设备上线（排除mesh类型及虚设备）
        UserDevice masterDevice = userDeviceService.getById(topicDeviceId);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 9);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

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

              /*  QueryWrapper<UserDevice> wrapper = new QueryWrapper<>();
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
                log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(resultList));*/


                String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_SUB_DEVICE_NETWORK, masterDevice.getProductId(), masterDevice.getDeviceId());
                MqttOtaDto mqttOtaDto = MqttOtaDto.builder().details("WIFI").build();
                MQTT.publish(topic, JSON.toJSONString(mqttOtaDto));
                log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(mqttOtaDto));

            }
        }).start();


        List<String> deviceIdList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .masterDeviceId(masterDevice.getDeviceId())
                .build())).stream().filter(device -> {
            //mesh设备和虚设备
//            if (device.getSignalType().equals(SignalEnum.MESH.getCode())||device.getSignalType().equals(SignalEnum.INVENTED.getCode())) {
//                return false;
//            }
            if (device.getSignalType().equals(SignalEnum.INVENTED.getCode())) {
                return false;
            }
            return !device.getStatus();
        }).map(UserDevice::getDeviceId).collect(Collectors.toList());

        if (deviceIdList.size() == 0) {
            return;
        }
        userDeviceService.update(UserDevice.builder()
                .status(true)
                .statusTime(LocalDateTime.now())
                .build(), new QueryWrapper<>(UserDevice.builder()
                .build())
                .in("master_device_id", deviceIdList));

        bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_ONLINE, masterDevice.getHomeId(), deviceIdList);
        bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_ONLINE_SUBSIDIARY, masterDevice.getHomeId(), masterDevice.getMasterDeviceId());

    }
}
