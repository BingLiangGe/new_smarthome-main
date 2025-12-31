package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.ProductIdPageDto;
import com.lj.iot.biz.base.dto.ProductModeAddDto;
import com.lj.iot.biz.base.dto.ProductModeEditDto;
import com.lj.iot.biz.db.smart.entity.ProductMode;
import com.lj.iot.biz.db.smart.service.IProductModeService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 产品模版管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/product_mode")
public class AuthProductModeController {
    @Autowired
    private IProductModeService productModeService;

    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("product:mode:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<ProductMode>> productTypePage(ProductIdPageDto pageDto) {
        return CommonResultVo.SUCCESS(productModeService.customPage(pageDto));
    }

    /**
     * 新增
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:mode:add")
    @PostMapping("/add")
    public CommonResultVo<String> add(@Valid ProductModeAddDto paramDto) {

        ValidUtils.noNullThrow(productModeService.getOne(new QueryWrapper<>(ProductMode.builder()
                .productId(paramDto.getProductId())
                .modeCode(paramDto.getModeCode())
                .build())), "同一产品模式代码不能重复");

        productModeService.save(ProductMode.builder()
                .productId(paramDto.getProductId())
                .modeCode(paramDto.getModeCode())
                .modeName(paramDto.getModeName())
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
    @CustomPermissions("product:mode:edit")
    @PostMapping("/edit")
    public CommonResultVo<String> edit(@Valid ProductModeEditDto paramDto) {

        ProductMode productMode = productModeService.getById(paramDto.getId());
        ValidUtils.isNullThrow(productMode, "数据不存在");
        ProductMode modeCodeData = productModeService.getOne(new QueryWrapper<>(ProductMode.builder()
                .productId(productMode.getProductId())
                .modeCode(paramDto.getModeCode())
                .build()));

        if (modeCodeData != null) {
            ValidUtils.isFalseThrow(paramDto.getId().compareTo(modeCodeData.getId()) == 0, "同一产品模式代码不能重复");
        }

        productModeService.updateById(ProductMode.builder()
                .id(paramDto.getId())
                .modeCode(paramDto.getModeCode())
                .modeName(paramDto.getModeName())
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
    @CustomPermissions("product:mode:delete")
    @PostMapping("/delete")
    public CommonResultVo<String> delete(@Valid IdDto paramDto) {

        ValidUtils.isNullThrow(productModeService.getById(paramDto.getId()), "数据不存在");
        productModeService.removeById(paramDto.getId());
        return CommonResultVo.SUCCESS();
    }
}
