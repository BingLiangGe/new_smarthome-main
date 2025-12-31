package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.vo.ProductList3326Vo;
import com.lj.iot.biz.base.vo.ProductListVo;
import com.lj.iot.biz.db.smart.entity.Product;

import java.util.List;

/**
 * 用户设备表
 */
public interface BizProductService {

    /**
     * 分页查询
     *
     * @param pageDto
     * @return
     */
    IPage<Product> customPage(ProductPageDto pageDto);

    /**
     * 新增
     *
     * @param paramDto
     */
    void add(ProductAddDto paramDto);

    /**
     * 编辑
     *
     * @param paramDto
     */
    void edit(ProductEditDto paramDto);

    /**
     * 删除
     *
     * @param paramDto
     */
    void delete(ProductIdDto paramDto);


    String CCCFDF(IdStrDto paramDto);


    List<ProductListVo> listProductListVo();


    List<ProductListVo> listProductListVo3326();

    List<ProductListVo> hotelListProductListVo();

}
