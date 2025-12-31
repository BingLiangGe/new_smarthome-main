package com.lj.iot.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.base.vo.HomeDataVo;
import com.lj.iot.biz.base.vo.HomeInfoVo;
import com.lj.iot.biz.base.vo.HomeUserVo;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.*;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.aiui.core.dto.UploadEntityDto;
import com.lj.iot.common.aiui.core.dto.UploadEntityItemDto;
import com.lj.iot.common.aiui.core.service.IUploadEntityService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 房屋-成员
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@Slf4j
@Service
public class BizHomeUserServiceImpl implements BizHomeUserService {

    @Resource
    @Lazy
    BizWsPublishService bizWsPublishService;

    @Autowired
    private IHomeUserService homeUserService;

    @Resource
    IUserDeviceService userDeviceService;

    @Resource
    private IHomeService homeService;
    @Resource
    IUploadEntityService uploadEntityService;
    @Autowired
    private IUserAccountService userAccountService;
    @Resource
    private IHomeUserDeviceNoAuthService iHomeUserDeviceNoAuthService;
    @Resource
    BizHomeService bizHomeService;

    @Resource
    ISceneService sceneService;

    @Resource
    IHomeRoomService homeRoomService;
    @Resource
    private ICacheService cacheService;
    @Resource
    private MqttPushService mqttPushService;


    @DSTransactional
    @Override
    public HomeUser setDefaultHome(HomeUserIdDto dto, String userId) {


        HomeUser homeUser = homeUserService.getOne(new QueryWrapper<>(HomeUser.builder()
                .id(dto.getHomeUserId())
                .memberUserId(userId)
                .build()));
        ValidUtils.isNullThrow(homeUser, "数据不存在");

        //用户下其他家设置为非默认家 (只有自己的is_main=1才生效)
        homeUserService.update(HomeUser.builder()
                        .isDefaultHome(false)
                        .build(),
                new QueryWrapper<>(HomeUser.builder()
                        .memberUserId(userId)
                        .build()));

        //分享的家庭也要同步false (只有自己的is_main=1 和 is_mian = 0才生效)
//        homeUserService.update(HomeUser.builder()
//                .isDefaultHome(false)
//                .build(),
//                new QueryWrapper<>(HomeUser.builder()
//                        .memberUserId(userId)
//                        .build()));

        //将指定的家设置为默认家
        homeUserService.updateById(HomeUser.builder()
                .id(homeUser.getId())
                .isDefaultHome(true).build());


        //查询第二张表 home 表设置true
        List<Home> list = homeService.list(new QueryWrapper<>(Home.builder().userId(userId).build()));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().longValue() == homeUser.getHomeId().longValue()) {
                list.get(i).setIsDefaultHome(true);
            } else {
                list.get(i).setIsDefaultHome(false);
            }
        }


        //更新
        homeService.updateBatchById(list);

        return homeUserService.getById(homeUser.getId());
    }

    @Override
    public List<HomeDataVo> listHome(String userId) {
        return homeUserService.listHome(userId);
    }

    @Override
    public List<HomeUserVo> findHomeUserList(Long homeId, String userId) {
        return homeUserService.findHomeUserList(homeId, userId);
    }

    @Override
    public HomeInfoVo info(Long homeId, String userId) {
        Home home = homeService.getById(homeId);
        return HomeInfoVo.builder()
                .homeId(home.getId())
                .isMain(home.getUserId().equals(userId))
                .isDefaultHome(home.getIsDefaultHome())
                .homeName(home.getHomeName())
                .address(home.getAddress())
                .country(home.getCountry())
                .province(home.getProvince())
                .city(home.getCity())
                .district(home.getDistrict())
                .lat(home.getLat())
                .lng(home.getLng())
                .build();
    }

    @Override
    public IPage<HomeUser> customPage(HomeUserPageDto pageDto) {
        return homeUserService.customPage(pageDto);
    }

    @Override
    public List<String> getMemberUserIdsByHomeId(Long homeId) {
        List<HomeUser> list = homeUserService.list(new QueryWrapper<>(HomeUser.builder()
                .homeId(homeId)
                .build()));
        List<String> userList = new ArrayList<>();
        for (HomeUser homeUser : list) {
            userList.add(homeUser.getMemberUserId());
        }
        return userList;
    }

    @Override
    public List<String> getEditMemberUserIdsByHomeId(Long homeId) {
        return homeUserService.getEditMemberUserIdsByHomeId(homeId);
    }

    @DSTransactional
    @Override
    public HomeUser kickOut(HomeUserIdDto idDto, String userId) {
        HomeUser homeUser = homeUserService.getOne(new QueryWrapper<>(HomeUser.builder()
                .id(idDto.getHomeUserId())
                .userId(userId).build()));
        ValidUtils.isNullThrow(homeUser, "数据不存在");
        ValidUtils.isTrueThrow(homeUser.getIsMain(), "管理员不能被踢出");

        //只能踢出正常APP账户
        ValidUtils.isNullThrow(userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .id(homeUser.getMemberUserId())
                .type(AccountTypeEnum.MASTER.getCode())
                .build())), "子账号不能被踢出，你可以删除子账号");

        List<Home> list = homeService.list(new QueryWrapper<>(Home.builder().userId(homeUser.getMemberUserId()).build()).ne("id", homeUser.getHomeId()));
        if (list.size() == 0) {
            //创建一个家庭，避免删了就没了
            HomeAddDto dto = new HomeAddDto();
            dto.setHomeName("我的家");
            bizHomeService.add(dto, homeUser.getMemberUserId());
        }

        homeUserService.deleteAndCache(homeUser);
        return homeUser;
    }

    @Override
    public HomeUser signOut(HomeUserIdDto idDto, String userId) {
        //判断不能退出自己的家
        HomeUser homeUser = homeUserService.getOne(new QueryWrapper<>(HomeUser.builder()
                .id(idDto.getHomeUserId())
                .memberUserId(UserDto.getUser().getUId()).build()));
        ValidUtils.isNullThrow(homeUser, "数据不存在");
        ValidUtils.isTrueThrow(homeUser.getIsMain(), "不能退出自己创建的家");
        //ValidUtils.isTrueThrow(homeUser.getIsDefaultHome(), "成员家庭已被设置了默认家庭");
        if (homeUser.getIsDefaultHome()) {
            List<Home> list = homeService.list(new QueryWrapper<>(Home.builder().userId(UserDto.getUser().getUId()).build()).ne("id", homeUser.getHomeId()));
            if (list.size() == 0) {
                //创建一个家庭，避免删了就没了
                HomeAddDto dto = new HomeAddDto();
                dto.setHomeName("我的家");
                bizHomeService.add(dto, homeUser.getMemberUserId());
            }
            if (list.size() > 0) {
                list.get(0).setIsDefaultHome(true);
                homeService.updateById(list.get(0));
                HomeUser one = homeUserService.getOne(new QueryWrapper<>(HomeUser.builder().homeId(list.get(0).getId()).memberUserId(UserDto.getUser().getUId()).build()));
                one.setIsDefaultHome(true);
                homeUserService.updateById(one);
            }
        } else {
            List<Home> list = homeService.list(new QueryWrapper<>(Home.builder().userId(UserDto.getUser().getUId()).build()).ne("id", homeUser.getHomeId()));
            if (list.size() == 0) {
                //创建一个家庭，避免删了就没了
                HomeAddDto dto = new HomeAddDto();
                dto.setHomeName("我的家");
                bizHomeService.add(dto, homeUser.getMemberUserId());
            }
        }


        //只能正常账号，加入其他家庭的成员才可以退出
        UserAccount userAccount = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .id(homeUser.getMemberUserId()).build()));
        ValidUtils.isNullThrow(userAccount, "用户不存在");
        ValidUtils.isFalseThrow(AccountTypeEnum.MASTER.getCode().equals(userAccount.getType()), "该账号不能退出");

        homeUserService.removeById(idDto.getHomeUserId());
        return homeUser;
    }

    @Override
    public HomeUser noIsAllThrow(Long homeId, String userId) {
        HomeUser homeUser = homeUserService.getOneCache(homeId, userId);
        ValidUtils.isNullThrow(homeUser, "权限不足");
        return homeUser;
    }

    @Override
    public void noIsAdminThrow(Long homeId, String userId) {
        HomeUser homeUser = noIsAllThrow(homeId, userId);
        ValidUtils.isFalseThrow(homeUser.getIsMain(), "你不是家管理员，无权限操作");
    }


    @Override
    public void noIsEditThrow(Long homeId, String userId) {
        HomeUser homeUser = noIsAllThrow(homeId, userId);
        ValidUtils.isFalseThrow(AccountTypeEnum.MASTER.getCode().equals(homeUser.getType())
                        || AccountTypeEnum.HOTEL.getCode().equals(homeUser.getType())
                        || AccountTypeEnum.SUB_EDIT.getCode().equals(homeUser.getType())
                , "你没有编辑权限");
    }

    @Override
    public String setHomeUserDeviceAuthority(SetHomeUserDeviceAuthorityDto dto, String userId) {
        StringBuilder ids = new StringBuilder();
        List<HomeUserDeviceNoAuth> list = Lists.newArrayList();
        HomeUserDeviceNoAuth homeUserDeviceNoAuth = null;
        HomeUser homeUser = homeUserService.getOne(new QueryWrapper<>(HomeUser.builder().id(dto.getHomeUserId()).build()));
        for (SetHomeUserDeviceAuthorityDto.UserDeviceAuth userDeviceAuth :
                dto.getUserDeviceAuths()) {
            switch (userDeviceAuth.getAuthority()) {
                case 1: //授权
                    ids.append(",").append(userDeviceAuth.getDeviceOrSceneId());
                    break;
                case 2: //取消授权
                    homeUserDeviceNoAuth = new HomeUserDeviceNoAuth();
                    homeUserDeviceNoAuth.setHomeUserId(dto.getHomeUserId());
                    homeUserDeviceNoAuth.setDeviceOrSceneId(userDeviceAuth.getDeviceOrSceneId());
                    homeUserDeviceNoAuth.setHomeId(dto.getHomeId());
                    homeUserDeviceNoAuth.setUserId(homeUser.getUserId());
                    homeUserDeviceNoAuth.setAuthType(userDeviceAuth.getAuthority());
                    list.add(homeUserDeviceNoAuth);
                    break;
            }

        }
        //存入数据库
        if (!StringUtils.isEmpty(ids)) {//取消授权
            iHomeUserDeviceNoAuthService.deleteAuth(dto.getHomeUserId(), dto.getAuthType(), ids.substring(1));
        }
        if (!CollectionUtils.isEmpty(list) && list.size() > 0) {//授权
            iHomeUserDeviceNoAuthService.saveBatch(list);
        }
        return "设置成功";
    }

    @DSTransactional
    @Override
    public String setManage(HomeUserIdDto dto, String uId) {
        HomeUser byId1 = homeUserService.getById(dto.getHomeUserId());
        HomeUser one1 = homeUserService.getOne(new QueryWrapper<>(HomeUser.builder().userId(uId).memberUserId(uId).homeId(byId1.getHomeId()).isMain(true).build()));
        ValidUtils.isNullThrow(one1, "权限不足");
        one1.setUserId(byId1.getMemberUserId());
        one1.setIsMain(false);
        one1.setType("3");
        Home byId = homeService.getById(one1.getHomeId());
        byId.setUserId(byId1.getMemberUserId());
        homeService.updateById(byId);
        homeUserService.updateById(one1);

        HomeUser one2 = homeUserService.getOne(new QueryWrapper<>(HomeUser.builder().userId(uId).memberUserId(byId1.getMemberUserId()).homeId(byId1.getHomeId()).isMain(false).build()));
        ValidUtils.isNullThrow(one1, "设置管理员家庭数据不存在");
        one2.setUserId(byId1.getMemberUserId());
        one2.setIsMain(true);
        one2.setType("1");
        homeUserService.updateById(one2);

        //更新用户下的设备
        userDeviceService.update(
                UserDevice.builder()
                        .updateTime(LocalDateTime.now())
                        .userId(byId1.getMemberUserId())
                        .build(),
                new QueryWrapper<>(UserDevice.builder()
                        .homeId(byId1.getHomeId())
                        .build()));

        //更新用户场景
        sceneService.update(
                Scene.builder()
                        .updateTime(LocalDateTime.now())
                        .userId(byId1.getMemberUserId())
                        .build(),
                new QueryWrapper<>(Scene.builder()
                        .homeId(byId1.getHomeId())
                        .build()));

        //房间
        homeRoomService.update(
                HomeRoom.builder()
                        .updateTime(LocalDateTime.now())
                        .userId(byId1.getMemberUserId())
                        .build(),
                new QueryWrapper<>(HomeRoom.builder()
                        .homeId(byId1.getHomeId())
                        .build()));


        bizWsPublishService.publish(WsResultVo.SUCCESS(
                one2.getUserId(),
                one2.getHomeId(),
                RedisTopicConstant.TOPIC_CHANNEL_MANAGE_SET,
                one2));


        //更新家庭下的实体
        //先获取老管理员的实体列表
        List<UploadEntityItemDto> UploadEntityItemList = getUploadEntityOfSceneCorpus(uId);
        log.info("老管理员的实体列表-------", UploadEntityItemList);
        //再更新新管理员的列表
        uploadEntityService.uploadCustomLevel(UploadEntityDto.builder().entityList(UploadEntityItemList)
                .dynamicEntitiesName(DynamicEntitiesNameEnum.SceneCorpus.getCode())
                .userId(byId1.getMemberUserId()).build());


        //给家下面的每个主控推送更改后的userId
        List<UserDevice> list = userDeviceService.list(
                new QueryWrapper<>(
                        UserDevice.builder()
                                .userId(byId1.getMemberUserId())
                                .homeId(byId1.getHomeId())
                                .signalType("MASTER")
                                .build()));
        for (UserDevice userDevice :
                list) {
            UserAccount user = userAccountService.getById(userDevice.getUserId());

            String token = cacheService.get("app" + "session:account:token:" + user.getId());

            //把token mqtt推送到硬件设备
            mqttPushService.pushLoginToken(userDevice, LoginVo.<UserAccount>builder()
                    .account(user.getMobile())
                    .userInfo(user)
                    .token(token)
                    .params(userDevice.getHomeId())
                    .build());
        }
        return "设置成功";
    }


    /**
     * 用户级别场景语料数据
     *
     * @return
     */
    List<UploadEntityItemDto> getUploadEntityOfSceneCorpus(String userId) {
        List<Scene> sceneList = sceneService.list(new QueryWrapper<>(Scene.builder()
                .userId(userId)
                .build()));

        Set<String> stringSet = new HashSet<>();
        for (Scene scene : sceneList) {
            stringSet.addAll(List.of(scene.getCommand().split(",")));
        }
        List<UploadEntityItemDto> uploadEntityItemDtoList = new ArrayList<>();
        for (String s : stringSet) {
            uploadEntityItemDtoList.add(UploadEntityItemDto.builder()
                    .name(s)
                    .alias(s)
                    .build());
        }
        return uploadEntityItemDtoList;
    }
}
