package com.lj.iot.watchnetty.handle;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.WatchHealth;
import com.lj.iot.biz.db.smart.entity.WatchSos;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.db.smart.service.IWatchHealthService;
import com.lj.iot.biz.db.smart.service.IWatchSosService;
import com.lj.iot.fegin.websocket.WsFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 * 报警数据上报
 *
 * @author tyj
 */
@Slf4j
@Component("tcpHandle_AL")
public class TcpHandleAL implements TcpHandle {

    @Autowired
    private IWatchSosService watchSosService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IWatchHealthService watchHealthService;

    @Resource
    private WsFeignClient wsFeignClient;

    @Override
    public String handle(UserDevice userDevice, Map<String, String> dataMap) {
        log.info("AL={}", userDevice);

        String datas[] = dataMap.get("data").split(",");

        String type = datas[16];

        log.info("AL_TYPE={}", type);

        JSONObject respJson = new JSONObject();
        respJson.put("deviceId", userDevice.getDeviceId());
        respJson.put("type", "AL");

        // SOS 报警
        if ("00010000".equals(type)) {
            watchSosService.save(WatchSos.builder()
                    .createTime(LocalDateTime.now())
                    .sosMobile("默认")
                    .sosType(4)
                    .deviceId(userDevice.getDeviceId())
                    .build());
        } else if ("00100008".equals(type)) { // 戴上手表

            userDeviceService.updateById(UserDevice.builder()
                    .deviceId(userDevice.getDeviceId())
                    .watchStatus(1)
                    .thingModel(userDevice.getThingModel()).build());

            watchHealthService.save(WatchHealth.builder()
                    .deviceId(userDevice.getDeviceId())
                    .createTime(LocalDateTime.now())
                    .healthValue("1")
                    .healthType(4)
                    .build());

            respJson.put("watchStatus", 1);

        } else if ("00100000".equals(type)) { // 取下手表
            userDeviceService.updateById(UserDevice.builder()
                    .deviceId(userDevice.getDeviceId())
                    .watchStatus(0)
                    .heartRate(0)
                    .temperature(new BigDecimal("0"))
                    .bloodPressure("0,0")
                    .bloodOxygen(0)
                    .thingModel(userDevice.getThingModel()).build());

            watchHealthService.save(WatchHealth.builder()
                    .deviceId(userDevice.getDeviceId())
                    .createTime(LocalDateTime.now())
                    .healthValue("0")
                    .healthType(4)
                    .build());


            respJson.put("watchStatus", 0);
        }

        if (respJson.get("watchStatus") != null) {

            List<String> userIds = Lists.newArrayList();
            userIds.add(userDevice.getUserId());

            wsFeignClient.appSend(WsResultVo.FAILURE(userIds, null, RedisTopicConstant.TOPIC_CHANNEL_WATCH_DATA, null, respJson.toJSONString()));
        }
        return "[DW*" + userDevice.getDeviceId() + "*0002*AL]";
    }
}
