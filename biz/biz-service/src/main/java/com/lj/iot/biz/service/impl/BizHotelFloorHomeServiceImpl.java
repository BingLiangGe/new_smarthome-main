package com.lj.iot.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizHomeService;
import com.lj.iot.biz.service.BizHotelFloorHomeService;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class BizHotelFloorHomeServiceImpl implements BizHotelFloorHomeService {

    @Resource
    BizHomeService bizHomeService;
    @Autowired
    private IHotelFloorService hotelFloorService;

    @Autowired
    private IHotelFloorHomeService hotelFloorHomeService;

    @Autowired
    private IApiConfigService apiConfigService;

    public String url = "/device/push/room";

    @Autowired
    private IHotelUserService hotelUserService;

    @Resource
    ISceneService sceneService;

    @Resource
    ISceneTemplateService sceneTemplateService;

    @DSTransactional
    @Override
    public Home add(HotelFloorHomeAddDto dto, Long hotelId, String userId) {

        //查询楼层是否符合条件
        HotelFloor hotelFloor = hotelFloorService.getOne(new QueryWrapper<>(HotelFloor.builder()
                .id(dto.getFloorId())
                .hotelUserId(userId)
                .hotelId(hotelId)
                .build()));
        System.out.println("hotelId:" + hotelId + "," + userId);
        ValidUtils.isNullThrow(hotelFloor, "楼层不存在");

        Home home = bizHomeService.hotelHomeAdd(HomeAddDto.builder().hotelId(hotelId).homeName(dto.getHomeName()).build(), userId);

        hotelFloorHomeService.save(HotelFloorHome.builder()
                .floorId(hotelFloor.getId())
                .hotelUserId(userId)
                .hotelId(hotelId)
                .homeId(home.getId())
                .build());


        //默认绑定4个场景
        List<SceneTemplate> listTemplate = sceneTemplateService.list();
        for (int i = 0; i < listTemplate.size(); i++) {
            //保存场景数据
            Scene scene = Scene.builder()
                    .userId(userId)
                    .homeId(home.getId())
                    .sceneIcon(listTemplate.get(i).getBackgroundUrl())
                    .sceneName(listTemplate.get(i).getName())
                    .isDefault(1)
                    .command(listTemplate.get(i).getName())
                    .createTime(LocalDateTime.now().withSecond(i))
                    .build();
            sceneService.save(scene);
        }

        List<HotelUser> list = hotelUserService.list(new QueryWrapper<>(HotelUser.builder().hotelUserId(userId).build()));
        if (list.size() > 0) {
            HotelFloorHome one = hotelFloorHomeService.getOne(new QueryWrapper<>(HotelFloorHome.builder().homeId(home.getId()).hotelUserId(userId).build()));

            JSONObject params = new JSONObject();
            params.put("room_id", home.getId());
            params.put("room_name", dto.getHomeName());
            params.put("store_id", one.getHotelId());
            try {
                apiConfigService.sendApiConfigData(params, url);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return home;
    }

    @DSTransactional
    @Override
    public Home edit(HotelFloorHomeEditDto dto, Long hotelId, String userId) {
        //查询楼层是否符合条件
        HotelFloor hotelFloor = hotelFloorService.getOne(new QueryWrapper<>(HotelFloor.builder()
                .id(dto.getFloorId())
                .hotelUserId(userId)
                .hotelId(hotelId)
                .build()));
        ValidUtils.isNullThrow(hotelFloor, "楼层不存在");

        HotelFloorHome floorHome = hotelFloorHomeService.getOne(new QueryWrapper<>(HotelFloorHome.builder()
                .hotelUserId(userId)
                .hotelId(hotelId)
                .homeId(dto.getHomeId())
                .build()));
        ValidUtils.isNullThrow(floorHome, "家庭不存在");


        Home home = bizHomeService.edit(HomeEditDto.builder()
                .homeId(dto.getHomeId())
                .homeName(dto.getHomeName())
                .build(), userId);

        hotelFloorHomeService.updateById(HotelFloorHome.builder()
                .id(floorHome.getId())
                .floorId(hotelFloor.getId())
                .build());
        List<HotelUser> list = hotelUserService.list(new QueryWrapper<>(HotelUser.builder().hotelUserId(userId).build()));
        if (list.size() > 0) {
            HotelFloorHome one = hotelFloorHomeService.getOne(new QueryWrapper<>(HotelFloorHome.builder().homeId(dto.getHomeId()).hotelUserId(userId).build()));

            JSONObject params = new JSONObject();
            params.put("room_id", dto.getHomeId());
            params.put("room_name", dto.getHomeName());
            params.put("store_id", one.getHotelId());
            try {
                apiConfigService.sendApiConfigData(params, url);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return home;
    }

    @DSTransactional
    @Override
    public void delete(HomeIdDto dto, Long hotelId, String userId) {

        HotelFloorHome floorHome = hotelFloorHomeService.getOne(new QueryWrapper<>(HotelFloorHome.builder()
                .hotelUserId(userId)
                .hotelId(hotelId)
                .homeId(dto.getHomeId())
                .build()));

        ValidUtils.isNullThrow(floorHome, "家庭不存在");

        bizHomeService.deleteHomeById(floorHome.getHomeId(), userId);
        hotelFloorHomeService.removeById(floorHome.getId());
    }
}
