package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.base.vo.WatchSosVo;
import com.lj.iot.biz.db.smart.entity.WatchSos;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 手表sos记录 Mapper 接口
 * </p>
 *
 * @author tyj
 * @since 2023-10-25
 */
public interface WatchSosMapper extends BaseMapper<WatchSos> {


    @Select("SELECT sos_mobile daring_data,create_time FROM watch_sos WHERE device_id=#{deviceId} AND DATE_FORMAT(create_time, '%Y-%m-%d')=#{date} and sos_type=#{type} order by create_time desc")
    List<WatchSosVo> getSosList(@Param("deviceId") String deviceId, @Param("date")String date, @Param("type")Integer type);
}
