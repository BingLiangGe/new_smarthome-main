package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.RfDeviceType;
import com.lj.iot.biz.db.smart.mapper.RfDeviceTypeMapper;
import com.lj.iot.biz.db.smart.service.IRfDeviceTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * 射频设备类型表 服务实现类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class RfDeviceTypeServiceImpl extends ServiceImpl<RfDeviceTypeMapper, RfDeviceType> implements IRfDeviceTypeService {

}
