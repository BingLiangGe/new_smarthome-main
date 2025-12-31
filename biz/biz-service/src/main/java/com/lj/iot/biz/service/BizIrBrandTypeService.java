package com.lj.iot.biz.service;

import com.lj.iot.biz.db.smart.entity.IrBrandType;

import java.util.List;

/**
 * @author mz
 * @Date 2022/8/15
 * @since 1.0.0
 */
public interface BizIrBrandTypeService {

    List<IrBrandType> listByDeviceTypeId(Long deviceTypeId);
}
