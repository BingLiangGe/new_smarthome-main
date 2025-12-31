package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.db.smart.entity.RfBrand;
import com.lj.iot.biz.db.smart.entity.RfBrandType;

import java.util.List;

/**
 * @author mz
 * @Date 2022/8/15
 * @since 1.0.0
 */
public interface BizRfBrandTypeService {

    List<RfBrandType> listByDeviceTypeId(Long deviceTypeId);

}
