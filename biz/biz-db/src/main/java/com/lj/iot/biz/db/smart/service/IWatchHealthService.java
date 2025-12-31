package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.base.vo.WatchChartsVo;
import com.lj.iot.biz.db.smart.entity.WatchHealth;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 手表健康数据 服务类
 * </p>
 *
 * @author tyj
 * @since 2023-10-25
 */
public interface IWatchHealthService extends IService<WatchHealth> {


    List<WatchChartsVo> selectChartData(String deviceId, String date, Integer type,Integer dataType);
}
