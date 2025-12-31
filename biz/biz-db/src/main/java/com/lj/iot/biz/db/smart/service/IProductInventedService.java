package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductIdPageDto;
import com.lj.iot.biz.db.smart.entity.ProductInvented;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.ProductMode;

/**
 * <p>
 * 产品分裂虚设备表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-08-29
 */
public interface IProductInventedService extends IService<ProductInvented> {

    IPage<ProductInvented> customPage(ProductIdPageDto pageDto);

}
