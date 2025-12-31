package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.redis.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class ServiceTopologyAddReplyTopicHandler extends AbstractTopicHandler {
    @Resource
    BizUserDeviceService bizUserDeviceService;

    @Autowired
    private ICacheService cacheService;

    public ServiceTopologyAddReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.SERVICE_TOPOLOGY_ADD_REPLY);
    }

    /**
     * 云端新增设备的拓扑关系,返回
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {
        JSONObject body = message.getBody();
        log.debug("TopoAdd body:{}", body.toJSONString());

        //主控会推两条消息上来，防后台没收到。这里去重
        String key = "mqttAddDevice" + body.getString("id");
        Integer flag = cacheService.get(key);
        if (flag != null) {
            return;
        }
        cacheService.addSeconds(key, 1, 5);
        //设备添加逻辑处理
        bizUserDeviceService.topologyAddDevice(message);
    }
}
