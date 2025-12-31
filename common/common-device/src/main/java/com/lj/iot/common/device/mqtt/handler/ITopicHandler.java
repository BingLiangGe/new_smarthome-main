package com.lj.iot.common.device.mqtt.handler;

import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.device.enums.DeviceSubTopicEnum;
import com.lj.iot.common.device.mqtt.properties.HandleMessageVo;

/**
 *
 */
public interface ITopicHandler {

    void entrance(HandleMessageVo message);

    /**
     * 设置支持topic
     *
     * @param topic
     */
    void setSupportTopic(DeviceSubTopicEnum topic);

    DeviceSubTopicEnum getSupportTopic();

    boolean isSupport(DeviceSubTopicEnum topic);

    void handle(HandleMessageVo message);

    void successReply(HandleMessageVo message);

    void errorReply(HandleMessageVo message, CommonException e);
}
