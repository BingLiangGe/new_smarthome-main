package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.RfBrandType;
import com.lj.iot.biz.db.smart.service.IRfBrandTypeService;
import com.lj.iot.biz.service.BizRfBrandTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BizRfBrandTypeServiceImpl implements BizRfBrandTypeService {

    @Autowired
    private IRfBrandTypeService rfBrandTypeService;

    @Override
    public List<RfBrandType> listByDeviceTypeId(Long deviceTypeId) {
        return rfBrandTypeService.list(new QueryWrapper<>(RfBrandType.builder()
                .deviceTypeId(deviceTypeId)
                .build()));
    }
}
