package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.HotelUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 酒店用户与角色对应关系 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface HotelUserRoleMapper extends BaseMapper<HotelUserRole> {

    List<Long> queryRoleIdList(@Param("hotelUserId") String hotelUserId, @Param("hotelId") Long hotelId);
}
