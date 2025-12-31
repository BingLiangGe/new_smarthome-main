package com.lj.iot.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import lombok.extern.slf4j.Slf4j;
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
public class KafkaEventTopologyListTopicHandler extends AbstractTopicHandler {

    public KafkaEventTopologyListTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_TOPOLOGY_LIST);
    }

    @Resource
    private IUserDeviceService userDeviceService;

    /**
     * 蓝牙设备列表
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {
        //查询数据
        List<UserDevice> userDeviceList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .masterDeviceId(message.getTopicDeviceId())
                .signalType(SignalEnum.MESH.getCode())
                .build()));

        JSONArray data = new JSONArray();

        for (UserDevice userDevice : userDeviceList) {
            JSONObject item = new JSONObject();
            item.put("productId", userDevice.getProductId());
            item.put("deviceId", userDevice.getDeviceId());
            data.add(item);
        }

        //返回值
        message.getBody().put("data", data);
    }
}
