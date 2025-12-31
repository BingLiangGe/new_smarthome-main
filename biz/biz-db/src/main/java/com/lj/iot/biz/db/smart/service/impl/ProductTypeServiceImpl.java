package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.ProductType;
import com.lj.iot.biz.db.smart.mapper.ProductTypeMapper;
import com.lj.iot.biz.db.smart.service.IProductTypeService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.PageUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 领捷产品类别表 服务实现类
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class ProductTypeServiceImpl extends ServiceImpl<ProductTypeMapper, ProductType> implements IProductTypeService {

    @Override
    public IPage<ProductType> customPage(PageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }

    @Override
    public Map<String, String> getMapData() {
        return list().stream()
                .collect(Collectors.toMap(ProductType::getProductType, ProductType::getImagesUrl));
    }

    @Cacheable(value = "product-type", key = "#productType", unless = "#result == null")
    @Override
    public ProductType getCacheProductType(String productType) {
        return this.getOne(new QueryWrapper<>(ProductType.builder().productType(productType).build()));
    }

    @CacheEvict(value = "product-type", key = "#productType")
    @Override
    public void deleteCache(String productType) {
    }

    @Override
    public List<String> subTypeList(String productTypeRay) {
        return this.baseMapper.subTypeList(productTypeRay);
    }

    @Override
    public ProductType getTopProductType(String productTypeCode) {
        ProductType productType = this.getOne(new QueryWrapper<>(ProductType.builder().productType(productTypeCode).build()));
        if (productType.getParentId() != 0) {
            productType = this.getById(Long.valueOf(productType.getProductTypeRay().split("\\|")[1]));
        }
        return productType;
    }
}
