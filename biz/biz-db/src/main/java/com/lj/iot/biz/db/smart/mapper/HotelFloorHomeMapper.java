package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.base.vo.FloorHomeVo;
import com.lj.iot.biz.base.vo.HotelFloorRoomVo;
import com.lj.iot.biz.db.smart.entity.HotelFloorHome;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 楼层-房间关系表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface HotelFloorHomeMapper extends BaseMapper<HotelFloorHome> {

    List<FloorHomeVo> listFloorHomeVo(@Param("hotelId") Long hotelId, @Param("userId") String userId);

    List<FloorHomeVo> listFloorHomeByFloorId(@Param("floorId") Long floorId, @Param("userId") String userId);

    /**
     * 获取酒店楼层房间信息
     * @param dto
     * @return
     */
    List<HotelFloorRoomVo> getHotelFloorRoomList(@Param("params") HotelFloorRoomVo dto);

    /**
     * 查询iot酒店房间
     * @param iotRoomId
     * @param iotHotelId
     * @return
     */
    HotelFloorRoomVo getHotelFloorRoom(@Param("roomId") Long iotRoomId,@Param("hotelId") Long iotHotelId);
}
