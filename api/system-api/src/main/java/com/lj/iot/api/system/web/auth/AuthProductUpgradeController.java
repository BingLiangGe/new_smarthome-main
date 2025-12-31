package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.base.dto.ProductUpgradeAddDto;
import com.lj.iot.biz.base.dto.ProductUpgradeEditDto;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;
import com.lj.iot.biz.service.BizProductUpgrade;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 产品升级管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/product_upgrade")
public class AuthProductUpgradeController {

    @Autowired
    private BizProductUpgrade bizProductUpgrade;

    /**
     * 升级包列表
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("product:upgrade:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<ProductUpgrade>> productPage(ProductPageDto pageDto) {
        return CommonResultVo.SUCCESS(bizProductUpgrade.customPage(pageDto));
    }



    /**
     * 新升级包列表
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("product:upgrade:page")
    @RequestMapping("/newPage")
    public CommonResultVo<IPage<ProductUpgrade>> newProductPage(ProductPageDto pageDto) {
        return CommonResultVo.SUCCESS(bizProductUpgrade.newCustomPage(pageDto));
    }

    /**
     * 新增产品升级数据
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:upgrade:add")
    @PostMapping("/add")
    public CommonResultVo<String> productAdd(@Valid ProductUpgradeAddDto paramDto) {
        bizProductUpgrade.add(paramDto);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑产品升级数据
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:upgrade:edit")
    @PostMapping("/edit")
    public CommonResultVo<String> productEdit(@Valid ProductUpgradeEditDto paramDto) {
        bizProductUpgrade.edit(paramDto);
        return CommonResultVo.SUCCESS();
    }
}
