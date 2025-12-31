package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.db.smart.entity.HotelRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.UserGoods;
import com.lj.iot.common.base.dto.PageDto;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 酒店角色 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface HotelRoleMapper extends BaseMapper<HotelRole> {

    IPage<HotelRole> customPage(IPage<UserGoods> page, @Param("params") PageDto pageDto, @Param("hotelId") Long hotelId);

}
