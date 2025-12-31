package com.lj.iot.api.hotel.web.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.HotelAddDto;
import com.lj.iot.biz.base.dto.HotelEditDto;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.db.smart.entity.Hotel;
import com.lj.iot.biz.db.smart.entity.HotelFloor;
import com.lj.iot.biz.db.smart.entity.HotelUser;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 酒店
 */
@Slf4j
@RestController
@RequestMapping("api/auth/hotel")
public class HotelController {

    @Autowired
    private IHotelUserService hotelUserService;

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;
    public  String url = String.format("/device/push/store");

    @Autowired
    private IApiConfigService apiConfigService;

    @Autowired
    private IHotelService hotelService;

    @Autowired
    private IHotelFloorService hotelFloorService;

    /**
     * 用户酒店列表
     */
    @GetMapping("/list")
    public CommonResultVo<List<Hotel>> list() {
        return CommonResultVo.SUCCESS(hotelService.customList(UserDto.getUser().getUId()));
    }

    /**
     * 新增酒店
     */
    @PostMapping("/add")
    @CustomPermissions("hotel:add")
    public CommonResultVo<Hotel> add(@RequestBody @Valid HotelAddDto dto) {
        UserDto userDto = UserDto.getUser();

        HotelUserAccount hotelUserAccount = hotelUserAccountService.getById(userDto.getUId());

        ValidUtils.isNullThrow(hotelUserAccount, "用户不存在");
        ValidUtils.isFalseThrow(AccountTypeEnum.HOTEL.getCode().equals(hotelUserAccount.getType()), "管理员账号才能新增酒店");

        //判断酒店名是否存在
        long count = hotelService.count(new QueryWrapper<>(Hotel.builder()
                .hotelName(dto.getHotelName())
                .hotelUserId(hotelUserAccount.getId())
                .build()));

        ValidUtils.isFalseThrow(count == 0L, "酒店名已存在");

        //创建一个酒店
        Hotel hotel = Hotel.builder()
                .hotelName(dto.getHotelName())
                .hotelUserId(hotelUserAccount.getId())
                .build();
        hotelService.save(hotel);

        //酒店成员
        HotelUser hotelUser = HotelUser.builder()
                .hotelId(hotel.getId())
                .hotelUserId(hotelUserAccount.getId())
                .isMain(true)
                .isDefault(true)
                .memberUserId(hotelUserAccount.getId())
                .build();
        hotelUserService.save(hotelUser);
        //hotel.getId() store_id
        //dto.getHotelName() store_name
        //http://admin.hs499.com/device/push/store

        JSONObject params = new JSONObject();
        params.put("store_id", hotel.getId()+"");
        params.put("store_name", dto.getHotelName());
        apiConfigService.sendApiConfigData(params,url);


        return CommonResultVo.SUCCESS(hotelService.getById(hotel.getId()));
    }

    /**
     * 编辑酒店
     */
    @PostMapping("/edit")
    @CustomPermissions("hotel:edit")
    public CommonResultVo<Hotel> edit(@RequestBody @Valid HotelEditDto dto) {

        Hotel hotel = hotelService.getOne(new QueryWrapper<>(Hotel.builder()
                .id(dto.getId())
                .hotelUserId(UserDto.getUser().getActualUserId())
                .build()));

        ValidUtils.isNullThrow(hotel, "酒店不存在");

        //判断酒店名是否存在
        long count = hotelService.count(new QueryWrapper<>(Hotel.builder()
                .hotelName(dto.getHotelName())
                .hotelUserId(UserDto.getUser().getActualUserId())
                .build()));

        ValidUtils.isFalseThrow(count == 0L, "酒店名已存在");

        hotelService.updateById(Hotel.builder()
                .id(hotel.getId())
                .hotelName(dto.getHotelName())
                .build());

        JSONObject params = new JSONObject();
        params.put("store_id", hotel.getId()+"");
        params.put("store_name", dto.getHotelName());

        /*JSONObject params = new JSONObject();
        params.put("msgtype", "text");
        params.put("text", map);*/
        apiConfigService.sendApiConfigData(params,url);


        return CommonResultVo.SUCCESS(hotelService.getById(hotel.getId()));
    }

    /**
     * 删除酒店
     */
    @PostMapping("/delete")
    @CustomPermissions("hotel:delete")
    public CommonResultVo<String> delete(@RequestBody @Valid IdDto dto) {

        Hotel hotel = hotelService.getOne(new QueryWrapper<>(Hotel.builder()
                .id(dto.getId())
                .build()));

        ValidUtils.isNullThrow(hotel, "酒店不存在");
        ValidUtils.isNullThrow(hotel.getHotelUserId().equals(UserDto.getUser().getUId()),
                "管理员账号才能删除酒店");

        //楼层
        long count = hotelFloorService.count(new QueryWrapper<>(HotelFloor.builder()
                .hotelId(hotel.getId())
                .build()));

        ValidUtils.isFalseThrow(count == 0L, "先删除楼层");
        hotelService.removeById(hotel.getId());
        return CommonResultVo.SUCCESS();
    }
}
