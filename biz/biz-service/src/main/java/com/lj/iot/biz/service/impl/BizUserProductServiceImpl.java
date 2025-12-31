package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.service.BizUserProductService;
import org.springframework.stereotype.Service;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Service
public class BizUserProductServiceImpl implements BizUserProductService {

    /**
     * 查询
     * @param pageDto
     * @return
     */
    @Override
    public IPage<UserDevice> customPage(ProductPageDto pageDto) {
        return null;
    }





}
