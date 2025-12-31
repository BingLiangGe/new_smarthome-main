package com.lj.iot.api.hotel.web.auth;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.base.dto.HotelFloorHomeAddDto;
import com.lj.iot.biz.base.dto.HotelFloorHomeEditDto;
import com.lj.iot.biz.base.vo.FloorHomeVo;
import com.lj.iot.biz.base.vo.FloorVo;
import com.lj.iot.biz.db.smart.entity.Home;
import com.lj.iot.biz.db.smart.entity.HotelFloor;
import com.lj.iot.biz.db.smart.service.IHomeService;
import com.lj.iot.biz.db.smart.service.IHotelFloorHomeService;
import com.lj.iot.biz.db.smart.service.IHotelFloorService;
import com.lj.iot.biz.service.BizHotelFloorHomeService;
import com.lj.iot.biz.service.BizHotelFloorService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 楼层家庭
 */
@Slf4j
@RestController
@RequestMapping("api/auth/hotel_floor_home")
public class HotelFloorHomeController {

    @Autowired
    private BizHotelFloorHomeService bizHotelFloorHomeService;


    @Autowired
    private IHotelFloorService hotelFloorService;

    @Autowired
    private IHotelFloorHomeService hotelFloorHomeService;


    @Autowired
    private BizHotelFloorService bizHotelFloorService;

    @RequestMapping("/getHomeList")
    public CommonResultVo getHomeList() {

        Long hotelId=UserDto.getUser().getHotelId();
        String userId=UserDto.getUser().getActualUserId();

        List<FloorHomeVo> floorHomeVoList = hotelFloorHomeService.listFloorHomeVo(hotelId, userId);
        Map<Long, List<FloorHomeVo>> map = new HashMap<>();
        Map<Long, Long> mapOutLine = new HashMap<>();
        List<FloorHomeVo> subList = null;
        Long sumOutLine;
        for (FloorHomeVo floorHomeVo : floorHomeVoList) {
            subList = map.computeIfAbsent(floorHomeVo.getFloorId(), k -> new ArrayList<>());
            sumOutLine = mapOutLine.computeIfAbsent(floorHomeVo.getFloorId(), k -> 0L);
            subList.add(floorHomeVo);
            sumOutLine += floorHomeVo.getOutLine();
            mapOutLine.put(floorHomeVo.getFloorId(), sumOutLine);
        }

        return CommonResultVo.SUCCESS(subList);
    }

    /**
     * 新增楼层
     */
    @CustomPermissions("hotel_floor_home:add")
    @PostMapping("add")
    public CommonResultVo<Home> add(@RequestBody @Valid HotelFloorHomeAddDto dto) {
        return CommonResultVo.SUCCESS(bizHotelFloorHomeService.add(dto,
                UserDto.getUser().getHotelId(),
                UserDto.getUser().getActualUserId()));
    }

    /**
     * 编辑
     */
    @CustomPermissions("hotel_floor_home:edit")
    @PostMapping("edit")
    public CommonResultVo<Home> edit(@RequestBody @Valid HotelFloorHomeEditDto dto) {
        return CommonResultVo.SUCCESS(bizHotelFloorHomeService.edit(dto,
                UserDto.getUser().getHotelId(),
                UserDto.getUser().getActualUserId()));
    }


    /**
     * 删除
     */
    @CustomPermissions("hotel_floor_home:delete")
    @PostMapping("delete")
    public CommonResultVo<String> delete(@RequestBody @Valid HomeIdDto dto) {
        bizHotelFloorHomeService.delete(dto, UserDto.getUser().getHotelId(), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }
}
