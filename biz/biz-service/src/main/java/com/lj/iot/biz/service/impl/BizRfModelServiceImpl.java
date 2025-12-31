package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.base.dto.RfModelAddDto;
import com.lj.iot.biz.base.dto.RfModelEditDto;
import com.lj.iot.biz.db.smart.entity.RfBrandType;
import com.lj.iot.biz.db.smart.entity.RfModel;
import com.lj.iot.biz.db.smart.service.IRfBrandTypeService;
import com.lj.iot.biz.db.smart.service.IRfModelService;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.biz.service.BizRfModelService;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mz
 * @Date 2022/8/16
 * @since 1.0.0
 */
@Service
public class BizRfModelServiceImpl implements BizRfModelService {

    @Autowired
    private IRfModelService rfModelService;
    @Autowired
    private IRfBrandTypeService rfBrandTypeService;

    @Override
    public IPage<RfModel> page(PageDto pageDto) {
        return rfModelService.customPage(pageDto);
    }

    @Override
    public void add(RfModelAddDto paramDto) {
        RfBrandType rfBrandType = rfBrandTypeService.getOne(new QueryWrapper<>(RfBrandType.builder()
                .brandId(paramDto.getDeviceBrandId())
                .deviceTypeId(paramDto.getDeviceTypeId())
                .build()));
        ValidUtils.isNullThrow(rfBrandType, "设备类型和品牌不匹配");

        rfModelService.save(RfModel.builder()
                .brandId(rfBrandType.getBrandId())
                .brandName(rfBrandType.getBrandName())
                .sentCount(paramDto.getSentCount())
                .unitTime(paramDto.getUnitTime())
                .codeType(paramDto.getCodeType())
                .modelName(paramDto.getModelName())
                .deviceTypeId(paramDto.getDeviceTypeId())
                .headData(paramDto.getHeadData())
                .startZeroTime(paramDto.getStartZeroTime())
                .build());
    }

    @Override
    public void edit(RfModelEditDto paramDto) {

        RfBrandType rfBrandType = rfBrandTypeService.getOne(new QueryWrapper<>(RfBrandType.builder()
                .brandId(paramDto.getDeviceBrandId())
                .deviceTypeId(paramDto.getDeviceTypeId())
                .build()));
        ValidUtils.isNullThrow(rfBrandType, "设备类型和品牌不匹配");

        ValidUtils.isNullThrow(rfModelService.getById(paramDto.getId()), "数据不存在");

        rfModelService.updateById(RfModel.builder()
                .id(paramDto.getId())
                .brandId(rfBrandType.getBrandId())
                .brandName(rfBrandType.getBrandName())
                .sentCount(paramDto.getSentCount())
                .unitTime(paramDto.getUnitTime())
                .codeType(paramDto.getCodeType())
                .headData(paramDto.getHeadData())
                .startZeroTime(paramDto.getStartZeroTime())
                .build());
    }

    @Override
    public void delete(IdDto paramDto) {
        RfModel model = rfModelService.getById(paramDto.getId());
        ValidUtils.isNullThrow(model, "数据不存在");
        rfModelService.removeById(paramDto.getId());
    }
}
