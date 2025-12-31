package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.ProductType;

import java.util.List;
import java.util.Map;

/**
 *
 * 领捷设产品别表 服务类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface IProductTypeService extends IService<ProductType> {

    IPage<ProductType> customPage(PageDto pageDto);

    /**
     * 获取产品类型与图片键值对
     * @return
     */
    Map<String, String> getMapData();

    ProductType getCacheProductType(String productType);

    void deleteCache(String productType);

    List<String> subTypeList(String productTypeRay);


    ProductType getTopProductType(String productType);
}
