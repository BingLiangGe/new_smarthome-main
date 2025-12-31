package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.UserGoodsAddDto;
import com.lj.iot.biz.base.dto.UserGoodsEditDto;
import com.lj.iot.biz.base.vo.SeachDeviceVo;
import com.lj.iot.biz.db.smart.entity.UserGoods;
import com.lj.iot.common.base.dto.PageDto;

/**
 *
 */
public interface BizUserGoodsService {

    IPage<UserGoods> customPage(PageDto pageDto, String userId);

    IPage<UserGoods> customPage(PageDto pageDto, Long hotelId, String userId);

    IPage<SeachDeviceVo> customPageUserDevice(PageDto pageDto);

    void add(UserGoodsAddDto dto, String userId);

    void add(UserGoodsAddDto dto, Long hotelId, String userId);

    void edit(UserGoodsEditDto dto, String userId);

    void edit(UserGoodsEditDto dto, Long hotelId, String userId);

    void delete(IdDto dto, String userId);
}
