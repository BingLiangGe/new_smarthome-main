package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeAddDto;
import com.lj.iot.biz.base.dto.HomeEditDto;
import com.lj.iot.biz.base.dto.HomeRoomPageDto;
import com.lj.iot.biz.base.vo.HomePageVo;
import com.lj.iot.biz.db.smart.entity.Home;
import com.lj.iot.biz.db.smart.entity.HomeRoom;

import java.util.List;

public interface BizHomeService {
    Home add(HomeAddDto homeDto, String userId);

    Home hotelHomeAdd(HomeAddDto homeDto, String userId);

    Home edit(HomeEditDto homeDto, String userId);

    /**
     * 删除家
     *
     * @param homeId 家ID
     * @param userId 用户ID
     * @return
     */
    void deleteHomeById(Long homeId, String userId);


    List<HomeRoom> buildDefaultRoom(Long homeId, String userId);

    IPage<HomePageVo> customPage(HomeRoomPageDto pageDto);

}
