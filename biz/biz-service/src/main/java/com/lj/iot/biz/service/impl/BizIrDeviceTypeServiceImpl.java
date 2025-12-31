package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.IrDeviceType;
import com.lj.iot.biz.db.smart.service.IIrDeviceTypeService;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.biz.service.BizIrDeviceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BizIrDeviceTypeServiceImpl implements BizIrDeviceTypeService {

    @Autowired
    private IIrDeviceTypeService deviceTypeService;

    @Override
    public IPage<IrDeviceType> customPage(PageDto pageDto) {
        return deviceTypeService.page(PageUtil.page(pageDto));
    }
}
