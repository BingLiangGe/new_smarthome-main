package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.vo.HotelDataVo;
import com.lj.iot.biz.base.vo.HotelUserPageVo;
import com.lj.iot.biz.db.smart.entity.HotelUser;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.common.base.dto.PageDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 酒店和用户关联表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface HotelUserMapper extends BaseMapper<HotelUser> {

    List<HotelDataVo> listHotel(@Param("hotelUserId") String hotelUserId);

    HotelDataVo defaultHotel(@Param("hotelUserId") String hotelUserId);

    IPage<HotelUserPageVo> customPage(IPage<HotelUserPageVo> page, @Param("params") PageDto pageDto, @Param("hotelId") Long hotelId);
}
