package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.HomeRoomPageDto;
import com.lj.iot.biz.db.smart.entity.HomeRoom;
import com.lj.iot.biz.db.smart.mapper.HomeRoomMapper;
import com.lj.iot.biz.db.smart.service.IHomeRoomService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户房间表 服务实现类
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class HomeRoomServiceImpl extends ServiceImpl<HomeRoomMapper, HomeRoom> implements IHomeRoomService {

    @Override
    public IPage<HomeRoom> customPage(HomeRoomPageDto pageDto) {
        IPage<HomeRoom> page = PageUtil.page(pageDto);
        return this.baseMapper.customPage(page, pageDto);
    }

    @Override
    public Set<Long> setIdByHomeIdAndRoomName(Long homeId, String roomName) {
        return this.baseMapper.setIdByHomeIdAndHomeName(homeId, roomName);
    }

    @Override
    public HomeRoom first(Long homeId, String userId) {
        return this.baseMapper.first(homeId, userId);
    }

    @Override
    public List<HomeRoom> customList(Long homeId) {
        return this.baseMapper.customList(homeId);
    }

    @Override
    public List<HomeRoom> customList(Long homeId, String userId) {
        return this.baseMapper.customListByHomeIdAndUserId(homeId, userId);
    }

    @Override
    public List<HomeRoom> OfflineList(Long homeId, Long roomId) {

        return this.baseMapper.findListByHomeIdAndRoomId(homeId,roomId);
    }

    @Override
    public Map getRoomMap(Long homeId) {
        List<HomeRoom> homeRooms = this.baseMapper.customList(homeId);
        return homeRooms.stream().collect(Collectors.toMap(HomeRoom::getId,HomeRoom::getRoomName));
    }
}
