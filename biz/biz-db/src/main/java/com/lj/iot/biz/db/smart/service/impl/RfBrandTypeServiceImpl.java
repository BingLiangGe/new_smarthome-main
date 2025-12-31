package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.RfBrandType;
import com.lj.iot.biz.db.smart.mapper.RfBrandTypeMapper;
import com.lj.iot.biz.db.smart.service.IRfBrandTypeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 射频产品和设备类型关联表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-08-15
 */
@DS("smart")
@Service
public class RfBrandTypeServiceImpl extends ServiceImpl<RfBrandTypeMapper, RfBrandType> implements IRfBrandTypeService {

}
