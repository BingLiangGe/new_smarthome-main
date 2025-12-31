package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.base.dto.ProductUpgradeAddDto;
import com.lj.iot.biz.base.dto.ProductUpgradeEditDto;
import com.lj.iot.biz.db.smart.entity.AppUpgrade;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;
import com.lj.iot.biz.db.smart.service.IAppUpgradeService;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.lj.iot.biz.db.smart.service.IProductUpgradeService;
import com.lj.iot.biz.service.BizAppUpgradeService;
import com.lj.iot.biz.service.BizProductUpgrade;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 产品升级管理
 *
 * @author mz
 * @Date 2022/7/21
 * @since 1.0.0
 */
@Service
public class BizAppUpgradeImpl implements BizAppUpgradeService {


    @Autowired
    private IProductService productService;
    @Autowired
    private IAppUpgradeService appUpgradeService;

    @Autowired
    private IProductUpgradeService productUpgradeService;

    @Override
    public IPage<AppUpgrade> customPage(PageDto pageDto) {
        return appUpgradeService.page(PageUtil.page(pageDto));
    }

    @Override
    public void add(AppUpgrade paramDto) {
        paramDto.setCreateTime(LocalDateTime.now());
        paramDto.setUpdateTime(LocalDateTime.now());
         appUpgradeService.save(paramDto);
    }

    @Override
    public void edit(AppUpgrade paramDto) {
        paramDto.setUpdateTime(LocalDateTime.now());
        appUpgradeService.updateById(paramDto);
    }

    @Override
    public void delete(AppUpgrade paramDto) {
        appUpgradeService.removeById(paramDto.getId());
    }

    @Override
    public AppUpgrade findByVersionCode(long versionCode,Integer type) {
        return appUpgradeService.findByVersionCode(versionCode,type);
    }

    @Override
    public AppUpgrade findMasterControlUrl(long versionCode) {
        return appUpgradeService.findMasterControlUrl(versionCode);
    }


}
