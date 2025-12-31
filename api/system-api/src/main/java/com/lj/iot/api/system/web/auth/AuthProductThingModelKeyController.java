package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.ProductIdPageDto;
import com.lj.iot.biz.base.dto.ProductThingModelKeyAddDto;
import com.lj.iot.biz.base.dto.ProductThingModelKeyEditDto;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.db.smart.entity.ProductThingModelKey;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.lj.iot.biz.db.smart.service.IProductThingModelKeyService;
import com.lj.iot.biz.service.enums.ModeEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 产品按键管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/product_thing_model_key")
public class AuthProductThingModelKeyController {

    @Autowired
    private IProductService productService;

    @Autowired
    private IProductThingModelKeyService productThingModelKeyService;

    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("product:thing_model_key:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<ProductThingModelKey>> page(ProductIdPageDto pageDto) {
        return CommonResultVo.SUCCESS(productThingModelKeyService.customPage(pageDto));
    }

    /**
     * 新增
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:thing_model_key:add")
    @PostMapping("/add")
    public CommonResultVo<String> add(@Valid ProductThingModelKeyAddDto paramDto) {

        Product product = productService.getById(paramDto.getProductId());
        ValidUtils.isNullThrow(product, "产品不存在");

        ModeEnum modeEnum = ModeEnum.parse(paramDto.getMode());
        ValidUtils.isNullThrow(modeEnum, "模式不支持");

        productThingModelKeyService.save(ProductThingModelKey.builder()
                .productId(product.getProductId())
                .keyName(paramDto.getKeyName())
                .keyIdx(paramDto.getKeyIdx())
                .keyCode(paramDto.getKeyCode())
                .identifier(paramDto.getIdentifier())
                .mode(modeEnum.getCode())
                .step(paramDto.getStep())
                .build());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:thing_model_key:edit")
    @PostMapping("/edit")
    public CommonResultVo<String> edit(@Valid ProductThingModelKeyEditDto paramDto) {

        ProductThingModelKey productThingModelKey = productThingModelKeyService.getById(paramDto.getId());
        ValidUtils.isNullThrow(productThingModelKey, "数据不存在");
        Product product = productService.getById(paramDto.getProductId());
        ValidUtils.isNullThrow(product, "产品不存在");

        ModeEnum modeEnum = ModeEnum.parse(paramDto.getMode());
        ValidUtils.isNullThrow(modeEnum, "模式不支持");

        productThingModelKeyService.updateById(ProductThingModelKey.builder()
                .id(paramDto.getId())
                .productId(product.getProductId())
                .keyName(paramDto.getKeyName())
                .keyIdx(paramDto.getKeyIdx())
                .keyCode(paramDto.getKeyCode())
                .identifier(paramDto.getIdentifier())
                .mode(modeEnum.getCode())
                .step(paramDto.getStep())
                .build());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:thing_model_key:delete")
    @PostMapping("/delete")
    public CommonResultVo<String> delete(@Valid IdDto paramDto) {

        ValidUtils.isNullThrow(productThingModelKeyService.getById(paramDto.getId()), "数据不存在");
        productThingModelKeyService.removeById(paramDto.getId());
        return CommonResultVo.SUCCESS();
    }
}
