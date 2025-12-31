package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.ProductTypeAddDto;
import com.lj.iot.biz.base.dto.ProductTypeEditDto;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.db.smart.entity.ProductType;
import com.lj.iot.biz.db.smart.service.IProductTypeService;
import com.lj.iot.biz.service.BizUploadEntityService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.util.ValidUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 产品类型管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/product_type")
public class AuthProductTypeController {

    @Autowired
    private IProductTypeService productTypeService;

    @Autowired
    private BizUploadEntityService bizUploadEntityService;

    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("product:type:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<ProductType>> productTypePage(PageDto pageDto) {
        return CommonResultVo.SUCCESS(productTypeService.customPage(pageDto));
    }

    /**
     * 新增
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:type:add")
    @PostMapping("/add")
    public CommonResultVo<String> productTypeAdd(@Valid ProductTypeAddDto paramDto) {

        ValidUtils.noNullThrow(productTypeService.getOne(new QueryWrapper<>(ProductType.builder()
                .productType(paramDto.getProductType())
                .build())), "设备类型代码重复");

        ProductType productType = ProductType.builder()
                .productType(paramDto.getProductType())
                .productTypeName(paramDto.getProductTypeName())
                .imagesUrl(paramDto.getImagesUrl())
                .build();

        if (StringUtils.isNotBlank(paramDto.getParentId())) {
            if(!paramDto.getParentId().equals("0")){
                ProductType parentProductType = productTypeService.getById(paramDto.getParentId());
                ValidUtils.isNullThrow(parentProductType, "上级数据不存在");
                productType.setParentId(parentProductType.getId());
                productType.setProductTypeRay(parentProductType.getProductTypeRay() + parentProductType.getId() + "|");
            }else {
                productType.setParentId(0L);
                productType.setProductTypeRay("0|");
            }

        }

        productTypeService.save(productType);

        bizUploadEntityService.uploadEntityAppLevel(DynamicEntitiesNameEnum.DeviceName);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:type:edit")
    @PostMapping("/edit")
    public CommonResultVo<String> productTypeEdit(@Valid ProductTypeEditDto paramDto) {

        ProductType db = productTypeService.getById(paramDto.getId());
        ValidUtils.isNullThrow(db, "设备类型不存在");

        //清除缓存
        productTypeService.deleteCache(db.getProductType());

        productTypeService.updateById(ProductType.builder()
                .id(paramDto.getId())
                .productType(paramDto.getProductType())
                .productTypeName(paramDto.getProductTypeName())
                .imagesUrl(paramDto.getImagesUrl())
                .build());

        bizUploadEntityService.uploadEntityAppLevel(DynamicEntitiesNameEnum.DeviceName);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除
     *
     * @param paramDto
     * @return
     */
    @CustomPermissions("product:type:delete")
    @PostMapping("/delete")
    public CommonResultVo<String> productTypeDel(@Valid IdDto paramDto) {
        ProductType db = productTypeService.getById(paramDto.getId());
        ValidUtils.isNullThrow(db, "设备类型不存在");

        //清除缓存
        productTypeService.deleteCache(db.getProductType());

        productTypeService.removeById(paramDto.getId());

        bizUploadEntityService.uploadEntityAppLevel(DynamicEntitiesNameEnum.DeviceName);
        return CommonResultVo.SUCCESS();
    }
}
