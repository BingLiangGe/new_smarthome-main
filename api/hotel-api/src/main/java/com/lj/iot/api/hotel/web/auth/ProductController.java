package com.lj.iot.api.hotel.web.auth;

import com.lj.iot.biz.base.dto.ProductIdDto;
import com.lj.iot.biz.base.vo.ProductListVo;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.lj.iot.biz.service.BizProductService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 产品管理
 */
@RestController
@RequestMapping("api/auth/product")
public class ProductController {
    @Resource
    IProductService productService;

    @Autowired
    private BizProductService bizProductService;

    /**
     * 列表
     *
     * @return
     */
    @RequestMapping("list")
    public CommonResultVo<List<ProductListVo>> list() {
        return CommonResultVo.SUCCESS(bizProductService.hotelListProductListVo());
    }

    /**
     * 详情
     *
     * @return
     */
    @RequestMapping("info")
    public CommonResultVo<Product> info(ProductIdDto dto) {
        return CommonResultVo.SUCCESS(productService.getById(dto.getProductId()));
    }
}
