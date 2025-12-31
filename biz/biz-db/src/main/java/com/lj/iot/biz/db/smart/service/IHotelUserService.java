package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.vo.HotelDataVo;
import com.lj.iot.biz.base.vo.HotelUserPageVo;
import com.lj.iot.biz.db.smart.entity.HotelUser;
import com.lj.iot.common.base.dto.PageDto;

import java.util.List;

/**
 * <p>
 * 酒店和用户关联表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface IHotelUserService extends IService<HotelUser> {


    List<String> permissions(Boolean isMain, Long hotelId, String hotelUserId);

    List<HotelDataVo> listHotel(String hotelUserId);

    HotelDataVo defaultHotel(String hotelUserId);


    IPage<HotelUserPageVo> customPage(PageDto dto, Long hotelId);
}
