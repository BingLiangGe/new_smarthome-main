package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.base.vo.WatchChartsVo;
import com.lj.iot.biz.db.smart.entity.WatchHealth;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 手表健康数据 Mapper 接口
 * </p>
 *
 * @author tyj
 * @since 2023-10-25
 */
public interface WatchHealthMapper extends BaseMapper<WatchHealth> {


    List<WatchChartsVo> selectChartData(@Param("deviceId") String deviceId,@Param("date")  String date,
                                        @Param("type") Integer type, @Param("dataType") Integer dataType);
}
