package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.HotelLog;
import com.lj.iot.biz.db.smart.mapper.HotelLogMapper;
import com.lj.iot.biz.db.smart.service.IHotelLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 酒店日志 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@DS("smart")
@Service
public class HotelLogServiceImpl extends ServiceImpl<HotelLogMapper, HotelLog> implements IHotelLogService {

}
