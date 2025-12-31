package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.base.vo.*;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.mapper.UserDeviceMapper;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.common.util.ValidUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户设备表 服务实现类
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class UserDeviceServiceImpl extends ServiceImpl<UserDeviceMapper, UserDevice> implements IUserDeviceService {

    @Resource
    private ICacheService cacheService;

    @Autowired
    private IHomeRoomService homeRoomService;

    @Autowired
    private IProductService producerService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IUserDeviceModeService userDeviceModeService;

    @Resource
    private UserDeviceMapper userDeviceMapper;


    @Autowired
    private IUserAccountService userAccountService;


    @Override
    public List<SceneThingModelVo> getSceneThingModel(String deviceId) {
        return userDeviceMapper.getSceneThingModel(deviceId);
    }

    @Override
    public HotelCallVo getDeviceHotelCall(String masterDeviceId) {
        return userDeviceMapper.getDeviceHotelCall(masterDeviceId);
    }

    @Override
    public List<String> editDeviceContextStatus() {
        return userDeviceMapper.editDeviceContextStatus();
    }

    @Override
    public WatchInfoVo findWatchInfo(String deviceId) {
        return userDeviceMapper.findWatchInfo(deviceId);
    }

    @Override
    public CommonResultVo<String> sendTokenComment() {
        return CommonResultVo.SUCCESS();
    }

    @Override
    public com.lj.iot.common.util.util.PageUtil<UserDevice> findHotelDevicePage(Integer pageIndex, Integer pageSize, UserDevice userDevice) {
        com.lj.iot.common.util.util.PageUtil<UserDevice> page = new com.lj.iot.common.util.util.PageUtil<UserDevice>();

        page.setRows(userDeviceMapper.findHotelDevicePageLimit(pageIndex, pageSize, userDevice));
        page.setTotal(userDeviceMapper.findHotelDevicePageCount(userDevice));

        return page;
    }

    @Override
    public com.lj.iot.common.util.util.PageUtil<UserDevice> findUserDeviceWithOta(Integer pageIndex, Integer pageSize, UserDevice userDevice) {
        com.lj.iot.common.util.util.PageUtil<UserDevice> pageUtil = new com.lj.iot.common.util.util.PageUtil<UserDevice>();

        pageUtil.setRows(userDeviceMapper.findUserDeviceWithOtaLimit(pageIndex, pageSize, userDevice));
        pageUtil.setTotal(userDeviceMapper.findUserDeviceWithOtaLimitCount(userDevice));

        return pageUtil;
    }

    @Override
    public List<UserDevice> selectUserDeviceWith3326(Long homeId) {
        return this.list(new QueryWrapper<>(UserDevice.builder()
                .homeId(homeId)
                .productType("gatway_touch")
                .build()));
    }

    @Override
    public void updateUserDeviceStatusBatch(List<String> deviceIds) {
        UpdateWrapper updateWrapper = new UpdateWrapper<>();

        updateWrapper.in("device_id", deviceIds);
        updateWrapper.set("status", 0);
        update(updateWrapper);
    }

    @Override
    public List<String> findMasterDeviceDownStatus() {
        return userDeviceMapper.findMasterDeviceDownStatus();
    }

    @Override
    public List<String> findWatchDeviceDownStatus() {
        return userDeviceMapper.findWatchDeviceDownStatus();
    }

    @Override
    public List<String> findUserDeviceChirStatus() {
        return userDeviceMapper.findUserDeviceChirStatus();
    }

    @Override
    public IPage<UserDevice> customPage(UserDevicePageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }

    @Override
    public UserDeviceStatisticsVo statistics(UserDevicePageDto pageDto) {
        return UserDeviceStatisticsVo.builder()
                .online(this.baseMapper.online(pageDto))
                .offline(this.baseMapper.offline(pageDto))
                .build();
    }

    @Override
    public IPage<UserDeviceVo> customShowPage(HomeIdRoomIdPageDto pageDto) {
        return this.baseMapper.customShowPage(PageUtil.page(pageDto), pageDto);
    }

    @Override
    public IPage<UserDeviceVo> customShowPage3326(HomeIdRoomIdPageDto pageDto) {
        return this.baseMapper.customShowPage3326(PageUtil.page(pageDto), pageDto);
    }

    @Override
    public List<UserDevice> masterList(HomeIdDto dto) {
        List<UserDevice> list = this.list(new QueryWrapper<>(UserDevice.builder()
                .homeId(dto.getHomeId())
                .signalType(SignalEnum.MASTER.getCode())
                .build()));

        HomeRoom homeRoom;
        for (UserDevice userDevice : list) {
            homeRoom = homeRoomService.getById(userDevice.getRoomId());

            if (homeRoom == null) {
                continue;
            }

            int size = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                    .masterDeviceId(userDevice.getDeviceId())
                    .build())
                    .ne("signal_type", SignalEnum.MASTER.getCode())
                    .ne("signal_type", SignalEnum.IR.getCode())
                    .notLike("product_type", "%switch%")
                    .ne("signal_type", SignalEnum.RF.getCode())).size();

            Product product = producerService.getById(userDevice.getProductId());

            if (product != null) {
                userDevice.setImagesUrl(product.getImagesUrl());
            }

            userDevice.setUserDeviceSize(size);

            userDevice.setRoomName(homeRoom.getRoomName());
        }
        return list;
    }


    @Override
    public List<UserDevice> masterHotelList(HomeIdDto dto) {
        List<UserDevice> list = this.list(new QueryWrapper<>(UserDevice.builder()
                .homeId(dto.getHomeId())
                .signalType(SignalEnum.MASTER.getCode())
                .build()));

        HomeRoom homeRoom;
        for (UserDevice userDevice : list) {
            homeRoom = homeRoomService.getById(userDevice.getRoomId());
            int size = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                    .masterDeviceId(userDevice.getDeviceId())
                    .build())
                    .notLike("product_type", "%switch%")).size();
            userDevice.setUserDeviceSize(size);

            userDevice.setRoomName(homeRoom.getRoomName());


            Product product = producerService.getById(userDevice.getProductId());

            if (product != null) {
                userDevice.setImagesUrl(product.getImagesUrl());
            }
        }
        return list;
    }

    @Override
    public List<UserDeviceFilterVo> listBySetRoomId(Set<Long> roomIdSet) {
        return this.baseMapper.listBySetRoomId(roomIdSet);
    }

    @Override
    public List<UserDeviceFilterVo> listByMasterDeviceId(String masterDeviceId) {
        return this.baseMapper.listByMasterDeviceId(masterDeviceId);
    }

    @Override
    public List<UserDeviceFilterVo> listByHomeId(Long homeId) {
        return this.baseMapper.listByHomeId(homeId);
    }

    @Override
    public List<UserDeviceVo> showSceneList(HomeRoomIdDto dto) {
        return this.baseMapper.showSceneList(dto.getRoomId(), dto.getHomeId());
    }

    @Override
    public UserDevice findDeviceByDeviceIdAndUserId(String targetId, String userId) {
        return this.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(targetId)
                .userId(userId).build()));
    }

    @Override
    public List<UserDevice> listByUserIdAndProductTypes(String userId, List<String> productTypes) {

        return this.list(new QueryWrapper<>(UserDevice.builder()
                .userId(userId)
                .build()).in("product_type", productTypes));
    }

    @Override
    public List<UserDevice> listEnableBindDevice(String masterDeviceId) {
        return this.baseMapper.listEnableBindDevice(masterDeviceId, null, null);
    }

    @Override
    public List<UserDevice> listEnableBindDevice(List<String> deviceIds, String userId, String masterDeviceId) {
        return this.baseMapper.listEnableBindDevice(masterDeviceId, deviceIds, userId);
    }

    @Override
    public UserDevice masterStatus(String masterDeviceId) {
        return masterStatus(masterDeviceId, null);
    }

    @Override
    public UserDevice masterStatus(String masterDeviceId, String userId) {
        UserDevice masterDevice = cacheService.get(masterDeviceId);
        if (masterDevice == null) {
            masterDevice = this.getOne(new QueryWrapper<>(UserDevice.builder()
                    .deviceId(masterDeviceId)
                    .signalType(SignalEnum.MASTER.getCode())
                    .build()));
            ValidUtils.isNullThrow(masterDevice, "主控设备不存在");
            cacheService.addSeconds("userDevice:" + masterDeviceId, masterDevice, 10);
        }
        ValidUtils.isFalseThrow(masterDevice.getStatus(), "主控设备已离线,device_id=" + masterDevice.getDeviceId());
        if (userId != null) {
            ValidUtils.isFalseThrow(masterDevice.getUserId().equals(userId), "主控数据不存在");
        }
        return masterDevice;
    }

    /**
     * 红外、射频、蓝牙、主控修改物理模型
     *
     * @param userDevice
     * @param changeThingModel
     */
    @Override
    public void saveChangeThingModel(UserDevice userDevice, ThingModel changeThingModel) {
        List<UserDevice> userDeviceList = this.list(new QueryWrapper<>(UserDevice.builder()
                .physicalDeviceId(userDevice.getPhysicalDeviceId())
                .build()));
        //窗帘特殊处理
        if (userDevice.getProductId().equals("10623229")) {
            List<ThingModelProperty> properties = changeThingModel.getProperties();
            for (int i = 0; i < properties.size(); i++) {
                if (properties.get(i).getIdentifier().equals("powerstate")) {
                    if (properties.get(i).getValue().equals("2")) {
                        properties.get(i).setValue("0");
                    }
                }
            }
        }
        //实设备和虚设备都修改
        for (UserDevice device : userDeviceList) {
            ThingModel thingModel = device.getThingModel();

            if (ThingModel.thingModelPropertyCopy(changeThingModel, thingModel)) {
                try {
//                    ThingModelProperty mode = thingModel.thingModel2Map().get("mode");
//                    List<UserDeviceMode> userDeviceModeList = userDeviceModeService.list(new QueryWrapper<>(UserDeviceMode.builder()
//                            .deviceId(userDevice.getDeviceId())
//                            .build()));
//                    ThingModel newThingModel = null;
//
//                    if (userDeviceModeList.size()>0&mode!=null){
//                        //遍历设备模式
//                        for (int i = 0; i < userDeviceModeList.size(); i++) {
//                            ThingModel thingModel1 = userDeviceModeList.get(0).getThingModel();
//                            List<ThingModelProperty> properties = thingModel1.getProperties();
//                            //遍历设备模式下的thingModel json对象 取到 匹配的mode值
//                            for (int j = 0; j < properties.size(); j++) {
//                                String s = properties.get(j).getDataType().getSpecs().get(mode.getValue());
//                                if (userDeviceModeList.get(i).getModeName().equals(s)) {
//                                    newThingModel= userDeviceModeList.get(i).getThingModel();
//                                        break;
//                                }
//                            }
//
//                        }
//
//                    }


//                    if(newThingModel!=null){
//                        this.updateById(UserDevice.builder()
//                                .deviceId(device.getDeviceId())
//                                .thingModel(newThingModel)
//                                .build());
//                    }else{
                    this.updateById(UserDevice.builder()
                            .deviceId(device.getDeviceId())
                            .thingModel(thingModel)
                            .build());
//                    }

//                    userDeviceModeService.getOne()

                } catch (Exception e) {
                    log.error("UserDeviceServiceImpl.saveChangeThingModel", e);
                }
            }
        }
    }


    //@Cacheable(value = "common-cache", key = "'Userdevice:'+#deviceId", unless = "#result == null")
    @Override
    public UserDevice getOneByIdCache(String deviceId) {
        return this.getById(deviceId);
    }

    @CacheEvict(value = "common-cache", key = "'Userdevice:'+#deviceId")
    @Override
    public void deleteCacheById(String deviceId) {
    }

    @Override
    public Map<String, String> getGroupMapData(Long homeId, String userId) {
        return this.list(new QueryWrapper<>(UserDevice.builder().homeId(homeId).userId(userId).build())).stream()
                .collect(Collectors.toMap(UserDevice::getDeviceId, UserDevice::getGroupId));
    }

    @Override
    public void controlDeviceStatus(String controlDeviceId, String userId) {
        if (StringUtils.isNotEmpty(controlDeviceId)) {
            UserDevice userDevice = getById(controlDeviceId);
            ValidUtils.isEqualsThrow(userId, userDevice.getUserId(), "控制器权限不足");
            ValidUtils.isFalseThrow(userDevice.getStatus(), "控制器设备离线");
        }
    }

    @Override
    public List<UserDevice> listControlDevice(ControlDeviceto controlDeviceto) {
        return this.list(new QueryWrapper<>(UserDevice.builder()
                .homeId(controlDeviceto.getHomeId())
                .userId(UserDto.getUser().getUId())
                .productId(controlDeviceto.getControlProductId()).build()));
    }

    @Override
    public UserDevice queryInfo(DeviceIdDto dto) {
        UserDevice userDevice = getById(dto.getDeviceId());
        ValidUtils.isNullThrow(userDevice, "设备数据不存在");
        HomeRoom homeRoom = homeRoomService.getById(userDevice.getRoomId());
        userDevice.setRoomName(homeRoom == null ? "" : homeRoom.getRoomName());
        Product product = producerService.getById(userDevice.getProductId());
        userDevice.setRelationDeviceTypeId(product.getRelationDeviceTypeId());

        UserAccount userAccount = userAccountService.getById(userDevice.getUserId());

        if (userAccount != null) {
            userDevice.setMobile(userAccount.getMobile());
        }

        //查看是否有虚设备
        List<UserDevice> subUserDeviceList = this.list(new QueryWrapper<>(UserDevice.builder()
                .physicalDeviceId(userDevice.getDeviceId())
                .signalType(SignalEnum.INVENTED.getCode())
                .build()));
//        for (UserDevice device : subUserDeviceList) {
//            homeRoom = homeRoomService.getById(device.getRoomId());
//            userDevice.setRoomName(homeRoom == null ? "" : homeRoom.getRoomName());
//        }
        userDevice.setSubUserDeviceList(subUserDeviceList);

        //查看是否有模式
        List<UserDeviceMode> userDeviceModeList = userDeviceModeService.list(new QueryWrapper<>(UserDeviceMode.builder()
                .deviceId(userDevice.getDeviceId())
                .build()));
        userDevice.setUserDeviceModeList(userDeviceModeList);

        return userDevice;
    }

    @Override
    public UserDevice matchUserDevice(String physicalDeviceId, String identifier) {
        List<UserDevice> list = this.list(new QueryWrapper<>(UserDevice.builder()
                .physicalDeviceId(physicalDeviceId).build()));
        int size = list.size();
        if (list.size() == 1) {
            return list.get(0);
        }

        for (UserDevice us : list) {
            if (!SignalEnum.INVENTED.getCode().equals(us.getSignalType())) {
                continue;
            }
            Set<String> set = us.getThingModel().identifierSet();
            if (set.contains(identifier)) {
                return us;
            }
            if ((list.indexOf(us) + 1) == size) {
                return list.get(1);
            }
        }
        return null;
    }

    @Override
    public UserDevice findDeviceByDeviceIdAndRoomId(String deviceId) {
        return this.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(deviceId)
                .build()));
    }

    @Override
    public List<UserDevice> listByCondition(Long homeId, String deviceId) {
        //拼接查询条件
        QueryWrapper<UserDevice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("home_id", homeId);
        if (null != deviceId && !deviceId.isEmpty()) {
            queryWrapper.eq("device_id", deviceId);
        }
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public DeviceWsVo getDeviceWsData(String deviceId) {
        return this.baseMapper.getDeviceWsData(deviceId);
    }

    @Override
    public long findBySumStatus(Long homeId) {
        return baseMapper.findBySumStatus(homeId);
    }

    @Override
    public List<UserDevice> findGroupId(String gatway) {
        return baseMapper.findGroupId(gatway);
    }
}
