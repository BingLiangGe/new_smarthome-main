package com.lj.iot.biz.service.mqtt.handler;

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
public class ServiceTopologyDeleteReplyTopicHandler extends AbstractTopicHandler {

    public ServiceTopologyDeleteReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.SERVICE_TOPOLOGY_DELETE_REPLY);
    }

    /**
     * 云端删除设备的拓扑关系,返回   暂时不需要处理
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {

    }
}
