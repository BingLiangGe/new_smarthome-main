package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.MqttOtaDto;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.IOperationLogService;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizDeviceService;
import com.lj.iot.biz.service.BizProductUpgrade;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.redis.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class ServiceLoginTokenReplyTopicHandler extends AbstractTopicHandler {

    public ServiceLoginTokenReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.SERVICE_LOGIN_TOKEN_REPLY);
    }

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private MqttPushService mqttPushService;

    @Autowired
    private IUserAccountService userAccountService;

    @Resource
    private ICacheService cacheService;

    /**
     * 云端删除设备的拓扑关系,返回暂时不需要处理
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {
        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice
                .builder()
                .deviceId(message.getTopicDeviceId())
                .build()));
        UserAccount user = userAccountService.getById(userDevice.getUserId());
        log.info(JSONObject.toJSONString(user));
        if (user != null) {
            String token = cacheService.get("app" + "session:account:token:" + user.getId());

            //把token mqtt推送到硬件设备
            mqttPushService.pushLoginToken(userDevice, LoginVo.<UserAccount>builder()
                    .account(user.getMobile())
                    .userInfo(user)
                    .token(token)
                    .params(userDevice.getHomeId())
                    .build());
        }
    }

}
