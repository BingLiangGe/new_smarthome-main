package com.lj.iot.api.hotel.web.open;


import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lj.iot.biz.base.dto.MqttOtaDto;
import com.lj.iot.biz.base.dto.UserDeviceAddDto;
import com.lj.iot.biz.db.smart.entity.AppUpgrade;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IOperationLogService;
import com.lj.iot.biz.db.smart.service.IProductUpgradeService;
import com.lj.iot.biz.db.smart.service.IUpgradeRecordService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizAppUpgradeService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.ITopicHandler;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.util.ValidUtils;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * OTA
 *
 * @author hao
 * @Date 2023/2/17
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/open/ota")
public class OtaController {
    @Autowired
    private BizAppUpgradeService bizAppUpgradeService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private MqttPushService mqttPushService;

    @Autowired
    private IOperationLogService operationLogService;

    @Autowired
    private IProductUpgradeService productUpgradeService;

    @Autowired
    private IUpgradeRecordService upgradeRecordService;

    /**
     * APP软件升级
     *
     * @param versionCode
     * @return
     */
    @GetMapping("/findUrl")
    public CommonResultVo<AppUpgrade> findUrl(long versionCode) {
        AppUpgrade appUpgrade = bizAppUpgradeService.findByVersionCode(versionCode,1);

        if (appUpgrade == null) {
            return CommonResultVo.SUCCESS();
        }

        //目前版本最新
        if (versionCode == appUpgrade.getVersionCode()) {
            return CommonResultVo.SUCCESS();
        } else {
            return CommonResultVo.SUCCESS(appUpgrade);
        }

    }

    /**
     * 3326
     *
     * @param versionCode
     * @return
     */
    @GetMapping("/findMasterControlUrl")
    public CommonResultVo<AppUpgrade> findMasterControlUrl(long versionCode) {
        AppUpgrade appUpgrade = bizAppUpgradeService.findMasterControlUrl(versionCode);
        //目前版本最新
        if (versionCode == appUpgrade.getVersionCode()) {
            return CommonResultVo.SUCCESS();
        } else {
            return CommonResultVo.SUCCESS(appUpgrade);
        }

    }


    @GetMapping("/update_version")
    public CommonResultVo<String> update_version(@Valid UserDeviceAddDto dto) {
        //查询用户下的所有UserDevice
        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, dto.getProductId(), dto.getMasterDeviceId());

        log.info("update_version->userDeviceId={}", dto.getMasterDeviceId());
        UserDevice userDevice = userDeviceService.getById(dto.getMasterDeviceId());
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        UpdateWrapper updateWrapper = new UpdateWrapper();

        updateWrapper.eq("hard_ware_version", userDevice.getHardWareVersion());
        updateWrapper.set("success_count", 1);

        upgradeRecordService.update(updateWrapper);

        ProductUpgrade byMaxId = productUpgradeService.findNewUpgradeByProduct(dto.getProductId(), userDevice.getHardWareVersion(), userDevice.getSoftWareVersion());


        ValidUtils.isNullThrow(byMaxId, "当前产品没有升级包");
        //数据库找最新版本
        MqttOtaDto mqttOtaDto = MqttOtaDto.builder()
                .filepath(byMaxId.getVersionUrl())
                .hardwareversion(byMaxId.getHardWareVersion())
                .productId(byMaxId.getProductId())
                .softwareversion(byMaxId.getNewVersion())
                .deviceId(dto.getDeviceId())
                .build();
        MQTT.publish(topic, JSON.toJSONString(mqttOtaDto));

        return CommonResultVo.SUCCESS();
    }


    /**
     * 查询OTA升级
     *
     * @param dto
     * @return
     */
    @GetMapping("/check_version")
    public CommonResultVo<String> checkVersion(@Valid UserDeviceAddDto dto) {
        String masterDeviceId = dto.getMasterDeviceId();
        UserDevice byId = userDeviceService.getById(masterDeviceId);
        //当前设备版本
        String hardWareVersion = byId.getHardWareVersion();
        //查询数据最新的版本
        ProductUpgrade byMaxId = productUpgradeService.findByMaxId(dto.getProductId());
        ValidUtils.isNullThrow(byMaxId, "当前产品没有升级包");
        String hardWareVersion1 = byMaxId.getHardWareVersion();
        if (hardWareVersion1.equals(hardWareVersion1)) {
            //有最新版本
            return CommonResultVo.SUCCESS("yes");
        } else {
            //
            return CommonResultVo.SUCCESS("no");
        }
//        //查询用户下的所有UserDevice
//        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, dto.getProductId(), dto.getDeviceId());
//        //数据库找最新版本
//        MqttOtaDto mqttOtaDto = MqttOtaDto.builder()
//                .filepath(byMaxId.getVersionUrl())
//                .hardwareversion(byMaxId.getHardWareVersion())
//                .productId(byMaxId.getProductId())
//                .deviceId(dto.getDeviceId())
//                .build();
//
//        MQTT.publish(topic, JSON.toJSONString(mqttOtaDto));

    }

    DefaultEventExecutorGroup executorGroup = new DefaultEventExecutorGroup(Math.max(1, NettyRuntime.availableProcessors() * 2));


    @PostMapping("/onMessage")
    public void onMessage(@RequestBody HandleMessage message) {
        log.info("MqttMessageHandler.onMessage====={}", JSON.toJSONString(message));
        try {
            String topic = message.getTopic();
            //emqx系统topic   $SYS/brokers/emqx@127.0.0.1/clients/1559715685333352449/disconnected
            if (topic.contains("$SYS")) {
                int index = topic.lastIndexOf("/");
                String action = topic.substring(index + 1);
                topic = "$SYS/brokers/" + action;
                message.setTopic(topic);
                message.setTopicDeviceId(message.getBody().getString("clientid"));
            } else if (topic.contains("sys")) {
                int index = topic.indexOf("/", 4);
                String productId = topic.substring(4, index);
                topic = topic.substring(index + 1);
                index = topic.indexOf("/");
                String deviceId = topic.substring(0, index);
                topic = topic.substring(index + 1);
                message.setTopic(topic);
                message.setTopicProductId(productId);
                message.setTopicDeviceId(deviceId);
            }
            SubTopicEnum subTopicEnum = SubTopicEnum.parse(topic);
            Map<String, ITopicHandler> handlers = SpringUtil.getBeansOfType(ITopicHandler.class);
            for (Map.Entry<String, ITopicHandler> entry : handlers.entrySet()) {
                final ITopicHandler handler = entry.getValue();
                if (handler.isSupport(subTopicEnum)) {
                    handler.entrance(message);
                }
            }
        } catch (Exception e) {
            log.error("MqttMessageHandler.onMessage", e);
        }
    }


}
