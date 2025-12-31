package com.lj.iot.watchnetty.handle;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.WatchHealth;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.fegin.websocket.WsFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 * 健康数据-血压/多数据源
 *
 * @author tyj
 */
@Slf4j
@Component("tcpHandle_bloodMany")
public class TcpHandleBloodMany implements TcpHandle {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Resource
    private WsFeignClient wsFeignClient;

    @Autowired
    private IWatchHealthService watchHealthService;

    @Autowired
    private IWatchSosService watchSosService;

    @Autowired
    private IWatchSettingService watchSettingService;

    @Resource
    ISystemMessagesService systemMessagesService;


    @Override
    public String handle(UserDevice userDevice, Map<String, String> dataMap) {
        log.info("bloodMany={}", userDevice);

        JSONObject respJson = new JSONObject();
        respJson.put("deviceId", userDevice.getDeviceId());
        respJson.put("type", "bloodMany");

        UserDevice updateDevice = UserDevice.builder()
                .deviceId(userDevice.getDeviceId()).build();

        if (dataMap.get("oxygen") != null) {
            respJson.put("oxygen", Integer.valueOf(dataMap.get("oxygen")));
            updateDevice.setBloodOxygen(Integer.valueOf(dataMap.get("oxygen")));

            watchHealthService.save(WatchHealth.builder()
                    .deviceId(userDevice.getDeviceId())
                    .createTime(LocalDateTime.now())
                    .healthValue(dataMap.get("oxygen"))
                    .healthType(1)
                    .build());

            watchSosService.daring(userDevice.getDeviceId(),1,dataMap.get("oxygen"));
        }

        if (dataMap.get("blood") != null) {
            respJson.put("blood", dataMap.get("blood"));
            updateDevice.setBloodPressure(dataMap.get("blood"));

            watchHealthService.save(WatchHealth.builder()
                    .deviceId(userDevice.getDeviceId())
                    .createTime(LocalDateTime.now())
                    .healthValue(dataMap.get("blood"))
                    .healthType(0)
                    .build());

            watchSosService.daring(userDevice.getDeviceId(),0,dataMap.get("blood"));
        }

        if (dataMap.get("heart") != null) {
            respJson.put("heart", Integer.valueOf(dataMap.get("heart")));
            updateDevice.setHeartRate(Integer.valueOf(dataMap.get("heart")));

            watchHealthService.save(WatchHealth.builder()
                    .deviceId(userDevice.getDeviceId())
                    .createTime(LocalDateTime.now())
                    .healthValue(dataMap.get("heart"))
                    .healthType(2)
                    .build());


            watchSosService.daring(userDevice.getDeviceId(),2,dataMap.get("heart"));
        }

        List<String> userIds = Lists.newArrayList();
        userIds.add(userDevice.getUserId());

        wsFeignClient.appSend(WsResultVo.FAILURE(userIds, null, RedisTopicConstant.TOPIC_CHANNEL_WATCH_DATA, null, respJson.toJSONString()));


        userDeviceService.updateById(updateDevice);

        return null;
    }


}
