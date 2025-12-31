package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.base.vo.DevicePageVo;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.base.dto.ProductUpgradeAddDto;
import com.lj.iot.biz.base.dto.ProductUpgradeEditDto;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.lj.iot.biz.db.smart.service.IProductUpgradeService;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.biz.service.BizProductUpgrade;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 产品升级管理
 *
 * @author mz
 * @Date 2022/7/21
 * @since 1.0.0
 */
@Service
public class BizProductUpgradeImpl implements BizProductUpgrade {

    @Autowired
    private IProductService productService;
    @Autowired
    private IProductUpgradeService productUpgradeService;

    @Override
    public IPage<ProductUpgrade> customPage(PageDto pageDto) {
        return productUpgradeService.page(PageUtil.page(pageDto));
    }

    @Override
    public void add(ProductUpgradeAddDto paramDto) {

        //查询产品是否存在
        Product product = productService.getById(paramDto.getProductId());
        ValidUtils.isNullThrow(product, "产品不存在");
        productUpgradeService.save(ProductUpgrade.builder()
                .productId(paramDto.getProductId())
                .versionUrl(paramDto.getVersionUrl())
                .oldVersion(paramDto.getOldVersion())
                .newVersion(paramDto.getNewVersion())
                .updePackageName(paramDto.getUpdePackageName())
                .updePackageDetails(paramDto.getUpdePackageDetails())
                .hardWareVersion(paramDto.getHardWareVersion())
                .build());
    }

    @Override
    public void edit(ProductUpgradeEditDto paramDto) {
        productUpgradeService.updateById(ProductUpgrade.builder()
                .id(paramDto.getId())
                .versionUrl(paramDto.getVersionUrl())
                .oldVersion(paramDto.getOldVersion())
                .newVersion(paramDto.getNewVersion())
                .updePackageName(paramDto.getUpdePackageName())
                .updePackageDetails(paramDto.getUpdePackageDetails())
                .hardWareVersion(paramDto.getHardWareVersion())
                .valid(paramDto.getValid())
                .build());
    }

    @Override
    public IPage<ProductUpgrade> newCustomPage(ProductPageDto pageDto) {
        IPage<ProductUpgrade> page = PageUtil.page(pageDto);
        return productUpgradeService.newPage(page,pageDto);

    }

    @Override
    public ProductUpgrade findByMaxId(String productId) {
        return productUpgradeService.findByMaxId(productId);
    }
}
