package com.lj.iot.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.SubTempAccountAddDto;
import com.lj.iot.biz.base.dto.SubTempAccountEditDto;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizUserAccountService;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.RandomGeneratorUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BizUserAccountServiceImpl implements BizUserAccountService {

    @Autowired
    private IHomeService homeService;

    @Autowired
    private IHomeUserService homeUserService;

    @Autowired
    private IHomeUserJoinService homeUserJoinService;

    @Autowired
    private IHomeRoomService homeRoomService;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;

    @Resource
    MqttPushService mqttPushService;


    @Override
    public CommonResultVo<String> sendTokenComment() {

        /*List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .productType("gatway_872")
                .productId("213350486")
                .hardWareVersion("1.3")
                .status(true).build()));

        list.forEach(masterDevice -> {

            log.info("下发send,size={}", list.size());
            UserAccount user = userAccountService.getById(masterDevice.getUserId());

            if (user != null) {
                //把token mqtt推送到硬件设备
                mqttPushService.pushLoginToken(masterDevice, LoginVo.<UserAccount>builder()
                        .account(user.getMobile())
                        .userInfo(user)
                        .token("")
                        .params(masterDevice.getHomeId())
                        .build());
            } else {
                HotelUserAccount hotelUserAccount = hotelUserAccountService.getById(masterDevice.getUserId());

                if (hotelUserAccount != null) {
                    //把token mqtt推送到硬件设备
                    mqttPushService.pushLoginToken(masterDevice, LoginVo.<HotelUserAccount>builder()
                            .account(hotelUserAccount.getMobile())
                            .userInfo(hotelUserAccount)
                            .token("")
                            .params(masterDevice.getHomeId())
                            .build());
                }
            }
        });*/
        return CommonResultVo.SUCCESS();
    }

    @DSTransactional
    @Override
    public void cancellation(String userId) {

        long count = userDeviceService.count(new QueryWrapper<>(UserDevice.builder()
                .userId(userId)
                .build()));
        ValidUtils.isFalseThrow(count == 0, "注销前需要删除所有设备");

        count = sceneService.count(new QueryWrapper<>(Scene.builder()
                .userId(userId)
                .build()));
        //测试要求，没删除也可以注销
        //ValidUtils.isFalseThrow(count == 0, "注销前需要删除所有场景");

        //home home_room
        homeRoomService.remove(new QueryWrapper<>(HomeRoom.builder()
                .userId(userId)
                .build()));

        homeService.remove(new QueryWrapper<>(Home.builder()
                .userId(userId)
                .build()));

        List<HomeUser> homeUserList = homeUserService.list(new QueryWrapper<>(HomeUser.builder()
                .memberUserId(userId)
                .build()));
        for (HomeUser homeUser : homeUserList) {
            homeUserService.deleteAndCache(homeUser);
        }

        homeUserJoinService.remove(new QueryWrapper<>(HomeUserJoin.builder()
                .memberUserId(userId)
                .build()));

        homeUserJoinService.remove(new QueryWrapper<>(HomeUserJoin.builder()
                .userId(userId)
                .build()));

        userAccountService.deleteByIdAndCache(userId);

        List<UserAccount> userAccountList = userAccountService.list(new QueryWrapper<>(UserAccount.builder().actualUserId(userId).build()));
        for (UserAccount userAccount : userAccountList) {
            cancellation(userAccount.getId());
        }

    }

    @Override
    public UserAccount addDeviceUserAccount(UserDevice userDevice) {

        UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .mobile(userDevice.getDeviceId())
                .build()));

        if (user != null) {
            return user;
        }

        //生成 user_account
        user = UserAccount.builder()
                .id(IdUtils.nextId())
                .actualUserId(userDevice.getUserId())
                .mobile(userDevice.getDeviceId())
                .nickname(userDevice.getDeviceId())
                .type(AccountTypeEnum.SUB_EDIT.getCode())
                .build();
        userAccountService.save(user);

        //插入家和用户关系表数据
        HomeUser homeUser = HomeUser.builder()
                .homeId(userDevice.getHomeId())
                .userId(userDevice.getUserId())
                .memberUserId(user.getId())
                .memberMobile(user.getMobile())
                .isDefaultHome(true)
                .isMain(false)
                .type(AccountTypeEnum.SUB_EDIT.getCode())
                .build();
        homeUserService.addAndCache(homeUser);

        return user;
    }

    @Override
    public UserAccount addTempUserAccount(SubTempAccountAddDto dto, String userId) {
        ValidUtils.isFalseThrow(LocalDateTime.now().isBefore(dto.getExpires())
                , "过期时间要在当前时间之后");
        Home home = homeService.getOne(new QueryWrapper<>(Home.builder()
                .id(dto.getHomeId())
                .userId(userId)
                .build()));
        ValidUtils.isNullThrow(home, "家庭不存在");

        UserAccount user = UserAccount.builder()
                .id(IdUtils.nextId())
                .actualUserId(userId)
                .mobile(IdUtils.nextTimestamp() + "")
                .nickname(home.getHomeName() + "_" + RandomGeneratorUtils.getCode(3))
                .type(AccountTypeEnum.HOTEL_SUB_TEMP.getCode())
                .expires(dto.getExpires())
                .build();

        userAccountService.save(user);

        //插入家和用户关系表数据
        HomeUser homeUser = HomeUser.builder()
                .homeId(home.getId())
                .userId(home.getUserId())
                .memberUserId(user.getId())
                .memberMobile(user.getMobile())
                .isDefaultHome(true)
                .isMain(false)
                .type(AccountTypeEnum.HOTEL_SUB_TEMP.getCode())
                .build();
        homeUserService.addAndCache(homeUser);

        return user;
    }

    @Override
    public UserAccount freshTokenTempUserAccount(SubTempAccountEditDto dto, String actualUserId) {

        ValidUtils.isFalseThrow(LocalDateTime.now().isBefore(dto.getExpires())
                , "过期时间要在当前时间之后");

        UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .id(dto.getId())
                .actualUserId(actualUserId)
                .type(AccountTypeEnum.HOTEL_SUB_TEMP.getCode())
                .build()));
        ValidUtils.isNullThrow(user, "数据不存在");

        userAccountService.editByIdAndCache(UserAccount.builder().id(user.getId()).expires(dto.getExpires()).build());

        return userAccountService.getById(user.getId());
    }

    @Override
    public UserAccount editDeviceUserAccount(UserDevice userDevice, Boolean flag) {
        UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .mobile(userDevice.getDeviceId())
                .actualUserId(userDevice.getUserId())
                .build()), true);
        ValidUtils.isNullThrow(user, "设备不存在");

        userAccountService.editByIdAndCache(UserAccount.builder()
                .id(user.getId())
                .type(flag ? AccountTypeEnum.SUB_EDIT.getCode() : AccountTypeEnum.SUB_UN_EDIT.getCode())
                .build());


        //更新家和用户关系表数据
        HomeUser homeUser = homeUserService.getOneCache(userDevice.getHomeId(), user.getId());
        homeUserService.editAndCache(homeUser, flag ? AccountTypeEnum.SUB_EDIT.getCode() : AccountTypeEnum.SUB_UN_EDIT.getCode());

        return userAccountService.getById(user.getId());
    }
}
