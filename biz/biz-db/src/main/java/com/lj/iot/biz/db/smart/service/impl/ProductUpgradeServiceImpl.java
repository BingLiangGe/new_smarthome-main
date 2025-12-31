package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.base.vo.DevicePageVo;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;
import com.lj.iot.biz.db.smart.mapper.ProductUpgradeMapper;
import com.lj.iot.biz.db.smart.service.IProductUpgradeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 产品升级表 服务实现类
 *
 * @author xm
 * @since 2022-07-21
 */
@DS("smart")
@Service
public class ProductUpgradeServiceImpl extends ServiceImpl<ProductUpgradeMapper, ProductUpgrade> implements IProductUpgradeService {

    @Resource
    private ProductUpgradeMapper mapper;

    @Override
    public com.lj.iot.common.util.util.PageUtil<ProductUpgrade> productUpgradePage(Integer pageIndex, Integer pageSize, ProductUpgrade productUpgrade) {
        com.lj.iot.common.util.util.PageUtil<ProductUpgrade> pageUtil = new com.lj.iot.common.util.util.PageUtil();

        pageUtil.setRows(mapper.productUpgradePageLimit(pageIndex,pageSize,productUpgrade));
        pageUtil.setTotal(mapper.productUpgradePageLimitCount(productUpgrade));

        return pageUtil;
    }

    @Override
    public List<String> findUpgradeGroup() {
        return mapper.findUpgradeGroup();
    }

    @Override
    public ProductUpgrade findNewUpgradeByProduct(String product_id,String hardWareVersion, String softWareVersion) {
        return mapper.findNewUpgradeByProduct(product_id,hardWareVersion,softWareVersion);
    }

    @Override
    public IPage<ProductUpgrade> newPage(IPage<ProductUpgrade> pageDto, ProductPageDto pDto) {
        IPage<ProductUpgrade> page = PageUtil.page(pDto);
        return this.baseMapper.newPage(pageDto, pDto);
    }

    @Override
    public ProductUpgrade findByMaxId(String productId) {
        return this.baseMapper.findByMaxId(productId);
    }
}
