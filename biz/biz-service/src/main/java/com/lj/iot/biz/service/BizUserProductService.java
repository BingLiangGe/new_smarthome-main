package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.db.smart.entity.UserDevice;

/**
 * 产品表
 */
public interface BizUserProductService {

    IPage<UserDevice> customPage(ProductPageDto pageDto);
}
