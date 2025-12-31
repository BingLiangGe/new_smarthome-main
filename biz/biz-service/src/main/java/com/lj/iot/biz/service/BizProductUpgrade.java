package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.base.dto.ProductUpgradeAddDto;
import com.lj.iot.biz.base.dto.ProductUpgradeEditDto;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;

/**
 * 产品升级
 *
 * @author mz
 * @Date 2022/7/21
 * @since 1.0.0
 */
public interface BizProductUpgrade {

    IPage<ProductUpgrade> customPage(PageDto pageDto);

    void add(ProductUpgradeAddDto paramDto);

    void edit(ProductUpgradeEditDto paramDto);

    IPage<ProductUpgrade> newCustomPage(ProductPageDto pageDto);

    ProductUpgrade findByMaxId(String productId);
}
