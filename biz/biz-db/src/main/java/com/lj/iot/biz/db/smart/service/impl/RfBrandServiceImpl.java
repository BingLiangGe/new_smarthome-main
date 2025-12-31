package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.RfBrand;
import com.lj.iot.biz.db.smart.mapper.RfBrandMapper;
import com.lj.iot.biz.db.smart.service.IRfBrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * 射频设备品牌表 服务实现类
 * 
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class RfBrandServiceImpl extends ServiceImpl<RfBrandMapper, RfBrand> implements IRfBrandService {

    @Override
    public List<RfBrand> listByTypeId(Long rfDeviceTypeId) {
        return this.baseMapper.findBrandListByTypeId(rfDeviceTypeId);
    }
}
