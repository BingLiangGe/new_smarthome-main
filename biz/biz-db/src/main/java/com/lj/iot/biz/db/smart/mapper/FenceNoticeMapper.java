package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.db.smart.entity.FenceNotice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tyj
 * @since 2023-10-23
 */
public interface FenceNoticeMapper extends BaseMapper<FenceNotice> {


    @Select("SELECT COUNT(1) number FROM fence_notice WHERE device_id=#{deviceId} AND TO_DAYS(create_time) = TO_DAYS(NOW()) and type=#{type};")
    Integer selectTodayNumber(@Param("deviceId") String deviceId,@Param("type") Integer type);
}
