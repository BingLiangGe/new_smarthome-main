package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.base.vo.ProductListItemVo;
import com.lj.iot.biz.base.vo.ProductVo;
import com.lj.iot.biz.db.smart.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;
import com.lj.iot.common.util.util.PageUtil;

import java.util.List;

/**
 * 
 * 领捷产品表 服务类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface IProductService extends IService<Product> {

    List<ProductListItemVo> getProductListItem(String type);

    List<String> getProductSignType();

    List<ProductVo> getProductList();


    IPage<Product> customPage(ProductPageDto pageDto);

    /**
     * 根据产品ID和设备类型获取产品对象
     * @param productId
     * @param signType
     * @return
     */
    Product getOne(String productId, String signType);


    List<Product> selectProductMaster();
}
