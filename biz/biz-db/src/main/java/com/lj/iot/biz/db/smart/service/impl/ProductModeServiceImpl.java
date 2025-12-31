package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.ProductIdPageDto;
import com.lj.iot.biz.db.smart.entity.ProductMode;
import com.lj.iot.biz.db.smart.mapper.ProductModeMapper;
import com.lj.iot.biz.db.smart.service.IProductModeService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品模式 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-08-29
 */
@DS("smart")
@Service
public class ProductModeServiceImpl extends ServiceImpl<ProductModeMapper, ProductMode> implements IProductModeService {

    @Override
    public IPage<ProductMode> customPage(ProductIdPageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }
}
