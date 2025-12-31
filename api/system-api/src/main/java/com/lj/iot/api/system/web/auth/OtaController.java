package com.lj.iot.api.system.web.auth;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.DeviceOtaDto;
import com.lj.iot.biz.base.dto.MqttOtaDto;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.util.ValidUtils;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * OTA
 *
 * @author hao
 * @Date 2023/2/17
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/ota")
public class OtaController {

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IOperationLogService operationLogService;

    @Autowired
    private IUserAccountService userAccountService;

    @Resource
    ISystemMessagesService systemMessagesService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IUpgradeRecordService upgradeRecordService;

    DefaultEventExecutorGroup executorGroup = new DefaultEventExecutorGroup(Math.max(1, NettyRuntime.availableProcessors() * 2));

    /**
     * OTA升级
     *
     * @param dto
     * @return
     */
    @PostMapping("/updata")
    public CommonResultVo<String> updata(@Valid DeviceOtaDto dto) {


        //不是全选
        if (!dto.isSelect()) {
            ArrayList<String> list = dto.getDeviceId();
            for (int i = 0; i < list.size(); i++) {

                log.info("更新指定设备版本号------------" + list.get(i));
                int finalI = i;
                executorGroup.next().execute(new Runnable() {
                    @Override
                    public void run() {
                        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, dto.getProductId(), list.get(finalI));
                        //升级的时候，选中的版本号标记到硬件版本号上
                        UserDevice one = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(list.get(finalI)).build()));
                        ValidUtils.isNullThrow(one, "用户设备数据不存在");
                        one.setSoftWareVersion(dto.getSoftWareVersion());
                        one.setHardWareVersion(dto.getHardWareVersion());
                        Device byId = deviceService.getById(list.get(finalI));

                        if (byId == null){
                            return;
                        }

                        byId.setVersion(dto.getSoftWareVersion());
                        deviceService.updateById(byId);
                        userDeviceService.updateById(one);
                        MqttOtaDto mqttOtaDto = MqttOtaDto.builder()
                                .filepath(dto.getFilePath())
                                .softwareversion(dto.getSoftWareVersion())
                                .hardwareversion(dto.getHardWareVersion())
                                .productId(dto.getProductId())
                                .deviceId(list.get(finalI))
                                .build();

                        MQTT.publish(topic, JSON.toJSONString(mqttOtaDto));
                        log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(mqttOtaDto));
                        /*operationLogService.save(OperationLog.builder()
                                .action(new Byte("0"))
                                .deviceId(list.get(finalI))
                                .productId(dto.getProductId())
                                .params(JSON.toJSONString(mqttOtaDto))
                                .remark(topic)
                                .build());*/
                        upgradeRecordService.save(UpgradeRecord.builder().
                                deviceId(byId.getId())
                                .createTime(LocalDateTime.now())
                                .filePath(dto.getFilePath())
                                .softWareVersion(dto.getSoftWareVersion())
                                .hardWareVersion(dto.getHardWareVersion())
                                .isSuccess(0).build());
                    }
                });
            }
        } else {
            log.info("操作全选的用户和时间：", UserDto.getUser().getUId(), new Date());
            //查询所有


            List<String> list = deviceService.findNotBindDevice();

            for (int i = 0; i < list.size(); i++) {

                String deviceId=list.get(i);
                Device byId = deviceService.getById(deviceId);
                byId.setVersion(dto.getSoftWareVersion());
                deviceService.updateById(byId);
                String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, dto.getProductId(), deviceId);
                MqttOtaDto mqttOtaDto = MqttOtaDto.builder()
                        .filepath(dto.getFilePath())
                        .softwareversion(dto.getSoftWareVersion())
                        .hardwareversion(dto.getHardWareVersion())
                        .productId(dto.getProductId())
                        .deviceId(deviceId)
                        .build();
                MQTT.publish(topic, JSON.toJSONString(mqttOtaDto));
                log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(mqttOtaDto));
               /* operationLogService.save(OperationLog.builder()
                        .action(new Byte("0"))
                        .deviceId(deviceId)
                        .productId(dto.getProductId())
                        .params(JSON.toJSONString(mqttOtaDto))
                        .remark(topic)
                        .build());*/
                upgradeRecordService.save(UpgradeRecord.builder().
                        deviceId(byId.getId())
                        .createTime(LocalDateTime.now())
                        .filePath(dto.getFilePath())
                        .softWareVersion(dto.getSoftWareVersion())
                        .hardWareVersion(dto.getHardWareVersion())
                        .isSuccess(0).build());
            }
        }
        return CommonResultVo.SUCCESS();
    }


    /**
     * 新增所有消息人
     *
     * @return
     */
    @GetMapping("/addMessages")
    public CommonResultVo<String> addMessages(@Valid Integer type) {
        List<UserAccount> list = userAccountService.list();
        String messages;
        String inType;
        switch (type) {
            case 1:
                messages = "网关升级";
                inType = "网关升级";
                break;
            case 2:
                messages = "网关子设备升级";
                inType = "网关子设备升级";
                break;
            case 3:
                messages = "sos";
                inType = "紧急救助";
                break;
            default:
                messages = "其他消息";
                inType = "其他";
                break;
        }
        ArrayList<SystemMessages> list1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            list1.add(SystemMessages.builder().userId(list.get(i).getId()).readType(0).homeId(0).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).messages(messages).type(type).inType(inType).build());
        }
        systemMessagesService.saveBatch(list1);
        return CommonResultVo.SUCCESS();
    }


}
