package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductIdPageDto;
import com.lj.iot.biz.db.smart.entity.ProductInvented;
import com.lj.iot.biz.db.smart.mapper.ProductInventedMapper;
import com.lj.iot.biz.db.smart.service.IProductInventedService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品分裂虚设备表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-08-29
 */
@DS("smart")
@Service
public class ProductInventedServiceImpl extends ServiceImpl<ProductInventedMapper, ProductInvented> implements IProductInventedService {

    @Override
    public IPage<ProductInvented> customPage(ProductIdPageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto),pageDto);
    }
}
