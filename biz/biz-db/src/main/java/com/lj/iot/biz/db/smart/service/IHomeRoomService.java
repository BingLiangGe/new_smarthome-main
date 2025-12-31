package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.HomeRoomPageDto;
import com.lj.iot.biz.db.smart.entity.HomeRoom;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户房间表 服务类
 *
 * @author xm
 * @since 2022-07-13
 */
public interface IHomeRoomService extends IService<HomeRoom> {

    IPage<HomeRoom> customPage(HomeRoomPageDto pageDto);

    Set<Long> setIdByHomeIdAndRoomName(Long homeId, String homeName);

    HomeRoom first(Long homeId, String userId);

    List<HomeRoom> customList(Long homeId);

    List<HomeRoom> customList(Long homeId, String userId);

    List<HomeRoom> OfflineList(Long homeId, Long roomId);

    Map getRoomMap(Long homeId);
}
