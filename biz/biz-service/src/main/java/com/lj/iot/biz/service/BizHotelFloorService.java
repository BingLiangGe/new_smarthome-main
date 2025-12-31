package com.lj.iot.biz.service;

import com.lj.iot.biz.base.dto.FloorIdDto;
import com.lj.iot.biz.base.dto.HotelFloorAddDto;
import com.lj.iot.biz.base.dto.HotelFloorEditDto;
import com.lj.iot.biz.base.vo.FloorVo;
import com.lj.iot.biz.db.smart.entity.HotelFloor;

import java.util.List;
import java.util.Map;

/**
 * 楼层
 */
public interface BizHotelFloorService {

    HotelFloor add(HotelFloorAddDto dto, Long hotelId, String userId);
    HotelFloor edit(HotelFloorEditDto dto, Long hotelId, String userId);
    void delete(FloorIdDto dto, Long hotelId, String userId);

    List<FloorVo> listFloorHomeVo(Long hotelId, String userId);

    List<HotelFloor> listFloor(Long hotelId, String actualUserId);

    List<Map> findFloorRoom(Long floorId, String actualUserId);
}
