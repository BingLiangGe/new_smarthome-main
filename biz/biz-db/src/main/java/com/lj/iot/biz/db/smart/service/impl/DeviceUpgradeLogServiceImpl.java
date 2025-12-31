package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.DeviceUpgradeLog;
import com.lj.iot.biz.db.smart.mapper.DeviceUpgradeLogMapper;
import com.lj.iot.biz.db.smart.service.IDeviceUpgradeLogService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * 设备升级日志表 服务实现类
 *
 * @author xm
 * @since 2022-07-21
 */
@DS("smart")
@Service
public class DeviceUpgradeLogServiceImpl extends ServiceImpl<DeviceUpgradeLogMapper, DeviceUpgradeLog> implements IDeviceUpgradeLogService {

    @Override
    public IPage<DeviceUpgradeLog> customPage(PageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }
}
