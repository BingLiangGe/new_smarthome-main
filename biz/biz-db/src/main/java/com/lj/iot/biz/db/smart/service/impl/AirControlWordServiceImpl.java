package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.AirControlWord;
import com.lj.iot.biz.db.smart.mapper.AirControlWordMapper;
import com.lj.iot.biz.db.smart.service.IAirControlWordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xm
 * @since 2023-04-11
 */
@DS("smart")
@Service
public class AirControlWordServiceImpl extends ServiceImpl<AirControlWordMapper, AirControlWord> implements IAirControlWordService {

}
