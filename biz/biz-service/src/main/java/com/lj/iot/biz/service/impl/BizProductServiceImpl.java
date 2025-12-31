package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.base.vo.ProductList3326Vo;
import com.lj.iot.biz.base.vo.ProductListItemVo;
import com.lj.iot.biz.base.vo.ProductListVo;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.db.smart.entity.ProductType;
import com.lj.iot.biz.db.smart.entity.RfDeviceType;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.lj.iot.biz.db.smart.service.IProductTypeService;
import com.lj.iot.biz.db.smart.service.IRfDeviceTypeService;
import com.lj.iot.biz.service.BizProductService;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.ValidUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mz
 * @Date 2022/7/20
 * @since 1.0.0
 */
@Service
public class BizProductServiceImpl implements BizProductService {

    @Autowired
    private IProductService productService;

    @Autowired
    private IProductTypeService productTypeService;

    @Autowired
    private IRfDeviceTypeService rfDeviceTypeService;


    @Override
    public IPage<Product> customPage(ProductPageDto pageDto) {

        return productService.customPage(pageDto);
    }

    @Caching(evict = {@CacheEvict(value = "common-cache", key = "'List<ProductListVo>'")})
    @Override
    public void add(ProductAddDto paramDto) {

        ProductType productType = productTypeService.getOne(new QueryWrapper<>(ProductType.builder()
                .id(paramDto.getProductTypeId())
                .build()), true);
        ValidUtils.isNullThrow(productType, "产品类型不存在");

        SignalEnum signalEnum = SignalEnum.parse(paramDto.getSignalType());
        ValidUtils.isNullThrow(signalEnum, "不支持该种信号类型");

//        ValidUtils.noNullThrow(productService.getOne(new QueryWrapper<>(Product.builder()
//                .productCode(paramDto.getProductCode())
//                .build())), "产品代码已存在");

        Long relationDeviceTypeId = null;

        //射频和红外要关联对应设备类型表,因为红外射频需要维护信号码
        switch (signalEnum) {
            case IR:
                ValidUtils.isNullThrow(paramDto.getRelationDeviceTypeId(), "关联对应的设备类型不存在");
                relationDeviceTypeId = paramDto.getRelationDeviceTypeId();
                break;
            case RF:
                ValidUtils.isNullThrow(paramDto.getRelationDeviceTypeId(), "设备类型不能为空");
                RfDeviceType rfDeviceType = rfDeviceTypeService.getById(paramDto.getRelationDeviceTypeId());
                ValidUtils.isNullThrow(rfDeviceType, "关联对应的设备类型不存在");
                relationDeviceTypeId = rfDeviceType.getId();
                break;
            default:
        }

        productService.save(Product.builder()
                .productId(IdUtils.hexId())
                .productCCCFDF(IdUtils.uuid())
                .productCode(paramDto.getProductCode())
                .controlProductId(paramDto.getControlProductId())
                .productName(paramDto.getProductName())
                .thingModel(paramDto.getThingModel())
                .imagesUrl(paramDto.getImagesUrl())
                .relationDeviceTypeId(relationDeviceTypeId)
                .signalType(paramDto.getSignalType())
                .productType(productType.getProductType())
                .build());
    }

    @Caching(evict = {@CacheEvict(value = "common-cache", key = "'List<ProductListVo>'")})

    @Override
    public void edit(ProductEditDto paramDto) {

        Product product = productService.getOne(new QueryWrapper<>(Product.builder()
                .productId(paramDto.getProductId())
                .build()));
        ValidUtils.isNullThrow(product, "产品不存在");

        ProductType productType = productTypeService.getOne(new QueryWrapper<>(ProductType.builder()
                .id(paramDto.getProductTypeId())
                .build()), true);
        ValidUtils.isNullThrow(productType, "产品类型不存在");

        productService.update(Product.builder()
                .productCCCFDF(IdUtils.uuid())
                .productCode(paramDto.getProductCode())
                .productName(paramDto.getProductName())
                .controlProductId(product.getControlProductId())
                .thingModel(paramDto.getThingModel())
                .imagesUrl(paramDto.getImagesUrl())
                .relationDeviceTypeId(paramDto.getRelationDeviceTypeId())
                .signalType(paramDto.getSignalType())
                .productType(productType.getProductType())
                .build(), new QueryWrapper<>(Product.builder()
                .productId(product.getProductId())
                .build()));
    }

    @Caching(evict = {@CacheEvict(value = "common-cache", key = "'List<ProductListVo>'")})
    @Override
    public void delete(ProductIdDto paramDto) {
        Product product = productService.getById(paramDto.getProductId());
        ValidUtils.isNullThrow(product, "产品不存在，请查证后再试");
        productService.removeById(paramDto.getProductId());
    }

    @Override
    public String CCCFDF(IdStrDto paramDto) {
        Product product = productService.getOne(new QueryWrapper<>(Product.builder()
                .productId(paramDto.getId())
                .build()));
        ValidUtils.isNullThrow(product, "产品不存在，请查证后再试");
        return product.getProductCCCFDF();
    }

    //@Cacheable(value = "common-cache", key = "'List<ProductListVo>'", unless = "#result == null")
    @Override
    public List<ProductListVo> listProductListVo() {
        /*
        todo 旧接口逻辑
        List<Product> productList = productService.list(new QueryWrapper<>(Product.builder().isAppShow(0).build()));
        productList = productList.stream().sorted(Comparator.comparing(Product::getCreateTime)).collect(Collectors.toList());
        Map<String, ProductListVo> productListVoMap = new HashMap<>();
        for (Product it : productList) {
            String type = StringUtils.isNotEmpty(it.getProductCode()) ? it.getProductCode() : it.getSignalType();
            productListVoMap.computeIfAbsent(type, k -> ProductListVo.builder()
                    .type(type)
                    .data(new ArrayList<>())
                    .build());
            productListVoMap.get(type).getData().add(ProductListItemVo.builder()
                    .productId(it.getProductId())
                    .productName(it.getProductName())
                    .productType(it.getProductType())
                    .signalType(it.getSignalType())
                    .imagesUrl(it.getImagesUrl())
                    .relationDeviceTypeId(it.getRelationDeviceTypeId())
                    .build());
        }
        //把mesh下面的插卡取电去掉
        *//*List<ProductListItemVo> mesh = productListVoMap.get("MESH").getData().stream()
                .filter(d -> !Objects.equals(d.getProductId(), "10724821")).
                collect(Collectors.toList());
        productListVoMap.get("MESH").setData(mesh);*//*
        return new ArrayList<>(productListVoMap.values());*/

        List<String> signTypes = productService.getProductSignType();

        signTypes.set(0, "MASTER");
        signTypes.set(1, "IR");

        List<ProductListVo> data = Lists.newArrayList();
        for (String type : signTypes
        ) {
            data.add(ProductListVo.builder()
                    .type(type)
                    .data(productService.getProductListItem(type))
                    .build());
        }
        return data;
    }

    @Override
    public List<ProductListVo> listProductListVo3326() {

        List<String> signTypes = productService.getProductSignType();


        for (int i = 0; i < signTypes.size(); i++) {
            if ("MASTER".equals(signTypes.get(i)) || "TCP".equals(signTypes.get(i))) {
                signTypes.remove(i);
            }
        }



        List<ProductListVo> data = Lists.newArrayList();
        for (String type : signTypes
        ) {

            List<ProductListItemVo> list = productService.getProductListItem(type);

            list.removeIf(n -> "gate_lock".equals(n.getProductType()) || "bed".equals(n.getProductType()));
            data.add(ProductListVo.builder()
                    .type(type)
                    .data(list)
                    .build());
        }
        return data;
    }


    //@Cacheable(value = "common-cache", key = "'List<ProductListVo>'", unless = "#result == null")
    @Override
    public List<ProductListVo> hotelListProductListVo() {
        /* todo 旧逻辑
        List<Product> productList = productService.list(new QueryWrapper<>(Product.builder().isHotelShow(0).build()));
        productList = productList.stream().sorted(Comparator.comparing(Product::getCreateTime)).collect(Collectors.toList());
        Map<String, ProductListVo> productListVoMap = new HashMap<>();
        for (Product it : productList) {
            String type = StringUtils.isNotEmpty(it.getProductCode()) ? it.getProductCode() : it.getSignalType();
            productListVoMap.computeIfAbsent(type, k -> ProductListVo.builder()
                    .type(type)
                    .data(new ArrayList<>())
                    .build());
            productListVoMap.get(type).getData().add(ProductListItemVo.builder()
                    .productId(it.getProductId())
                    .productName(it.getProductName())
                    .productType(it.getProductType())
                    .signalType(it.getSignalType())
                    .imagesUrl(it.getImagesUrl())
                    .relationDeviceTypeId(it.getRelationDeviceTypeId())
                    .build());
        }*/

        List<String> signTypes = productService.getProductSignType();

        signTypes.set(0, "MESH");
        signTypes.set(1, "MASTER");
        signTypes.set(2, "IR");

        List<ProductListVo> data = Lists.newArrayList();
        for (String type : signTypes
        ) {
            data.add(ProductListVo.builder()
                    .type(type)
                    .data(productService.getProductListItem(type))
                    .build());
        }
        return data;
    }

}
