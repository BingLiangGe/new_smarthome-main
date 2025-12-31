package com.lj.iot.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizNoticeService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.commom.vms.core.VMS;
import com.lj.iot.commom.vms.dto.VmsDto;
import com.lj.iot.common.base.dto.FutureDto;
import com.lj.iot.common.jpush.core.JPUSH;
import com.lj.iot.common.jpush.dto.Alert;
import com.lj.iot.common.jpush.dto.JPushDto;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
@Slf4j
@Service
public class KafkaEventSosTopicHandler extends AbstractTopicHandler {

    @Resource
    private IUserDeviceService userDeviceService;

    @Resource
    private ICommunicateLogHisService communicateLogHisService;

    @Resource
    private IHomeService homeService;

    @Resource
    private IHomeRoomService homeRoomService;

    @Resource
    private ISosContactService sosContactService;

    @Resource
    private IUserAccountService userAccountService;

    @Resource
    private BizNoticeService bizNoticeService;

    /**
     * 求救处理
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {
        String deviceId = message.getTopicDeviceId();
        UserDevice userDevice = userDeviceService.getById(deviceId);

        Home home = homeService.getById(userDevice.getHomeId());
        if (home == null) {
            log.error("SosTopicHandler.handle:设备userDevice:{}没有对应的家", JSON.toJSONString(userDevice));
            return;
        }
        HomeRoom homeRoom = homeRoomService.getById(userDevice.getRoomId());
        if (homeRoom == null) {
            log.error("SosTopicHandler.handle:设备userDevice:{}没有对应的房间数据", JSON.toJSONString(userDevice));
            return;
        }

        UserAccount userAccount = userAccountService.getByIdCache(userDevice.getUserId());
        if (userAccount == null) {
            log.error("SosTopicHandler.handle:设备userDevice:{}没有对应的用户", JSON.toJSONString(userDevice));
            return;
        }

        //保存消息记录
        try {
            bizNoticeService.sos(userDevice);
        } catch (Exception e) {
            log.error("SosTopicHandler.handle:保存呼救数据异常{}", JSON.toJSONString(userDevice));
        }

        //获取紧急联系人
        List<SosContact> sosContactList = sosContactService.list(new QueryWrapper<>(SosContact.builder()
                .homeId(userDevice.getHomeId()).build()));

        //电话呼救
        List<String> phoneNumberList = new ArrayList<>();
        for (SosContact sosContact : sosContactList) {
            phoneNumberList.add(sosContact.getPhoneNumber());
            VMS.async(VmsDto.builder()
                    .deviceName(userDevice.getCustomName())
                    .homeName(home.getHomeName())
                    .mobile(sosContact.getPhoneNumber())
                    .roomName(homeRoom.getRoomName())
                    .userName(userAccount.getNickname()).build(), future -> {
                FutureDto futureDto = (FutureDto) future.getNow();
                if (futureDto.isSuccess()) {
                    communicateLogHisService.save(CommunicateLogHis.builder()
                            .createDate(LocalDateTime.now())
                            .deviceName(userDevice.getDeviceName())
                            .homeId(home.getId())
                            .flag("1")
                            .userId(userDevice.getUserId())
                            .homeName(home.getHomeName())
                            .msg(futureDto.getMessage())
                            .code(futureDto.getCode())
                            .roomName(homeRoom.getRoomName())
                            .contactsName(userAccount.getNickname()).build());
                } else {
                    communicateLogHisService.save(CommunicateLogHis.builder()
                            .createDate(LocalDateTime.now())
                            .deviceName(userDevice.getDeviceName())
                            .homeId(home.getId())
                            .flag("0")
                            .userId(userDevice.getUserId())
                            .homeName(home.getHomeName())
                            .msg(futureDto.getMessage())
                            .code(futureDto.getCode())
                            .roomName(homeRoom.getRoomName())
                            .contactsName(userAccount.getNickname()).build());
                }
            });
        }

        //极光推送
        final String text = home.getHomeName() + "用户通过设备\"" + userDevice.getCustomName() + "\"，向您发出求助信号";
        JPUSH.async(JPushDto.builder()
                .alias(phoneNumberList)
                .alert(Alert.builder()
                        .title("求助")
                        .body(text)
                        .msgType("sos").build()).build(), future -> {
        });
    }

    public KafkaEventSosTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_SOS);
    }

}
