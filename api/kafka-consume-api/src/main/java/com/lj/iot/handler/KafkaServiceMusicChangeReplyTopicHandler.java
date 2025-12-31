package com.lj.iot.handler;

import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class KafkaServiceMusicChangeReplyTopicHandler extends AbstractTopicHandler {

    public KafkaServiceMusicChangeReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.SERVICE_MUSIC_CHANGE_REPLY);
    }

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
