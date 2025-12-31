package com.lj.iot.biz.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.FloorIdDto;
import com.lj.iot.biz.base.dto.HotelFloorAddDto;
import com.lj.iot.biz.base.dto.HotelFloorEditDto;
import com.lj.iot.biz.base.vo.FloorHomeVo;
import com.lj.iot.biz.base.vo.FloorVo;
import com.lj.iot.biz.db.smart.entity.HotelFloor;
import com.lj.iot.biz.db.smart.entity.HotelFloorHome;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IHotelFloorHomeService;
import com.lj.iot.biz.db.smart.service.IHotelFloorService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizHomeService;
import com.lj.iot.biz.service.BizHotelFloorService;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BizHotelFloorServiceImpl implements BizHotelFloorService {

    @Autowired
    private IHotelFloorService hotelFloorService;

    @Autowired
    private IHotelFloorHomeService hotelFloorHomeService;

    @Autowired
    private BizHomeService bizHomeService;
    @Autowired
    IUserDeviceService userDeviceService;


    @Override
    public HotelFloor add(HotelFloorAddDto dto, Long hotelId, String userId) {
        HotelFloor hotelFloor = HotelFloor.builder()
                .hotelId(hotelId)
                .hotelUserId(userId)
                .floorName(dto.getFloorName())
                .build();
        hotelFloorService.save(hotelFloor);
        return hotelFloorService.getById(hotelFloor.getId());
    }

    @Override
    public HotelFloor edit(HotelFloorEditDto dto, Long hotelId, String userId) {
        HotelFloor hotelFloor = hotelFloorService.getOne(new QueryWrapper<>(HotelFloor.builder()
                .id(dto.getFloorId())
                .hotelId(hotelId)
                .hotelUserId(userId)
                .build()));
        ValidUtils.isNullThrow(hotelFloor, "数据不存在");

        hotelFloorService.updateById(HotelFloor.builder()
                .id(hotelFloor.getId())
                .floorName(dto.getFloorName())
                .build());

        return hotelFloorService.getById(hotelFloor.getId());
    }

    @DSTransactional
    @Override
    public void delete(FloorIdDto dto, Long hotelId, String userId) {
        HotelFloor hotelFloor = hotelFloorService.getOne(new QueryWrapper<>(HotelFloor.builder()
                .id(dto.getFloorId())
                .hotelId(hotelId)
                .hotelUserId(userId)
                .build()));
        ValidUtils.isNullThrow(hotelFloor, "数据不存在");

        //查询家庭、删除家庭
        List<HotelFloorHome> hotelFloorHomeList = hotelFloorHomeService.list(new QueryWrapper<>(HotelFloorHome.builder()
                .floorId(hotelFloor.getId())
                .build()));

        for (HotelFloorHome hotelFloorHome : hotelFloorHomeList) {
            bizHomeService.deleteHomeById(hotelFloorHome.getHomeId(), userId);
        }

        hotelFloorHomeService.remove(new QueryWrapper<>(HotelFloorHome.builder()
                .floorId(hotelFloor.getId()).build()));

        hotelFloorService.removeById(hotelFloor.getId());
    }

    @Override
    public List<FloorVo> listFloorHomeVo(Long hotelId, String userId) {

        List<FloorHomeVo> floorHomeVoList = hotelFloorHomeService.listFloorHomeVo(hotelId, userId);
        Map<Long, List<FloorHomeVo>> map = new HashMap<>();
        Map<Long, Long> mapOutLine = new HashMap<>();
        List<FloorHomeVo> subList;
        Long sumOutLine;
        for (FloorHomeVo floorHomeVo : floorHomeVoList) {
            subList = map.computeIfAbsent(floorHomeVo.getFloorId(), k -> new ArrayList<>());
            sumOutLine = mapOutLine.computeIfAbsent(floorHomeVo.getFloorId(), k -> 0L);
            subList.add(floorHomeVo);
            sumOutLine += floorHomeVo.getOutLine();
            mapOutLine.put(floorHomeVo.getFloorId(), sumOutLine);
        }

        List<HotelFloor> hotelFloorList = hotelFloorService.list(new QueryWrapper<>(HotelFloor.builder()
                .hotelId(hotelId)
                .hotelUserId(userId)
                .build()));

        List<FloorVo> list = new ArrayList<>();
        for (HotelFloor hotelFloor : hotelFloorList) {
            list.add(FloorVo.builder()
                    .floorId(hotelFloor.getId())
                    .floorName(hotelFloor.getFloorName())
                    .outLine(mapOutLine.get(hotelFloor.getId()))
                    .list(map.get(hotelFloor.getId()))
                    .build());
        }

        return list;
    }

    @Override
    public List<HotelFloor> listFloor(Long hotelId, String userId) {

        List<HotelFloor> hotelFloorList = hotelFloorService.list(new QueryWrapper<>(HotelFloor.builder()
                .hotelId(hotelId)
                .hotelUserId(userId)
                .build()));
        return hotelFloorList;
    }

    @Override
    public List<Map> findFloorRoom(Long floorId, String userId) {
        List<FloorHomeVo> floorHomeVos = hotelFloorHomeService.listFloorHomeByFloorId(floorId, userId);
        List<Map> homeList = new ArrayList<>();
        //房间下面找设备列表
        for (FloorHomeVo floorHomeVo:
                floorHomeVos) {
            Map homeMap  = new HashMap();
            List<UserDevice> deviceList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                    .homeId(floorHomeVo.getHomeId())
                    .build()).notLike("product_type","%switch%"));
            homeMap.put("homeName",floorHomeVo.getHomeName());
            homeMap.put("homeId",floorHomeVo.getHomeId());
            homeMap.put("list",deviceList.size());
            homeList.add(homeMap);
        }
        return homeList;
    }
}
