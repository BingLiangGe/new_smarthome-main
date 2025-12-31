package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeIdRoomIdPageDto;
import com.lj.iot.biz.base.dto.UserDevicePageDto;
import com.lj.iot.biz.base.vo.*;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 用户设备表 Mapper 接口
 *
 * @author xm
 * @since 2022-07-13
 */
public interface UserDeviceMapper extends BaseMapper<UserDevice> {


    @Select("SELECT um.`identifier`,um.`scene_id` FROM product_thing_model_key pt\n" +
            "LEFT JOIN user_device_mesh_key um ON um.`product_key_id`=pt.`id` WHERE um.`device_id`=#{deviceId};")
    List<SceneThingModelVo> getSceneThingModel(String deviceId);


    @Select("SELECT hf.floor_name,h.home_name FROM user_device ud \n" +
            "LEFT JOIN hotel_floor_home fh ON fh.home_id=ud.home_id\n" +
            "LEFT JOIN hotel_floor  hf ON hf.id=fh.floor_id\n" +
            "LEFT JOIN home h ON h.id=ud.home_id\n" +
            "WHERE ud.device_id=#{masterDeviceId} \n")
    HotelCallVo getDeviceHotelCall(String masterDeviceId);

    @Select("SELECT ud.`device_id` FROM user_device ud\n" +
            "LEFT JOIN hotel_floor_home hf ON hf.`home_id`=ud.`home_id`\n" +
            "LEFT JOIN home h ON h.`id`=ud.`home_id`\n" +
            "LEFT JOIN `hotel_floor` hff ON hff.`id`=hf.`floor_id`\n" +
            " WHERE ud.user_id='20230925144220758699849934745600' AND ud.product_type='scene_card' AND hf.`hotel_id`=9419;")
    List<String> findOxHotelDevice();

    @Select("SELECT ud.`device_id` FROM user_device ud\n" +
            "LEFT JOIN user_device mud ON mud.`device_id`=ud.`master_device_id`\n" +
            "WHERE mud.`status`=1 AND ud.`status`=0")
    List<String> editDeviceContextStatus();

    WatchInfoVo findWatchInfo(String deviceId);


    List<UserDevice> findHotelDevicePageLimit(@Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize,
                                              @Param("userDevice") UserDevice userDevice);

    Integer findHotelDevicePageCount(@Param("userDevice") UserDevice userDevice);

    List<UserDevice> findUserDeviceWithOtaLimit(@Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize,
                                                @Param("userDevice") UserDevice userDevice);

    Integer findUserDeviceWithOtaLimitCount(
            @Param("userDevice") UserDevice userDevice);


    @Select("SELECT\n" +
            "ud.`device_id` \n" +
            "FROM\n" +
            "  user_device ud\n" +
            "WHERE ud.`status` = 1\n" +
            "  AND ud.`product_type` = 'gatway_872'\n" +
            "  AND ud.`status_time` <= CONCAT (\n" +
            "    DATE_FORMAT(\n" +
            "      DATE_SUB(NOW(), INTERVAL 300 SECOND),'%Y%m%d%H%i%S'),'000'\n" +
            "  );\n" +
            "  ")
    List<String> findMasterDeviceDownStatus();


    @Select("SELECT\n" +
            "ud.`device_id` \n" +
            "FROM\n" +
            "  user_device ud\n" +
            "WHERE ud.`status` = 1\n" +
            "  AND ud.`product_type` = 'smart_watch'\n" +
            "  AND ud.`status_time` <= CONCAT (\n" +
            "    DATE_FORMAT(\n" +
            "      DATE_SUB(NOW(), INTERVAL 300 SECOND),'%Y%m%d%H%i%S'),'000'\n" +
            "  );\n" +
            "  ")
    List<String> findWatchDeviceDownStatus();

    @Select("\n" +
            "SELECT\n" +
            "  ud.`device_id`\n" +
            "FROM\n" +
            "  user_device ud\n" +
            "WHERE  ud.`product_type` IN ('curtain','socket','light_ct') \n" +
            "AND ud.`status_time` <= CONCAT(\n" +
            "    DATE_FORMAT(\n" +
            "      DATE_SUB(NOW(), INTERVAL 180  SECOND),\n" +
            "      '%Y%m%d%H%i%S'\n" +
            "    ),\n" +
            "    '000'\n" +
            "  )\n" +
            "  AND `status` =1\n")
    List<String> findUserDeviceChirStatus();

    IPage<UserDevice> customPage(IPage<UserDevice> page, @Param("params") UserDevicePageDto pageDto);

    Integer online(@Param("params") UserDevicePageDto pageDto);

    Integer offline(@Param("params") UserDevicePageDto pageDto);

    IPage<UserDeviceVo> customShowPage(IPage<UserDevice> page, @Param("params") HomeIdRoomIdPageDto pageDto);


    IPage<UserDeviceVo> customShowPage3326(IPage<UserDevice> page, @Param("params") HomeIdRoomIdPageDto pageDto);

    List<UserDeviceVo> showSceneList(@Param("roomId") Long roomId, @Param("homeId") Long homeId);

    List<UserDevice> listEnableBindDevice(@Param("masterDeviceId") String masterDeviceId
            , @Param("deviceIds") List<String> deviceIds, @Param("userId") String userId);

    List<UserDeviceFilterVo> listBySetRoomId(@Param("roomIdSet") Set<Long> roomIdSet);

    List<UserDeviceFilterVo> listByMasterDeviceId(@Param("masterDeviceId") String masterDeviceId);

    List<UserDeviceFilterVo> listByHomeId(@Param("homeId") Long homeId);

    Long getHomeIdById(@Param("deviceId") String deviceId);

    DeviceWsVo getDeviceWsData(@Param("deviceId") String deviceId);

    long findBySumStatus(@Param("homeId") Long homeId);

    List<UserDevice> findGroupId(@Param("gatway") String gatway);
}
