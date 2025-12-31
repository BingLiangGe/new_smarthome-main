package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductIdPageDto;
import com.lj.iot.biz.db.smart.entity.ProductMode;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 产品模式 服务类
 * </p>
 *
 * @author xm
 * @since 2022-08-29
 */
public interface IProductModeService extends IService<ProductMode> {

    IPage<ProductMode> customPage(ProductIdPageDto pageDto);

}
