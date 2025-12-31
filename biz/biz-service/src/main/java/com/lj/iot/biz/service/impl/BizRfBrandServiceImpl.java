package com.lj.iot.biz.service.impl;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.RfBrandAddDto;
import com.lj.iot.biz.base.dto.RfBrandEditDto;
import com.lj.iot.biz.db.smart.entity.RfBrand;
import com.lj.iot.biz.db.smart.entity.RfBrandType;
import com.lj.iot.biz.db.smart.service.IRfBrandService;
import com.lj.iot.biz.db.smart.service.IRfBrandTypeService;
import com.lj.iot.biz.service.BizRfBrandService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author mz
 * @Date 2022/8/15
 * @since 1.0.0
 */
@Service
public class BizRfBrandServiceImpl implements BizRfBrandService {

    @Autowired
    private IRfBrandService rfBrandService;
    @Autowired
    private IRfBrandTypeService rfBrandTypeService;

    @Override
    public IPage<RfBrand> page(PageDto pageDto) {
        return rfBrandService.page(PageUtil.page(pageDto), new QueryWrapper<RfBrand>()
                .like("brand_name", pageDto.getSearch()));
    }

    @Override
    public List<RfBrand> listByDeviceTypeId(Long deviceTypeId) {

        List<RfBrandType> rfBrandTypeList = rfBrandTypeService.list(new QueryWrapper<>(RfBrandType.builder()
                .deviceTypeId(deviceTypeId)
                .build()));

        if (rfBrandTypeList.size() == 0) {
            return new ArrayList<>();
        }
        Set<Long> ids = new HashSet<>();
        for (RfBrandType rfBrandType : rfBrandTypeList) {
            ids.add(rfBrandType.getBrandId());
        }
        return rfBrandService.listByIds(ids);
    }

    @DSTransactional
    @Override
    public void add(RfBrandAddDto paramDto) {
        RfBrand brand = RfBrand.builder()
                .brandName(paramDto.getBrandName())
                .build();
        long count = rfBrandService.count(new QueryWrapper<>(brand));
        ValidUtils.isFalseThrow(count == 0L, "该品牌名称已存在");
        rfBrandService.save(brand);
    }

    @DSTransactional
    @Override
    public void edit(RfBrandEditDto paramDto) {

        RfBrand brand = rfBrandService.getById(paramDto.getId());
        ValidUtils.isNullThrow(brand, "品牌不存在");

        long count = rfBrandService.count(new QueryWrapper<>(RfBrand.builder()
                .brandName(paramDto.getBrandName())
                .build()));
        ValidUtils.isFalseThrow(count == 0L, "该品牌名称已存在");

        rfBrandTypeService.update(RfBrandType.builder()
                .brandName(paramDto.getBrandName())
                .firstLetter(PinyinUtil.getFirstLetter(brand.getBrandName().substring(0, 1), "").toUpperCase())
                .build(), new QueryWrapper<>(RfBrandType.builder()
                .brandId(brand.getId())
                .build()));

        rfBrandService.updateById(RfBrand.builder()
                .id(brand.getId())
                .brandName(paramDto.getBrandName())
                .build());

    }

    @DSTransactional
    @Override
    public void delete(IdDto paramDto) {

        RfBrand brand = rfBrandService.getById(paramDto.getId());
        ValidUtils.isNullThrow(brand, "品牌不存在");

        rfBrandService.removeById(brand.getId());

        rfBrandTypeService.remove(new QueryWrapper<>(RfBrandType.builder()
                .brandId(brand.getId())
                .build()));
    }
}
