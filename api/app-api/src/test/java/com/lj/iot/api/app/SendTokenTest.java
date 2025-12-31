package com.lj.iot.api.app;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.MqttParamDto;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IHotelUserAccountService;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@SpringBootTest
public class SendTokenTest {

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;

    @Resource
    MqttPushService mqttPushService;


    @Autowired
    private IUserDeviceService userDeviceService;

    @Test
    public void resetDevice() {

        List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .productType("gatway_872")
                .productId("213350486")
                .status(true).build()));
        int count = 0;

        while (count < list.size()) {
            log.info("list.size()={}",list.size());
            List<UserDevice> newList = list.subList(count, 10);
            log.info("list.size()={}",list.size());
            for (UserDevice userDevice : newList
            ) {
                log.info("重启={}", userDevice);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("productId", userDevice.getProductId());
                jsonObject.put("deviceId", userDevice.getDeviceId());

                String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_RESTART, userDevice.getProductId(), userDevice.getMasterDeviceId());
                /*MqttParamDto paramDto = MqttParamDto.builder()
                        .id(IdUtil.simpleUUID())
                        .time(DateUtil.current())
                        .data(jsonObject)
                        .build();
                MQTT.publish(topic, JSON.toJSONString(paramDto));
                log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(paramDto));*/
                log.info("");
            }
            count += 10;
            try {
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("");
            log.info("");
            log.info("");
        }
    }

    @Test
    public void send() {

        List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .productType("gatway_872")
                .productId("213350486")
                .status(true).build()));
        log.info("size-{},",list.size());
        list.forEach(masterDevice -> {

            log.info("------------------------------------------------下发send,size={}", list.size());
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
        });
    }
}
