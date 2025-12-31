package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.SosContactAddDto;
import com.lj.iot.biz.base.dto.SosContactEditDto;
import com.lj.iot.biz.base.enums.ContactTypeEnum;
import com.lj.iot.biz.base.vo.SosContactVo;
import com.lj.iot.biz.db.smart.entity.Home;
import com.lj.iot.biz.db.smart.entity.HotelFloorHome;
import com.lj.iot.biz.db.smart.entity.SosContact;
import com.lj.iot.biz.db.smart.entity.SosHotel;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/auth/sos")
public class SosController {

    @Resource
    ISosContactService sosContactService;

    @Resource
    IHomeService homeService;

    @Autowired
    private IHotelFloorHomeService hotelFloorHomeService;


    @Autowired
    private ISosHotelService sosHotelService;


    /**
     * 列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("list")
    @CustomPermissions("sos_contact:list")
    public CommonResultVo<List<SosHotel>> list(HomeIdDto dto) {
        List<SosHotel> list = sosHotelService.list(new QueryWrapper<SosHotel>(SosHotel.builder().hotelId(dto.getHomeId()).build()));

        return CommonResultVo.SUCCESS(list);
    }

    /**
     * 新增电话
     *
     * @param dto
     * @return
     */
    @PostMapping("add")
    @CustomPermissions("sos_contact:add")
    public CommonResultVo<SosContact> add(@RequestBody @Valid SosContactAddDto dto) {
        List<HotelFloorHome> list = hotelFloorHomeService.list(new QueryWrapper<>(HotelFloorHome.builder()
                .hotelId(dto.getHomeId()).build()));

        ValidUtils.isNullThrow(list, "酒店不存在");

        SosHotel dbSosHotel = SosHotel.builder()
                .hotelId(dto.getHomeId())
                .phoneNumber(dto.getPhoneNumber())
                .createTime(LocalDateTime.now())
                .username(dto.getUsername()).build();

        sosHotelService.save(dbSosHotel);

        for (HotelFloorHome hotelFloorHome : list
        ) {
            Home home = homeService.getOne(new QueryWrapper<>(Home.builder().id(hotelFloorHome.getHomeId()).build()));
            ValidUtils.isNullThrow(home, "数据不存在");
            SosContact sosContact = SosContact.builder().
                    userId(UserDto.getUser().getActualUserId())
                    .homeId(home.getId())
                    .contactType(ContactTypeEnum.property.getCode())
                    .hotelId(dto.getHomeId())
                    .phoneNumber(dto.getPhoneNumber())
                    .username(dto.getUsername())
                    .sosId(dbSosHotel.getId())
                    .build();
            sosContactService.save(sosContact);
        }

        return CommonResultVo.SUCCESS();
    }


    /**
     * 编辑电话
     *
     * @param dto
     * @return
     */
    @PostMapping("edit")
    @CustomPermissions("sos_contact:edit")
    public CommonResultVo<SosHotel> edit(@RequestBody @Valid SosContactEditDto dto) {

        SosHotel sosContact = sosHotelService.getById(dto.getId());

        ValidUtils.isNullThrow(sosContact, "数据不存在");

        sosContactService.remove(new QueryWrapper<>(SosContact.builder()
                .sosId(sosContact.getId()).build()));

        sosHotelService.updateById(SosHotel.builder()
                .phoneNumber(dto.getPhoneNumber())
                .username(dto.getUsername())
                .hotelId(sosContact.getHotelId())
                .id(sosContact.getId())
                .build());

        List<HotelFloorHome> list = hotelFloorHomeService.list(new QueryWrapper<>(HotelFloorHome.builder()
                .hotelId(sosContact.getHotelId()).build()));

        ValidUtils.isNullThrow(list, "酒店不存在");


        for (HotelFloorHome hotelFloorHome : list
        ) {
            Home home = homeService.getOne(new QueryWrapper<>(Home.builder().id(hotelFloorHome.getHomeId()).build()));
            ValidUtils.isNullThrow(home, "数据不存在");
            SosContact dbContact = SosContact.builder().
                    userId(UserDto.getUser().getActualUserId())
                    .homeId(home.getId())
                    .contactType(ContactTypeEnum.property.getCode())
                    .hotelId(sosContact.getHotelId())
                    .phoneNumber(dto.getPhoneNumber())
                    .username(dto.getUsername())
                    .sosId(sosContact.getId())
                    .build();
            sosContactService.save(dbContact);
        }

        return CommonResultVo.SUCCESS(sosHotelService.getById(sosContact.getId()));
    }

    /**
     * 删除
     *
     * @param dto
     * @return
     */
    @PostMapping("delete")
    @CustomPermissions("sos_contact:delete")
    public CommonResultVo<String> delete(@RequestBody @Valid IdDto dto) {
        SosHotel sosContact = sosHotelService.getById(dto.getId());
        ValidUtils.isNullThrow(sosContact, "数据不存在");

        sosHotelService.removeById(dto.getId());
        sosContactService.remove(new QueryWrapper<>(SosContact.builder()
                .sosId(sosContact.getId()).build()));
        return CommonResultVo.SUCCESS();
    }
}
