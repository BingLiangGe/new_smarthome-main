package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.db.smart.entity.DeviceUpgradeLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.common.base.dto.PageDto;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 设备升级日志表 Mapper 接口
 * 
 *
 * @author xm
 * @since 2022-07-21
 */
public interface DeviceUpgradeLogMapper extends BaseMapper<DeviceUpgradeLog> {

    IPage<DeviceUpgradeLog> customPage(IPage<DeviceUpgradeLog> page, @Param("params") PageDto pageDto);

}
