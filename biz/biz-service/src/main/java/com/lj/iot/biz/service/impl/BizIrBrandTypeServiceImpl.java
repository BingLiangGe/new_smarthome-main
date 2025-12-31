package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.IrBrandType;
import com.lj.iot.biz.db.smart.service.IIrBrandTypeService;
import com.lj.iot.biz.service.BizIrBrandTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BizIrBrandTypeServiceImpl implements BizIrBrandTypeService {

    @Autowired
    private IIrBrandTypeService irBrandTypeService;

    @Override
    public List<IrBrandType> listByDeviceTypeId(Long deviceTypeId) {
        return irBrandTypeService.list(new QueryWrapper<>(IrBrandType.builder()
                .deviceTypeId(deviceTypeId)
                .build()));
    }

}
