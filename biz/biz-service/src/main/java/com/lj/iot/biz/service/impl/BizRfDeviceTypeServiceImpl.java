package com.lj.iot.biz.service.impl;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.RfDeviceTypeAddDto;
import com.lj.iot.biz.base.dto.RfDeviceTypeEditDto;
import com.lj.iot.biz.db.smart.entity.RfBrand;
import com.lj.iot.biz.db.smart.entity.RfBrandType;
import com.lj.iot.biz.db.smart.entity.RfDeviceType;
import com.lj.iot.biz.db.smart.service.IRfBrandService;
import com.lj.iot.biz.db.smart.service.IRfBrandTypeService;
import com.lj.iot.biz.db.smart.service.IRfDeviceTypeService;
import com.lj.iot.biz.service.BizRfDeviceTypeService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mz
 * @Date 2022/8/15
 * @since 1.0.0
 */
@Service
public class BizRfDeviceTypeServiceImpl implements BizRfDeviceTypeService {

    @Autowired
    private IRfBrandService rfBrandService;
    @Autowired
    private IRfDeviceTypeService rfDeviceTypeService;
    @Autowired
    private IRfBrandTypeService rfBrandTypeService;

    @Override
    public IPage<RfDeviceType> page(PageDto pageDto) {
        IPage<RfDeviceType> page = rfDeviceTypeService.page(PageUtil.page(pageDto)
                , new QueryWrapper<RfDeviceType>()
                        .like("device_name", pageDto.getSearch()));
        List<RfDeviceType> rfDeviceTypeList = page.getRecords();
        for (RfDeviceType rfDeviceType : rfDeviceTypeList) {
            List<RfBrandType> list = rfBrandTypeService.list(new QueryWrapper<>(RfBrandType.builder()
                    .deviceTypeId(rfDeviceType.getId())
                    .build()));
            for (RfBrandType rfBrandType : list) {
                if(rfDeviceType.getBrandIds()==null){
                    rfDeviceType.setBrandIds(new ArrayList<>());
                    rfDeviceType.setBrandNames(new ArrayList<>());
                }
                rfDeviceType.getBrandIds().add(rfBrandType.getBrandId());
                rfDeviceType.getBrandNames().add(rfBrandType.getBrandName());
            }
        }
        return page;
    }

    @Override
    public List<RfDeviceType> listByBrandId(Long brandId) {

        List<RfDeviceType> deviceTypeList = rfDeviceTypeService.list();


        List<RfBrandType> rfBrandTypeList = rfBrandTypeService.list(new QueryWrapper<>(RfBrandType.builder()
                .brandId(brandId)
                .build()));

        if (rfBrandTypeList.size() == 0) {
            return deviceTypeList;
        }

        Set<Long> ids = new HashSet<>();
        for (RfBrandType rfBrandType : rfBrandTypeList) {
            ids.add(rfBrandType.getDeviceTypeId());
        }

        for (RfDeviceType rfDeviceType : deviceTypeList) {
            if (ids.contains(rfDeviceType.getId())) {
                rfDeviceType.setCheck(true);
            }
        }
        return deviceTypeList;
    }

    @DSTransactional
    @Override
    public void add(RfDeviceTypeAddDto paramDto) {

        long count = rfDeviceTypeService.count(new QueryWrapper<>(RfDeviceType.builder()
                .deviceName(paramDto.getTypeName())
                .build()));

        ValidUtils.isFalseThrow(count == 0, "已存在该名字，请查证");

        RfDeviceType deviceType = RfDeviceType.builder()
                .deviceName(paramDto.getTypeName())
                .build();
        rfDeviceTypeService.save(deviceType);

        if (paramDto.getBrandIds() != null) {
            for (Long brandId : paramDto.getBrandIds()) {
                RfBrand brand = rfBrandService.getById(brandId);
                ValidUtils.isNullThrow(brand, "品牌不存在");

                rfBrandTypeService.save(RfBrandType.builder()
                        .brandId(brand.getId())
                        .brandName(brand.getBrandName())
                        .firstLetter(PinyinUtil.getFirstLetter(brand.getBrandName().substring(0, 1), "").toUpperCase())
                        .deviceTypeId(deviceType.getId())
                        .build());
            }
        }
    }

    @DSTransactional
    @Override
    public void edit(RfDeviceTypeEditDto paramDto) {

        RfDeviceType deviceType = rfDeviceTypeService.getById(paramDto.getId());
        ValidUtils.isNullThrow(deviceType, "设备类型不存在");

        rfDeviceTypeService.updateById(RfDeviceType.builder()
                .id(deviceType.getId())
                .deviceName(paramDto.getTypeName())
                .build());
        if (paramDto.getBrandIds() != null) {
            rfBrandTypeService.remove(new QueryWrapper<>(RfBrandType.builder()
                    .deviceTypeId(deviceType.getId())
                    .build()));
            for (Long brandId : paramDto.getBrandIds()) {
                RfBrand brand = rfBrandService.getById(brandId);
                ValidUtils.isNullThrow(brand, "品牌不存在");
                rfBrandTypeService.save(RfBrandType.builder()
                        .brandId(brand.getId())
                        .brandName(brand.getBrandName())
                        .firstLetter(PinyinUtil.getFirstLetter(brand.getBrandName().substring(0, 1), "").toUpperCase())
                        .deviceTypeId(deviceType.getId())
                        .build());
            }
        }
    }

    @DSTransactional
    @Override
    public void delete(IdDto paramDto) {

        RfDeviceType deviceType = rfDeviceTypeService.getById(paramDto.getId());
        ValidUtils.isNullThrow(deviceType, "设备类型不存在");

        rfDeviceTypeService.removeById(deviceType.getId());

        rfBrandTypeService.remove(new QueryWrapper<>(RfBrandType.builder()
                .deviceTypeId(deviceType.getId())
                .build()));
    }
}
