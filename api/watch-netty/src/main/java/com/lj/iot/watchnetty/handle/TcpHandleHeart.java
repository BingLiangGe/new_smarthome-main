package com.lj.iot.watchnetty.handle;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.db.smart.service.IWatchSosService;
import com.lj.iot.fegin.websocket.WsFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * 健康数据-心率
 *
 * @author tyj
 */
@Slf4j
@Component("tcpHandle_heart")
public class TcpHandleHeart implements TcpHandle {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Resource
    private WsFeignClient wsFeignClient;

    @Autowired
    private IWatchSosService watchSosService;

    @Override
    public String handle(UserDevice userDevice, Map<String, String> dataMap) {
        log.info("heart={}", userDevice);

        String[] data = dataMap.get("data").split(",");

        JSONObject respJson = new JSONObject();
        respJson.put("deviceId", userDevice.getDeviceId());
        respJson.put("type", "heart");
        respJson.put("rate", Integer.valueOf(data[1]));

        List<String> userIds = Lists.newArrayList();
        userIds.add(userDevice.getUserId());

        wsFeignClient.appSend(WsResultVo.FAILURE(userIds, null, RedisTopicConstant.TOPIC_CHANNEL_WATCH_DATA, null, respJson.toJSONString()));


        userDeviceService.updateById(UserDevice.builder()
                .deviceId(userDevice.getDeviceId())
                .heartRate(Integer.valueOf(data[1]))
                .build());

        watchSosService.daring(userDevice.getDeviceId(),2,data[1]);

        return null;
    }
}
