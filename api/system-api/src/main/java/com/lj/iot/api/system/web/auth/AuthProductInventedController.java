package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.ProductIdPageDto;
import com.lj.iot.biz.base.dto.ProductInventedAddDto;
import com.lj.iot.biz.base.dto.ProductInventedEditDto;
import com.lj.iot.biz.db.smart.entity.ProductInvented;
import com.lj.iot.biz.db.smart.entity.ProductType;
import com.lj.iot.biz.db.smart.service.IProductInventedService;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.lj.iot.biz.db.smart.service.IProductTypeService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 产品虚设备管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/product_invented")
public class AuthProductInventedController {

    @Autowired
    private IProductInventedService productInventedService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IProductTypeService productTypeService;

    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("product:invented:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<ProductInvented>> page(ProductIdPageDto pageDto) {
        return CommonResultVo.SUCCESS(productInventedService.customPage(pageDto));
    }

    /**
     * 新增
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:invented:add")
    @PostMapping("/add")
    public CommonResultVo<String> add(@Valid ProductInventedAddDto paramDto) {


        ValidUtils.isNullThrow(productService.getById(paramDto.getProductId()), "产品不存在");
        ProductType productType = productTypeService.getOne(new QueryWrapper<>(ProductType.builder()
                .id(paramDto.getProductTypeId())
                .build()));
        ValidUtils.isNullThrow(productType, "产品类型不存在");

        productInventedService.save(ProductInvented.builder()
                .productId(paramDto.getProductId())
                .productType(productType.getProductType())
                .thingModel(paramDto.getThingModel())
                .build());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:invented:edit")
    @PostMapping("/edit")
    public CommonResultVo<String> edit(@Valid ProductInventedEditDto paramDto) {

        ProductInvented productInvented = productInventedService.getById(paramDto.getId());
        ValidUtils.isNullThrow(productInvented, "数据不存在");
        ProductType productType = productTypeService.getById(paramDto.getProductTypeId());
        ValidUtils.isNullThrow(productType, "产品类型不存在");

        productInventedService.updateById(ProductInvented.builder()
                .id(paramDto.getId())
                .productType(productType.getProductType())
                .thingModel(paramDto.getThingModel())
                .build());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:invented:delete")
    @PostMapping("/delete")
    public CommonResultVo<String> delete(@Valid IdDto paramDto) {

        ValidUtils.isNullThrow(productInventedService.getById(paramDto.getId()), "数据不存在");
        productInventedService.removeById(paramDto.getId());
        return CommonResultVo.SUCCESS();
    }
}
