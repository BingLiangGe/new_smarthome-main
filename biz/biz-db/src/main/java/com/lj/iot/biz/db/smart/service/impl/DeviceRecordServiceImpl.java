package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.DeviceRecord;
import com.lj.iot.biz.db.smart.mapper.DeviceRecordMapper;
import com.lj.iot.biz.db.smart.service.IDeviceRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tyj
 * @since 2024-03-14
 */
@DS("smart")
@Service
public class DeviceRecordServiceImpl extends ServiceImpl<DeviceRecordMapper, DeviceRecord> implements IDeviceRecordService {

}
