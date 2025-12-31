package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.vo.DevicePageVo;
import com.lj.iot.biz.db.smart.entity.DeviceUpgradeLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.base.dto.PageDto;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 设备升级日志表 服务类
 *
 *
 * @author xm
 * @since 2022-07-21
 */
public interface IDeviceUpgradeLogService extends IService<DeviceUpgradeLog> {

    IPage<DeviceUpgradeLog> customPage(PageDto pageDto);
}
