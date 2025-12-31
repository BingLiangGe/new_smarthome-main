package com.lj.iot.biz.service.mqtt.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.db.smart.entity.MusicMenu;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IMusicMenuService;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.redis.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class ServiceMusicChangeReplyTopicHandler extends AbstractTopicHandler {

    public ServiceMusicChangeReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.SERVICE_MUSIC_CHANGE_REPLY);
    }

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private MqttPushService mqttPushService;

    @Autowired
    private IUserAccountService userAccountService;

    @Resource
    private ICacheService cacheService;

    @Autowired
    private BizWsPublishService bizWsPublishService;

    @Resource
    IMusicMenuService musicMenuService;

    /**
     * 云端删除设备的拓扑关系,返回   暂时不需要处理
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {
        //UserDevice userDevice = userDeviceService.getById(message.getTopicDeviceId());
        //List<MusicMenu> list = musicMenuService.findUserid(userDevice.getDeviceId());
       // bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_MUSIC_MENU,userDevice.getHomeId(),list);
    }
}
