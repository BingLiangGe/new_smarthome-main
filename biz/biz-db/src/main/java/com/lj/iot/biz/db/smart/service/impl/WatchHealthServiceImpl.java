package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.base.vo.WatchChartsVo;
import com.lj.iot.biz.db.smart.entity.WatchHealth;
import com.lj.iot.biz.db.smart.mapper.WatchHealthMapper;
import com.lj.iot.biz.db.smart.service.IWatchHealthService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 手表健康数据 服务实现类
 * </p>
 *
 * @author tyj
 * @since 2023-10-25
 */
@DS("smart")
@Service
public class WatchHealthServiceImpl extends ServiceImpl<WatchHealthMapper, WatchHealth> implements IWatchHealthService {

    @Resource
    private WatchHealthMapper mapper;


    @Override
    public List<WatchChartsVo> selectChartData(String deviceId, String date, Integer type,Integer dataType) {
        return mapper.selectChartData(deviceId,date,type,dataType);
    }
}
