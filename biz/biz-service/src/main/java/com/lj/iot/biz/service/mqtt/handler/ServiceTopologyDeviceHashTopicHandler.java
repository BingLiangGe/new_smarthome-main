package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.MqttOtaDto;
import com.lj.iot.biz.base.vo.DeviceWsVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.IDeviceService;
import com.lj.iot.biz.db.smart.service.IOperationLogService;
import com.lj.iot.biz.db.smart.service.ISystemMessagesService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizDeviceService;
import com.lj.iot.biz.service.BizProductUpgrade;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.FourFriendsUtil;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class ServiceTopologyDeviceHashTopicHandler extends AbstractTopicHandler {

    public ServiceTopologyDeviceHashTopicHandler() {
        setSupportTopic(SubTopicEnum.PUB_DEVICE_HASH);
    }

    @Autowired
    private IUserDeviceService userDeviceService;
    @Autowired
    private BizProductUpgrade bizProductUpgrade;

    @Autowired
    private IOperationLogService operationLogService;

    @Autowired
    private BizDeviceService bizDeviceService;
    @Resource
    ISystemMessagesService systemMessagesService;

    @Resource
    IDeviceService deviceService;

    @Autowired
    private BizWsPublishService bizWsPublishService;

    @Autowired
    private FourFriendsUtil fourFriendsUtil;

    /**
     * 扫描
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {

        log.info(">ServiceTopologyDeviceHashTopicHandler_handle,message={}", message);
        JSONObject jsonObject = message.getBody().getJSONObject("data");
        Device device = deviceService.getById(jsonObject.getString("deviceId"));

        UserDevice masterDevice = userDeviceService.getById(message.getTopicDeviceId());

        ValidUtils.isNullThrow(masterDevice, "设备不存在");

        // 三元组不存在
        if (device == null) {

            if ("1.3".equals(masterDevice.getHardWareVersion())) {
                fourFriendsUtil.sendDevice(null, null, null, -1);
            }

            bizWsPublishService.publishAllMemeberFailure(
                    RedisTopicConstant.TOPIC_CHANNEL_DEVICE_SCAN,
                    jsonObject.getString("deviceId") + ":三元组不存在");
            return;
        }
        log.info("ServiceTopologyDeviceHashTopicHandler,device={}", device);
        String productId = jsonObject.getString("productId");
        if (productId != null) {

            if ("1.3".equals(masterDevice.getHardWareVersion())) {
                fourFriendsUtil.sendDevice(productId, device.getId(), masterDevice.getDeviceId(), 0);
            }

            log.info("ServiceTopologyDeviceHashTopicHandler,productId={}", productId);
            if (productId.equals("9337720") | productId.equals("9337719")) {
                //特殊处理
                Device one = deviceService.getOne(new QueryWrapper<>(Device.builder().productId(productId).id(device.getId()).build()));
                log.info("ServiceTopologyDeviceHashTopicHandler,one={}", one);
                if (one != null) {
                    List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().masterProductId(message.getTopicProductId()).deviceId(message.getTopicDeviceId()).build()));
                    log.info("ServiceTopologyDeviceHashTopicHandler,list={},masterP={},masterDevice={}", list.size(), message.getTopicProductId(), message.getTopicDeviceId());
                    bizWsPublishService.publishAllMemberByHomeId(
                            RedisTopicConstant.TOPIC_CHANNEL_DEVICE_SCAN,
                            list.get(0).getHomeId(),
                            one);
                }

            } else {
                //不过滤
                List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().masterProductId(message.getTopicProductId()).deviceId(message.getTopicDeviceId()).build()));
                bizWsPublishService.publishAllMemberByHomeId(
                        RedisTopicConstant.TOPIC_CHANNEL_DEVICE_SCAN,
                        list.get(0).getHomeId(),
                        device);
            }
        } else {
            //不过滤
            List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().masterProductId(message.getTopicProductId()).deviceId(message.getTopicDeviceId()).build()));
            bizWsPublishService.publishAllMemberByHomeId(
                    RedisTopicConstant.TOPIC_CHANNEL_DEVICE_SCAN,
                    list.get(0).getHomeId(),
                    device);
        }

    }
}
