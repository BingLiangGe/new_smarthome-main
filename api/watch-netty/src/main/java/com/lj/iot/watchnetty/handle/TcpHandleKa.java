package com.lj.iot.watchnetty.handle;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.fegin.websocket.WsFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Slf4j
@Component("tcpHandle_KA")
public class TcpHandleKa implements TcpHandle {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Resource
    private WsFeignClient wsFeignClient;

    @Override
    public String handle(UserDevice userDevice, Map<String, String> dataMap) {
        log.info("ka={}", userDevice);

        String[] data = dataMap.get("data").split(",");

        // 翻滚次数
        userDevice.getThingModel().getProperties().get(0).setValue(data[3]);
        // 步数
        userDevice.getThingModel().getProperties().get(1).setValue(data[2]);
        // 电量
        userDevice.getThingModel().getProperties().get(2).setValue(data[4].split("\\]")[0]);

        JSONObject respJson = new JSONObject();
        respJson.put("deviceId", userDevice.getDeviceId());
        respJson.put("type", "ka");
        respJson.put("rollNumber", Integer.valueOf(data[3]));
        respJson.put("stepNumber", Integer.valueOf(data[2]));
        respJson.put("electricity", data[4].split("\\]")[0]);

        List<String> userIds = Lists.newArrayList();
        userIds.add(userDevice.getUserId());

        wsFeignClient.appSend(WsResultVo.FAILURE(userIds, null, RedisTopicConstant.TOPIC_CHANNEL_WATCH_DATA, null, respJson.toJSONString()));



        userDeviceService.updateById(UserDevice.builder()
                .deviceId(userDevice.getDeviceId())
                .statusTime(LocalDateTime.now())
                .status(true)
                .thingModel(userDevice.getThingModel()).build());

        return "[DW*" + userDevice.getDeviceId() + "*0002*KA]";
    }
}
