package com.lj.iot.biz.service.mqtt;

import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.mqtt.client.core.HandleMessage;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
public interface ITopicHandler {

    void entrance(HandleMessage message);

    /**
     * 设置支持topic
     *
     * @param topic
     */
    void setSupportTopic(SubTopicEnum topic);

    SubTopicEnum getSupportTopic();

    boolean isSupport(SubTopicEnum topic);

    void handle(HandleMessage message);

    void successReply(HandleMessage message);

    void errorReply(HandleMessage message, CommonException e);
}
