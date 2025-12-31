package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.IrDeviceType;
import com.lj.iot.biz.db.smart.mapper.IrDeviceTypeMapper;
import com.lj.iot.biz.db.smart.service.IIrDeviceTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-09-20
 */
@DS("smart")
@Service
public class IrDeviceTypeServiceImpl extends ServiceImpl<IrDeviceTypeMapper, IrDeviceType> implements IIrDeviceTypeService {

}
