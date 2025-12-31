package com.lj.iot.handler;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.redis.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class KafkaServiceLoginTokenReplyTopicHandler extends AbstractTopicHandler {

    public KafkaServiceLoginTokenReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.SERVICE_LOGIN_TOKEN_REPLY);
    }

    @Resource
    private IUserDeviceService userDeviceService;

    @Resource
    private MqttPushService mqttPushService;

    @Resource
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
        log.info(JSON.toJSONString(user));
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
