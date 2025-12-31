package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.lj.iot.biz.base.dto.LoginDto;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.db.smart.BizHotelUserAccountService;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.common.util.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BizHotelUserAccountServiceImpl implements BizHotelUserAccountService {

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;

    @Autowired
    private IHotelService hotelService;

    @Autowired
    private IHotelUserService hotelUserService;

    @Autowired
    private IUserAccountService userAccountService;

    @Resource
    ISceneService sceneService;

    @Resource
    ISceneTemplateService sceneTemplateService;

    @DSTransactional
    @Override
    public HotelUserAccount register(LoginDto loginDto) {
        String id = IdUtils.nextId();
        HotelUserAccount hotelUserAccount = HotelUserAccount.builder()
                .id(id)
                .actualUserId(id)
                .mobile(loginDto.getAccount())
                .nickname(loginDto.getAccount())
                .type(AccountTypeEnum.HOTEL.getCode())
                .build();
        hotelUserAccountService.save(hotelUserAccount);

        hotelUserAccount = hotelUserAccountService.getById(hotelUserAccount.getId());

        //创建一个酒店
        Hotel hotel = Hotel.builder()
                .hotelName("我的酒店")
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

        //创建家庭用户
        UserAccount user = UserAccount.builder()
                .id(hotelUserAccount.getId())
                .actualUserId(hotelUserAccount.getId())
                .mobile(hotelUserAccount.getMobile())
                .nickname(hotelUserAccount.getNickname())
                .type(AccountTypeEnum.HOTEL.getCode())
                .build();
        userAccountService.save(user);

        return hotelUserAccount;
    }
}
