package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeRoomEditDto;
import com.lj.iot.biz.base.dto.HomeRoomPageDto;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.db.smart.entity.HomeRoom;

import java.util.List;

/**
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
public interface BizHomeRoomService {

    /**
     * 分页查询
     *
     * @return
     */
    IPage<HomeRoom> customPage(HomeRoomPageDto pageDto);

    HomeRoom edit(HomeRoomEditDto dto, String userId);

    HomeRoom add(Long homeId, String roomName, String userId);

    void delete(Long id, String userId);

    List<HomeRoom> OfflineList(String deviceId, Long roomId);
}
