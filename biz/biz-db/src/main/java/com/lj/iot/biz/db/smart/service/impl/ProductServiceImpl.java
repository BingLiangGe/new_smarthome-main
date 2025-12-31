package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.base.vo.ProductListItemVo;
import com.lj.iot.biz.base.vo.ProductVo;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;
import com.lj.iot.biz.db.smart.mapper.ProductMapper;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 领捷产品表 服务实现类
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {


    @Resource
    private ProductMapper productMapper;

    @Override
    public List<ProductListItemVo> getProductListItem(String type) {
        return productMapper.getProductListItem(type);
    }

    @Override
    public List<String> getProductSignType() {
        return productMapper.getProductSignType();
    }

    @Override
    public List<ProductVo> getProductList() {
        return productMapper.getProductList();
    }

    @Override
    public IPage<Product> customPage(ProductPageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }

    @Override
    public Product getOne(String productId, String signType) {
        return this.getOne(new QueryWrapper<>(Product.builder()
                .productId(productId)
                .signalType(signType)
                .build()));
    }

    @Override
    public List<Product> selectProductMaster() {
        return this.list(new QueryWrapper<>(Product.builder()
                .signalType("MASTER").build()));
    }
}
