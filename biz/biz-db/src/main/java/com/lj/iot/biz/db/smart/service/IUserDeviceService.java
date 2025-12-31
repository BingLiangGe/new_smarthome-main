package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.vo.*;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.util.PageUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户设备表 服务类
 *
 * @author xm
 * @since 2022-07-13
 */
public interface IUserDeviceService extends IService<UserDevice> {

    List<SceneThingModelVo> getSceneThingModel(String deviceId);

    HotelCallVo getDeviceHotelCall(String masterDeviceId);

    List<String> editDeviceContextStatus();

    WatchInfoVo findWatchInfo(String deviceId);

    /**
     * 发送token指令
     * @return
     */
    CommonResultVo<String> sendTokenComment();

    PageUtil<UserDevice> findHotelDevicePage(Integer pageIndex, Integer pageSize, UserDevice userDevice);

    PageUtil<UserDevice> findUserDeviceWithOta(Integer pageIndex, Integer pageSize, UserDevice userDevice);

    /**
     * 查看3326家设备
     *
     * @param homeId
     * @return
     */
    List<UserDevice> selectUserDeviceWith3326(Long homeId);

    void updateUserDeviceStatusBatch(List<String> deviceIds);

    List<String> findMasterDeviceDownStatus();


    List<String> findWatchDeviceDownStatus();

    List<String> findUserDeviceChirStatus();

    IPage<UserDevice> customPage(UserDevicePageDto pageDto);

    UserDeviceStatisticsVo statistics(UserDevicePageDto pageDto);


    IPage<UserDeviceVo> customShowPage(HomeIdRoomIdPageDto pageDto);


    IPage<UserDeviceVo> customShowPage3326(HomeIdRoomIdPageDto pageDto);

    /**
     * 查看主控设备列表数据
     *
     * @param dto
     * @return
     */
    List<UserDevice> masterList(HomeIdDto dto);


    /**
     * 查看主控设备列表数据
     *
     * @param dto
     * @return
     */
    List<UserDevice> masterHotelList(HomeIdDto dto);

    List<UserDeviceFilterVo> listBySetRoomId(Set<Long> roomIdSet);

    List<UserDeviceFilterVo> listByMasterDeviceId(String masterDeviceId);

    List<UserDeviceFilterVo> listByHomeId(Long roomId);

    /**
     * 查询子设备列表
     *
     * @param dto
     * @return
     */
    List<UserDeviceVo> showSceneList(HomeRoomIdDto dto);

    /**
     * 通过设备ID和用户ID获取设备数据
     *
     * @param targetId 设备ID
     * @param userId   用户ID
     * @return
     */
    UserDevice findDeviceByDeviceIdAndUserId(String targetId, String userId);


    List<UserDevice> listByUserIdAndProductTypes(String userId, List<String> productTypes);

    /**
     * 通过主控获取所有可以绑定的设备
     *
     * @param masterDeviceId 主控ID
     * @return
     */
    List<UserDevice> listEnableBindDevice(String masterDeviceId);

    /**
     * 获取设备集合
     *
     * @param deviceIds
     * @param uId
     * @return
     */
    List<UserDevice> listEnableBindDevice(List<String> deviceIds, String uId, String masterDeviceId);

    UserDevice masterStatus(String masterDeviceId);

    UserDevice masterStatus(String masterDeviceId, String userId);

    /**
     * 模型变动保存
     *
     * @param userDevice
     * @param changeThingModel
     */
    void saveChangeThingModel(UserDevice userDevice, ThingModel changeThingModel);

    UserDevice getOneByIdCache(String deviceId);


    void deleteCacheById(String deviceId);

    /**
     * 获取设备组以及设备ID数据
     *
     * @return
     */
    Map<String, String> getGroupMapData(Long homeId, String userId);

    void controlDeviceStatus(String controlDeviceId, String userId);

    List<UserDevice> listControlDevice(ControlDeviceto controlDeviceto);

    UserDevice queryInfo(DeviceIdDto dto);

    UserDevice matchUserDevice(String physicalDeviceId, String identifier);

    UserDevice findDeviceByDeviceIdAndRoomId(String deviceId);

    List<UserDevice> listByCondition(Long homeId, String deviceId);

    DeviceWsVo getDeviceWsData(String deviceId);

    long findBySumStatus(Long homeId);

    List<UserDevice> findGroupId(String gatway);
}
