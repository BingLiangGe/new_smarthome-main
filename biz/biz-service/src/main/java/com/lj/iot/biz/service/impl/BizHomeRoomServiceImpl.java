package com.lj.iot.biz.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeRoomEditDto;
import com.lj.iot.biz.base.dto.HomeRoomPageDto;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizHomeRoomService;
import com.lj.iot.biz.service.BizUploadEntityService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.aiui.DeviceNotificationService;
import com.lj.iot.biz.service.enums.OfflineTypeEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.aiui.core.dto.DeviceNotificationDto;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 房屋-房间
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@Service
public class BizHomeRoomServiceImpl implements BizHomeRoomService {

    @Autowired
    private IHomeRoomService homeRoomService;

    @Autowired
    private IHomeService homeService;
    @Autowired
    private IHotelService hotelService;
    @Autowired
    private IHotelFloorHomeService hotelFloorHomeService;

    public String url = "/device/push/room";

    @Autowired
    private IApiConfigService apiConfigService;

    @Resource
    BizUserDeviceService bizUserDeviceService;

    @Resource
    MqttPushService mqttPushService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private BizUploadEntityService bizUploadEntityService;
    @Autowired
    private IHotelUserService hotelUserService;

    @Override
    public IPage<HomeRoom> customPage(HomeRoomPageDto pageDto) {
        return homeRoomService.customPage(pageDto);
    }

    @Override
    public HomeRoom edit(HomeRoomEditDto dto, String userId) {

        HomeRoom homeRoom = homeRoomService.getOne(new QueryWrapper<>(HomeRoom.builder()
                .id(dto.getRoomId())
                .userId(userId)
                .build()));
        ValidUtils.isNullThrow(homeRoom, "数据不存在");

        homeRoomService.updateById(HomeRoom.builder()
                .id(homeRoom.getId())
                .roomName(dto.getRoomName())
                .build());

        //动态实体上传
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.RoomName);

        //推送消息到主控设备
        pushOfficeHomeRoomData(userId, homeRoom, OfflineTypeEnum.OFFLINE_EDIT.getCode());

        // 3326进行分发-bind
        DeviceNotificationDto notificationDto = DeviceNotificationDto.builder().intentName("room")
                .masterDeviceId(null)
                .homeId(homeRoom.getHomeId()).build();

        DeviceNotificationService deviceNotificationService = SpringUtil.getBean("deviceNotifi_" + notificationDto.getIntentName(), DeviceNotificationService.class);
        deviceNotificationService.handle(notificationDto);

        return homeRoomService.getById(homeRoom.getId());
    }

    @Override
    public HomeRoom add(Long homeId, String roomName, String userId) {

        Home home = homeService.getOne(new QueryWrapper<>(Home.builder()
                .id(homeId)
                .userId(userId)
                .build()));
        ValidUtils.isNullThrow(home, "数据不存在");

        HomeRoom homeRoom = HomeRoom.builder()
                .homeId(home.getId())
                .roomName(roomName)
                .userId(userId)
                .build();
        homeRoomService.save(homeRoom);


        //动态实体上传
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.RoomName);
        //推送消息到主控设备
        pushOfficeHomeRoomData(userId, homeRoom, OfflineTypeEnum.OFFLINE_ADD.getCode());
        HomeRoom byId = homeRoomService.getById(homeRoom.getId());
        byId.setRoomId(homeRoom.getId());

        // 3326进行分发-room
        DeviceNotificationDto notificationDto = DeviceNotificationDto.builder().intentName("room")
                .masterDeviceId(null)
                .homeId(homeId).build();

        DeviceNotificationService deviceNotificationService = SpringUtil.getBean("deviceNotifi_" + notificationDto.getIntentName(), DeviceNotificationService.class);
        deviceNotificationService.handle(notificationDto);


        List<HotelUser> list = hotelUserService.list(new QueryWrapper<>(HotelUser.builder().hotelUserId(userId).build()));
        if (list.size() > 0) {
            HotelFloorHome one = hotelFloorHomeService.getOne(new QueryWrapper<>(HotelFloorHome.builder().homeId(home.getId()).hotelUserId(userId).build()));
            JSONObject map = new JSONObject();
            map.put("room_id", home.getId() + "");
            map.put("room_name", roomName);
            map.put("store_id", one.getHotelId() + "");
            try {
                apiConfigService.sendApiConfigData(map, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return byId;
    }

    private void pushOfficeHomeRoomData(String userId, HomeRoom homeRoom, String type) {
        List<UserDevice> maUserDeviceList = bizUserDeviceService.getMasterUserDeviceByHomeId(homeRoom.getHomeId(), userId);
        maUserDeviceList.stream().forEach(it -> {
            mqttPushService.pushOfficeHomeRoomData(homeRoom, type, it);
        });
    }

    @Override
    public void delete(Long id, String userId) {

        HomeRoom homeRoom = homeRoomService.getOne(new QueryWrapper<>(HomeRoom.builder()
                .id(id)
                .userId(userId)
                .build()));
        ValidUtils.isNullThrow(homeRoom, "数据不存在");

        //房间有设备不允许删除
        long count = userDeviceService.count(new QueryWrapper<>(UserDevice.builder()
                .roomId(id)
                .homeId(homeRoom.getHomeId())
                .build()));
        ValidUtils.isFalseThrow(count == 0, "房间有设备");


        Long homeId = homeRoom.getHomeId();
        List<HomeRoom> list1 = homeRoomService.list(new QueryWrapper<>(HomeRoom.builder().homeId(homeId).build()));
        //家庭数据少于1条不能删
        if (list1.size() <= 1) {
            throw CommonException.INSTANCE(CommonCodeEnum.FAILURE.getCode(), "房间需要保留一个以上");
        }

        homeRoomService.removeById(homeRoom.getId());

        //动态实体上传
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.RoomName);

        //推送消息到主控设备
        pushOfficeHomeRoomData(userId, homeRoom, OfflineTypeEnum.OFFLINE_DELETE.getCode());


        // 3326进行分发-bind
        DeviceNotificationDto notificationDto = DeviceNotificationDto.builder().intentName("room")
                .masterDeviceId(null)
                .homeId(homeId).build();

        DeviceNotificationService deviceNotificationService = SpringUtil.getBean("deviceNotifi_" + notificationDto.getIntentName(), DeviceNotificationService.class);
        deviceNotificationService.handle(notificationDto);

    }

    @Override
    public List<HomeRoom> OfflineList(String deviceId, Long roomId) {
        UserDevice masterDevice = userDeviceService.findDeviceByDeviceIdAndRoomId(deviceId);
        ValidUtils.isNullThrow(masterDevice, "设备数据不存在");
        return homeRoomService.OfflineList(masterDevice.getHomeId(), masterDevice.getRoomId());
    }
}
