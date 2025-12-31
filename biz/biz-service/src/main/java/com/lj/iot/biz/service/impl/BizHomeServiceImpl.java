package com.lj.iot.biz.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeAddDto;
import com.lj.iot.biz.base.dto.HomeEditDto;
import com.lj.iot.biz.base.dto.HomeRoomPageDto;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.base.enums.DefaultRoomEnum;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.base.vo.HomePageVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizHomeService;
import com.lj.iot.biz.service.BizSceneService;
import com.lj.iot.biz.service.BizUploadEntityService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.enums.OfflineTypeEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ^_^
 */
@Slf4j
@Service
public class BizHomeServiceImpl implements BizHomeService {

    @Resource
    IHomeService homeService;

    @Resource
    ISosContactService sosContactService;

    @Autowired
    private ISpeechRecordService speechRecordService;

    @Autowired
    private ICommunicateLogHisService communicateLogHisService;

    @Autowired
    private IHomeUserJoinService homeUserJoinService;

    @Autowired
    private IHotelFloorHomeService hotelFloorHomeService;
    @Resource
    IHomeUserService homeUserService;

    @Resource
    IUserAccountService userAccountService;

    @Resource
    IHomeRoomService homeRoomService;

    @Resource
    IUserDeviceService userDeviceService;

    @Resource
    BizSceneService bizSceneService;

    @Resource
    BizUserDeviceService bizUserDeviceService;

    @Resource
    MqttPushService mqttPushService;

    @Resource
    BizUploadEntityService bizUploadEntityService;

    @Autowired
    ISceneTemplateService sceneTemplateService;
    @Autowired
    ISceneService sceneService;
    @Resource
    BizHomeService bizHomeService;

    @DSTransactional
    @Override
    public Home add(HomeAddDto dto, String userId) {
        //保存用户和家的关系表
        UserAccount userAccount = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .id(userId)
                .build()));

        //创建家
        Home home = Home.builder()
                .userId(userId)
                .homeName(dto.getHomeName())
                .address(dto.getAddress())
                .country(dto.getCountry())
                .province(dto.getProvince())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .lat(dto.getLat())
                .lng(dto.getLng())
                .build();

        homeService.save(home);

        //默认绑定4个场景
        List<SceneTemplate> list = sceneTemplateService.list();
        for (int i = 0; i < list.size(); i++) {
            //保存场景数据
            Scene scene = Scene.builder()
                    .userId(userAccount.getId())
                    .homeId(home.getId())
                    .sceneIcon(list.get(i).getBackgroundUrl())
                    .sceneName(list.get(i).getName())
                    .isDefault(1)
                    .command(list.get(i).getName())
                    .createTime(LocalDateTime.now().withSecond(i))
                    .build();
            sceneService.save(scene);
        }

        homeUserService.addAndCache(HomeUser.builder()
                .homeId(home.getId())
                .userId(userAccount.getId())
                .memberUserId(userAccount.getId())
                .memberMobile(userAccount.getMobile())
                .isDefaultHome(home.getIsDefaultHome())
                .isMain(true)
                .type(AccountTypeEnum.MASTER.getCode())
                .build());
        //保存家默认的四个房间
        final List<HomeRoom> homeRooms = buildDefaultRoom(home.getId(), userAccount.getId());
        homeRoomService.saveBatch(homeRooms);
        //动态实体上传
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.RoomName);
        //推送消息到主控设备
        List<UserDevice> maUserDeviceList=bizUserDeviceService.getMasterUserDeviceByHomeId(home.getId(),userId);
        maUserDeviceList.stream().forEach(it->{
            mqttPushService.pushOfficeHomeData(home, OfflineTypeEnum.OFFLINE_ADD.getCode(),it);
        });
        return home;
    }

    @DSTransactional
    @Override
    public Home hotelHomeAdd(HomeAddDto dto, String userId) {
        //保存用户和家的关系表
        UserAccount userAccount = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
            .id(userId)
            .build()));

        //创建家
        Home home = Home.builder()
                .userId(userId)
                .homeName(dto.getHomeName())
                .address(dto.getAddress())
                .country(dto.getCountry())
                .province(dto.getProvince())
                .city(dto.getCity())
                .hotelId(dto.getHotelId())
                .district(dto.getDistrict())
                .lat(dto.getLat())
                .lng(dto.getLng())
                .build();

        homeService.save(home);

        homeUserService.addAndCache(HomeUser.builder()
                .homeId(home.getId())
                .userId(userAccount.getId())
                .memberUserId(userAccount.getId())
                .memberMobile(userAccount.getMobile())
                .isDefaultHome(home.getIsDefaultHome())
                .isMain(true)
                .type(AccountTypeEnum.MASTER.getCode())
                .build());

        //保存默认卧室
        homeRoomService.save(HomeRoom.builder()
                .userId(userAccount.getId())
                .homeId(home.getId())
                .roomName("卧室")
                .build());

        //动态实体上传
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.RoomName);

        return home;
    }


    @Override
    public Home edit(HomeEditDto homeDto, String userId) {

        Home home = homeService.getOne(new QueryWrapper<>(Home.builder()
                .userId(userId)
                .id(homeDto.getHomeId())
                .build()));
        ValidUtils.isNullThrow(home, "家庭不存在");
        //保存家庭数据
        homeService.updateById(Home.builder()
                .id(home.getId())
                .homeName(homeDto.getHomeName())
                .address(homeDto.getAddress())
                .country(homeDto.getCountry())
                .province(homeDto.getProvince())
                .city(homeDto.getCity())
                .district(homeDto.getDistrict())
                .lat(homeDto.getLat())
                .lng(homeDto.getLng())
                .remarks(homeDto.getRemarks())
                .build());
        //推送消息到主控设备
        List<UserDevice> maUserDeviceList=bizUserDeviceService.getMasterUserDeviceByHomeId(home.getId(),userId);
        maUserDeviceList.stream().forEach(it->{
            mqttPushService.pushOfficeHomeData(home,OfflineTypeEnum.OFFLINE_EDIT.getCode(),it);
        });
        return homeService.getById(home.getId());
    }

    /**
     * 新建默认四个房间
     *
     * @param homeId 家ID
     * @param userId 用户ID
     * @return 房间集合
     */
    @Override
    public List<HomeRoom> buildDefaultRoom(Long homeId, String userId) {
        return DefaultRoomEnum.allNames().stream().map(
                it -> HomeRoom.builder()
                        .homeId(homeId)
                        .roomName(it)
                        .userId(userId).build()).collect(Collectors.toList());
    }

    @DSTransactional
    @Override
    public void deleteHomeById(Long homeId, String userId) {
        //判断用户是否为管理员角色
        Home home = homeService.findHomeByHomeIdAndUserId(homeId, userId);
        ValidUtils.isNullThrow(home, "只有创建者才允许修改");
        ValidUtils.isTrueThrow(home.getIsDefaultHome(), "默认家不允许删除");
        //家下有设备需要删除设备后再进行
        ValidUtils.isFalseThrow(userDeviceService.count(new QueryWrapper<>(UserDevice.builder()
                .userId(userId)
                .homeId(homeId)
                .build()))==0, "家下存在设备未删除");


        List<Home> list = homeService.list(new QueryWrapper<>(Home.builder().userId(userId).build()).ne("id",homeId));
        if (list.size()==0){
            //创建一个家庭，避免删了就没了
            HomeAddDto dto = new HomeAddDto();
            dto.setHomeName("我的家");
            bizHomeService.add(dto, userId);
        }


        //删除家庭
        homeService.removeById(homeId);

        //删除场景
        bizSceneService.deleteSceneByHomeId(homeId, userId);

        //删除家房间数据
        homeRoomService.remove(new QueryWrapper<>(HomeRoom.builder()
                .homeId(homeId).build()));

        //删除联系人 sos_contact
        sosContactService.remove(new QueryWrapper<>(SosContact.builder().homeId(homeId).build()));

        //通话记录communicate_log_his
        communicateLogHisService.remove(new QueryWrapper<>(CommunicateLogHis.builder().homeId(homeId).build()));

        //删除识别记录speech_record
        speechRecordService.remove(new QueryWrapper<>(SpeechRecord.builder().homeId(homeId).build()));

        //删除用户和家的关系表
        List<HomeUser> homeUserList = homeUserService.list(new QueryWrapper<>(HomeUser.builder()
                .homeId(homeId).build()));
        for (HomeUser homeUser : homeUserList) {
            //如果有的用户已经设置了默认家，那就重新分配一个
            if(homeUser.getIsDefaultHome()){
                List<HomeUser> homeList = homeUserService.list(new QueryWrapper<>(HomeUser.builder()
                        .userId(homeUser.getMemberUserId()).isMain(true).build()).ne("home_id", homeId));
                if(homeList.size()>0){
                    HomeUser homeUser1 = homeList.get(0);
                    homeUser1.setIsDefaultHome(true);
                    homeUserService.updateById(homeUser1);
                    Home byId = homeService.getById(homeUser1.getHomeId());
                    byId.setIsDefaultHome(true);
                    homeService.updateById(byId);
                }else{
                    //没有家的情况
                    //创建一个家庭，避免删了就没了
                    HomeAddDto dto = new HomeAddDto();
                    dto.setHomeName("我的家");
                    bizHomeService.add(dto, homeUser.getMemberUserId());
                }
            }
            homeUserService.deleteAndCache(homeUser);
        }

        //删除申请/邀请加入家庭记录
        homeUserJoinService.remove(new QueryWrapper<>(HomeUserJoin.builder().homeId(homeId).build()));

        //删除家庭和楼层关系表
        hotelFloorHomeService.remove(new QueryWrapper<>(HotelFloorHome.builder().homeId(homeId).build()));

        //上传动态实体,aiui上实体词条可以多，不可以少
        //bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.RoomName);
        //bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.SceneCorpus);
        //推送消息到主控设备
        List<UserDevice> maUserDeviceList=bizUserDeviceService.getMasterUserDeviceByHomeId(homeId,userId);
        maUserDeviceList.stream().forEach(it->{
            mqttPushService.pushOfficeHomeData(home,OfflineTypeEnum.OFFLINE_DELETE.getCode(), it);
        });
    }

    /**
     * 管理系统-分页查询
     *
     * @param pageDto
     * @return
     */
    @Override
    public IPage<HomePageVo> customPage(HomeRoomPageDto pageDto) {
        return homeService.customPage(pageDto);
    }

}
