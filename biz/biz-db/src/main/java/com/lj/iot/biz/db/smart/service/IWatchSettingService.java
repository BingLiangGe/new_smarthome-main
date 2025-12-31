package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.base.vo.WatchSettingInfoVo;
import com.lj.iot.biz.db.smart.entity.WatchSetting;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tyj
 * @since 2023-10-25
 */
public interface IWatchSettingService extends IService<WatchSetting> {


    WatchSettingInfoVo getWathSettingByDeviceIdAndType(String deviceId, Integer dataType);
}
