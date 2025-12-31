package com.lj.iot.biz.service;

import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.base.dto.HotelFloorHomeAddDto;
import com.lj.iot.biz.base.dto.HotelFloorHomeEditDto;
import com.lj.iot.biz.db.smart.entity.Home;

/**
 * 楼层
 */
public interface BizHotelFloorHomeService {

    Home add(HotelFloorHomeAddDto dto, Long hotelId, String userId);

    Home edit(HotelFloorHomeEditDto dto, Long hotelId, String userId);

    void delete(HomeIdDto dto, Long hotelId, String userId);
}
