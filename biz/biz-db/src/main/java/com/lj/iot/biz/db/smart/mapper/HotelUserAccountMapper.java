package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.db.smart.entity.Hotel;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.common.util.util.PageUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 酒店用户账号表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface HotelUserAccountMapper extends BaseMapper<HotelUserAccount> {


    public List<Hotel> getHotelLimit(@Param("pageIndex") Integer pageIndex, @Param("pageSize")Integer pageSize,@Param("hotel") Hotel hotel);

    public Integer getHotelLimitCount(@Param("hotel") Hotel hotel);


    public List<HotelUserAccount> getHotelUserLimit(@Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize, @Param("userAccount") HotelUserAccount userAccount);

    public Integer getHotelUserLimitCount( @Param("userAccount") HotelUserAccount userAccount);

}
