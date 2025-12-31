package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.DeviceGroup;
import com.lj.iot.biz.db.smart.mapper.DeviceGroupMapper;
import com.lj.iot.biz.db.smart.service.IDeviceGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xm
 * @since 2023-03-11
 */
@DS("smart")
@Service
public class DeviceGroupServiceImpl extends ServiceImpl<DeviceGroupMapper, DeviceGroup> implements IDeviceGroupService {

}
