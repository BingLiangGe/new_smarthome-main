package com.lj.iot.api.system.web.auth;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.MqttParamDto;
import com.lj.iot.biz.db.smart.entity.UpgradeRecord;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUpgradeRecordService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/open/ota")
public class OtaController {


    @Autowired
    private IUpgradeRecordService upgradeRecordService;


    @Autowired
    private IUserDeviceService userDeviceService;

    /**
     * 清除队列
     * @param masterDeviceId
     * @return
     */
    @RequestMapping("/cleanUpgradation")
    public String cleanUpgradation(String masterDeviceId) {
        // 清除队列
        upgradeRecordService.remove(new QueryWrapper<>(UpgradeRecord.builder().deviceId(masterDeviceId).build()));
        return "升级队列清除成功";
    }

    /**
     * 获取设备详情
     *
     * @param masterDeviceId
     * @return
     */
    @RequestMapping("/getDeviceInfo")
    public String getDeviceInfo(String masterDeviceId) {
        ValidUtils.isNullThrow(masterDeviceId, "主控设备号必填!");

        UserDevice userDevice = userDeviceService.getById(masterDeviceId);

        ValidUtils.isNullThrow(userDevice, "主控未绑定,请核对后重试!");

        ValidUtils.isFalseThrow(userDevice.getStatus(), "主控设备已离线,请上电后重试!");

        List<UpgradeRecord> list = upgradeRecordService.list(new QueryWrapper<>(UpgradeRecord.builder()
                .deviceId(userDevice.getDeviceId()).build()));

        String respMsg = "\t当前无升级任务!";
        if (!list.isEmpty()) {
            UpgradeRecord lastRecord = list.get(list.size() - 1);
            respMsg = "\t设备拉取升级次数:" + lastRecord.getSuccessCount() + ",拉取软件版本号:" + lastRecord.getSoftWareVersion();
        }

        return "当前软件版本号:" + userDevice.getSoftWareVersion() + ",当前硬件版本号:" + userDevice.getHardWareVersion() + respMsg;
    }


    /**
     * 推送升级
     *
     * @param masterDeviceId
     * @return
     */
    @RequestMapping("/upgradation")
    public String upgradation(String masterDeviceId) {

        ValidUtils.isNullThrow(masterDeviceId, "主控设备号必填!");

        UserDevice userDevice = userDeviceService.getById(masterDeviceId);

        ValidUtils.isNullThrow(userDevice, "主控未绑定,请核对后重试!");

        ValidUtils.isFalseThrow(userDevice.getStatus(), "主控设备已离线,请上电后重试!");

        // 清除队列
        upgradeRecordService.remove(new QueryWrapper<>(UpgradeRecord.builder().deviceId(userDevice.getDeviceId()).build()));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productId", userDevice.getProductId());
        jsonObject.put("deviceId", userDevice.getDeviceId());

        // 重启设备
        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_RESTART, userDevice.getProductId(), userDevice.getPhysicalDeviceId());
        MqttParamDto paramDto = MqttParamDto.builder()
                .id(IdUtil.simpleUUID())
                .time(DateUtil.current())
                .data(jsonObject)
                .build();
        MQTT.publish(topic, JSON.toJSONString(paramDto));
        log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(paramDto));


        upgradeRecordService.save(UpgradeRecord.builder()
                .deviceId(userDevice.getDeviceId())
                .createTime(LocalDateTime.now())
                .filePath("http://lj-test2.oss-cn-beijing.aliyuncs.com/xr_system_v1.0.138_SGPY_NEW.img")
                .softWareVersion("1.0.138")
                .hardWareVersion("1.3")
                .isSuccess(0).build());

        return "推送成功";
    }
}
