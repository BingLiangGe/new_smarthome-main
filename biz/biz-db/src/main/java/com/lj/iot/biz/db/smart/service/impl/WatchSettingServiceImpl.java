package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.base.vo.WatchSettingInfoVo;
import com.lj.iot.biz.db.smart.entity.WatchSetting;
import com.lj.iot.biz.db.smart.mapper.WatchSettingMapper;
import com.lj.iot.biz.db.smart.service.IWatchSettingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tyj
 * @since 2023-10-25
 */
@DS("smart")
@Service
public class WatchSettingServiceImpl extends ServiceImpl<WatchSettingMapper, WatchSetting> implements IWatchSettingService {

    @Resource
    private WatchSettingMapper mapper;


    @Override
    public WatchSettingInfoVo getWathSettingByDeviceIdAndType(String deviceId, Integer dataType) {
        return mapper.getWathSettingByDeviceIdAndType(deviceId,dataType);
    }
}
