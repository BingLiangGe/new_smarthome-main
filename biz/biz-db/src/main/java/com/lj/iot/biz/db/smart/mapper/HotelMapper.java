package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.db.smart.entity.Hotel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 酒店 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface HotelMapper extends BaseMapper<Hotel> {

    @Select("\n" +
            "SELECT h.`home_name` homeName,h.`id` homeId,0 `list` FROM `hotel_floor_home` hf\n" +
            "LEFT JOIN home h ON h.`id`=hf.`home_id`\n" +
            " WHERE hf.hotel_id=#{hotelId} AND hf.floor_id=#{floorId} AND h.`home_name`=#{homeName}")
    List<Map>  getHomeByInfo(Integer hotelId,Integer floorId,String homeName);

    List<Hotel> findHotelPageLimit(@Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize,
                                   @Param("hotel") Hotel hotel);


    Integer findHotelPageLimitCount(@Param("hotel") Hotel hotel);

    List<Hotel> customList(@Param("userId") String userId);

}
