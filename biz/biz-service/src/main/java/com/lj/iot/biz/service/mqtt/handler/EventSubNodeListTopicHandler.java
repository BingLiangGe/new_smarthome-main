package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserDeviceNode;
import com.lj.iot.biz.db.smart.service.IUserDeviceNodeService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.util.ToPinYin;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 节点列表
 *
 * @author tyj
 * @date 2024-2-19 14:21:25
 */
@Slf4j
@Component
public class EventSubNodeListTopicHandler extends AbstractTopicHandler {

    public EventSubNodeListTopicHandler() {
        setSupportTopic(SubTopicEnum.PUB_SUB_NODE_LIST);
    }

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IUserDeviceNodeService nodeService;

    @Override
    public void handle(HandleMessage message) {

        UserDevice masterUserDevice = userDeviceService.getById(message.getTopicDeviceId());

        ValidUtils.isNullThrow(masterUserDevice, "设备不存在");

        log.info("进入节点列表,deviceId=#{}",
                masterUserDevice.getDeviceId());
        JSONArray dataArray = message.getBody().getJSONArray("data");

        String netkey = message.getBody().getString("netkey");

            nodeService.remove(new QueryWrapper<>(UserDeviceNode.builder()
                    .masterDeviceId(masterUserDevice.getDeviceId()).build()));

        List<UserDeviceNode> list = dataArray.toJavaList(UserDeviceNode.class);

        list.forEach(e->{
            e.setCreateTime(LocalDateTime.now());
            e.setMasterDeviceId(masterUserDevice.getMasterDeviceId());
            if (netkey != null){
                e.setNetkey(netkey);
            }
        });

        nodeService.saveBatch(list);
    }
}
