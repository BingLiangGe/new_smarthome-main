package com.lj.iot.api.system.web.auth;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.IApiConfigService;
import com.lj.iot.biz.db.smart.service.IHotelService;
import com.lj.iot.biz.db.smart.service.IHotelUserAccountService;
import com.lj.iot.biz.db.smart.service.IHotelUserService;
import com.lj.iot.biz.service.BizHotelFloorHomeService;
import com.lj.iot.biz.service.BizHotelFloorService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.PlatFormEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth/hotel")
public class HotelController {

    private static String URL = String.format("/device/push/store");

    @Autowired
    private IHotelService hotelService;

    @Autowired
    private BizHotelFloorService bizHotelFloorService;

    @Autowired
    private IHotelUserService hotelUserService;

    @Autowired
    private BizHotelFloorHomeService bizHotelFloorHomeService;

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;

    @Autowired
    private IApiConfigService apiConfigService;


    /**
     * 查询门店信息_筛选
     *
     * @param hotelId
     * @param floorId
     * @param homeName
     * @return
     */
    @RequestMapping("/getHomeByInfo")
    public CommonResultVo<List<Map>> getHomeByInfo(Integer hotelId, Integer floorId, String homeName) {
        ValidUtils.isNullThrow(hotelId, "hotelId 必传");
        ValidUtils.isNullThrow(floorId, "floorId 必传");
        try {
            log.info("url={}", URLEncoder.encode(homeName, "UTF-8"));
            homeName = URLDecoder.decode(homeName, "UTF-8");
            log.info("url_encode={}", URLDecoder.decode(homeName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        ValidUtils.isNullThrow(homeName, "homeName 必传");

        return CommonResultVo.SUCCESS(hotelService.getHomeByInfo(hotelId, floorId, homeName));
    }

    /**
     * 新增酒店
     *
     * @param dto
     * @return
     */
    @PostMapping("/hotelAdd")
   // @CustomPermissions("hotel:add")
    public CommonResultVo<Hotel> add(@RequestBody @Valid HotelAddDto dto) {
        UserDto userDto = UserDto.getUser();

        HotelUserAccount hotelUserAccount = hotelUserAccountService.getById(userDto.getUId());

        ValidUtils.isNullThrow(hotelUserAccount, "用户不存在");
        ValidUtils.isFalseThrow(AccountTypeEnum.HOTEL.getCode().equals(hotelUserAccount.getType()), "管理员账号才能新增酒店");

        //判断酒店名是否存在
        List<Hotel> list = hotelService.list(new QueryWrapper<>(Hotel.builder()
                .hotelName(dto.getHotelName())
                .hotelUserId(hotelUserAccount.getId())
                .build()));

        if (list.size() > 0) {
            return CommonResultVo.INSTANCE(-5, "门店已存在", list.get(0));
        }

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
        params.put("store_id", hotel.getId() + "");
        params.put("store_name", dto.getHotelName());
        apiConfigService.sendApiConfigData(params, URL);

        HotelFloorAddDto floorDto = HotelFloorAddDto.builder().floorName("默认楼层").build();

        bizHotelFloorService.add(floorDto,
                hotel.getId(),
                hotel.getHotelUserId());


        return CommonResultVo.SUCCESS(hotelService.getById(hotel.getId()));
    }


    /**
     * 用户酒店列表
     */
    @GetMapping("/list")
    public CommonResultVo<List<Hotel>> list() {
        return CommonResultVo.SUCCESS(hotelService.customList(UserDto.getUser().getUId()));
    }

    /**
     * 查询楼梯
     *
     * @return
     */
    @GetMapping("find_hotel_floor")
    public CommonResultVo<List<HotelFloor>> findHotelFloor() {
        UserDto userDto = UserDto.getUser();

        HotelUser hotelUser = hotelUserService.getOne(new QueryWrapper<>(HotelUser.builder()
                .memberUserId(userDto.getUId())
                .isDefault(true).build()));

        log.info("hotelId={},userId={}", hotelUser.getHotelId(), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS(bizHotelFloorService.listFloor(hotelUser.getHotelId(), UserDto.getUser().getActualUserId()));
    }

    /**
     * 根据楼层ID查询房间
     *
     * @return
     */
    @GetMapping("find_hotel_floor_room")
    public CommonResultVo<List<Map>> findHotelFloorRoom(@Valid FloorIdDto dto) {
        return CommonResultVo.SUCCESS(bizHotelFloorService.findFloorRoom(dto.getFloorId(), UserDto.getUser().getActualUserId()));
    }

    /**
     * 设置默认酒店
     */
   // @CustomPermissions("hotel_user:set_default")
    @PostMapping("set_default")
    public CommonResultVo<String> setDefault(@RequestBody @Valid HotelIdDto dto) {

        UserDto userDto = UserDto.getUser();
        HotelUser hotelUser = hotelUserService.getOne(new QueryWrapper<>(HotelUser.builder()
                .hotelId(dto.getHotelId())
                .memberUserId(userDto.getUId())
                .build()));
        ValidUtils.isNullThrow(hotelUser, "数据不存在");

        //用户下其他酒店设置为非默
        hotelUserService.update(HotelUser.builder()
                        .isDefault(false)
                        .build(),
                new QueryWrapper<>(HotelUser.builder()
                        .memberUserId(userDto.getUId())
                        .build()));

        //将指定的家设置为默认家
        hotelUserService.updateById(HotelUser.builder()
                .id(hotelUser.getId())
                .isDefault(true).build());

        //获取权限
        List<String> perms = hotelUserService.permissions(hotelUser.getIsMain(), hotelUser.getHotelId(), hotelUser.getMemberUserId());

        log.info("HOTELiD={}", hotelUser.getHotelId());
        //刷新token
        LoginUtils.fresh(UserDto.builder()
                .platform(PlatFormEnum.HOTEL.getCode())
                .uId(userDto.getUId())
                .account(userDto.getAccount())
                .perms(perms)
                .actualUserId(userDto.getActualUserId())
                .isMain(hotelUser.getIsMain())
                .hotelId(hotelUser.getHotelId())
                .build());

        return CommonResultVo.SUCCESS();
    }


    /**
     * 包间新增
     *
     * @param dto
     * @return
     */
    //@CustomPermissions("hotel_floor_home:add")
    @PostMapping("add")
    public CommonResultVo<Home> add(@RequestBody @Valid HotelFloorHomeAddDto dto) {

        HotelUser hotelUser = hotelUserService.getOne(new QueryWrapper<>(HotelUser.builder()
                .memberUserId(UserDto.getUser().getActualUserId())
                .isDefault(true).build()));


        return CommonResultVo.SUCCESS(bizHotelFloorHomeService.add(dto,
                hotelUser.getHotelId(),
                UserDto.getUser().getActualUserId()));
    }

    /**
     * 包间编辑
     */
    //@CustomPermissions("hotel_floor_home:edit")
    @PostMapping("edit")
    public CommonResultVo<Home> edit(@RequestBody @Valid HotelFloorHomeEditDto dto) {
        return CommonResultVo.SUCCESS(bizHotelFloorHomeService.edit(dto,
                UserDto.getUser().getHotelId(),
                UserDto.getUser().getActualUserId()));
    }

}