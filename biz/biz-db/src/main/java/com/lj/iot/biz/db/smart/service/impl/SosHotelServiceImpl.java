package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.SosHotel;
import com.lj.iot.biz.db.smart.mapper.SosHotelMapper;
import com.lj.iot.biz.db.smart.service.ISosHotelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tyj
 * @since 2024-01-03
 */
@DS("smart")
@Service
public class SosHotelServiceImpl extends ServiceImpl<SosHotelMapper, SosHotel> implements ISosHotelService {

}
