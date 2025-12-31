package com.lj.iot.api.app.aop;

import com.alibaba.fastjson.JSON;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.db.smart.entity.HomeRoom;
import com.lj.iot.biz.db.smart.entity.Scene;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizHomeUserService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.ValidUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Aspect
@Component
public class HomeAuthAop {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private IHomeUserService homeUserService;


    @Autowired
    private BizHomeUserService bizHomeUserService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IHomeRoomService homeRoomService;

    @Around(value = "@annotation(homeAuth))")
    public Object customPermissions(ProceedingJoinPoint joinPoint, HomeAuth homeAuth) throws Throwable {
        UserDto user = LoginUtils.getUser();
        ValidUtils.isNullThrow(user, CommonCodeEnum.LOGIN_INFO_NOT_EXIST.getCode(), "登录已过期，请重新登录");

        Object[] args = joinPoint.getArgs();

        //属性名
        String identify = homeAuth.value();

        if (StringUtils.hasText(identify)) {
            hasIdentify(identify, args, homeAuth);
        } else {
            noIdentify(homeAuth);
        }
        return joinPoint.proceed();
    }

    private void noIdentify(HomeAuth homeAuth) {
        UserAccount userAccount = userAccountService.getByIdCache(UserDto.getUser().getUId());
        ValidUtils.isNullThrow(userAccount, "权限不足");
        switch (homeAuth.type()) {
            case MAIN:
                ValidUtils.isFalseThrow(
                        AccountTypeEnum.MASTER.getCode().equals(userAccount.getType())
                                || AccountTypeEnum.HOTEL.getCode().equals(userAccount.getType())
                        , "权限不足");
                break;
            case EDIT:
                ValidUtils.isFalseThrow(
                        AccountTypeEnum.MASTER.getCode().equals(userAccount.getType())
                                || AccountTypeEnum.HOTEL.getCode().equals(userAccount.getType())
                                || AccountTypeEnum.SUB_EDIT.getCode().equals(userAccount.getType())
                        , "权限不足");
                break;
            default:
        }
    }

    private void hasIdentify(String identify, Object[] args, HomeAuth homeAuth) {
        Long homeId = null;
        switch (identify) {
            case "homeId":
                for (Object arg : args) {
                    homeId = JSON.parseObject(JSON.toJSONString(arg)).getLong(identify);
                    if (homeId != null) {
                        break;
                    }
                }
                break;
            case "roomId":
                for (Object arg : args) {
                    Long roomId = JSON.parseObject(JSON.toJSONString(arg)).getLong(identify);
                    ValidUtils.isNullThrow(roomId, "房间不存在");
                    HomeRoom homeRoom = homeRoomService.getById(roomId);
                    ValidUtils.isNullThrow(homeRoom, "房间不存在");
                    homeId = homeRoom.getHomeId();
                    if (homeId != null) {
                        break;
                    }
                }
                break;
            case "masterDeviceId":
            case "deviceId":
                String deviceId = null;
                for (Object arg : args) {
                    deviceId = JSON.parseObject(JSON.toJSONString(arg)).getString(identify);
                    if (deviceId != null) {
                        break;
                    }
                }
                ValidUtils.isNullThrow(deviceId, "设备不存在");
                UserDevice userDevice = userDeviceService.getOneByIdCache(deviceId);
                ValidUtils.isNullThrow(userDevice, "设备不存在");
                homeId = userDevice.getHomeId();
                break;
            case "sceneId":
                Long sceneId = null;
                for (Object arg : args) {
                    sceneId = JSON.parseObject(JSON.toJSONString(arg)).getLong(identify);
                    if (sceneId != null) {
                        break;
                    }
                }
                ValidUtils.isNullThrow(sceneId, "场景不存在");
                Scene scene = sceneService.getOneByIdCache(sceneId);
                ValidUtils.isNullThrow(scene, "场景不存在");
                homeId = scene.getHomeId();
                break;
            case "homeUserId":
                Long homeUserId = null;
                for (Object arg : args) {
                    homeUserId = JSON.parseObject(JSON.toJSONString(arg)).getLong(identify);
                    if (homeUserId != null) {
                        break;
                    }
                }
                ValidUtils.isNullThrow(homeUserId, "权限不足");
                homeId = homeUserService.getHomeIdById(homeUserId);
        }
        ValidUtils.isNullThrow(homeId, "权限不足");

        switch (homeAuth.type()) {
            case MAIN:
                bizHomeUserService.noIsAdminThrow(homeId, UserDto.getUser().getUId());
                break;
            case EDIT:
                bizHomeUserService.noIsEditThrow(homeId, UserDto.getUser().getUId());
                break;
            default:
                bizHomeUserService.noIsAllThrow(homeId, UserDto.getUser().getUId());
        }
    }
}
