package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.util.util.PageUtil;

import java.util.List;

/**
 *
 * 产品升级表 服务类
 *
 *
 * @author xm
 * @since 2022-07-21
 */
public interface IProductUpgradeService extends IService<ProductUpgrade> {

    public PageUtil<ProductUpgrade> productUpgradePage(Integer pageIndex, Integer pageSize, ProductUpgrade productUpgrade);


    List<String> findUpgradeGroup();

    ProductUpgrade findNewUpgradeByProduct(String product_id,String hardWareVersion, String softWareVersion);

    IPage<ProductUpgrade> newPage(IPage<ProductUpgrade> pageDto,ProductPageDto pDto);

    ProductUpgrade findByMaxId(String productId);
}
