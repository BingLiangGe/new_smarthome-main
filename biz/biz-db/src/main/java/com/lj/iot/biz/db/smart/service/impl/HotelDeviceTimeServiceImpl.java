package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.HotelDeviceTime;
import com.lj.iot.biz.db.smart.mapper.HotelDeviceTimeMapper;
import com.lj.iot.biz.db.smart.service.IHotelDeviceTimeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备续费时间 服务实现类
 * </p>
 *
 * @author xm
 * @since 2023-04-08
 */
@DS("smart")
@Service
public class HotelDeviceTimeServiceImpl extends ServiceImpl<HotelDeviceTimeMapper, HotelDeviceTime> implements IHotelDeviceTimeService {

}
